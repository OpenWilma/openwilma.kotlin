package org.openwilma.kotlin.parsers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.helger.commons.collection.impl.ICommonsList
import com.helger.css.ECSSVersion
import com.helger.css.decl.CSSExpressionMemberTermSimple
import com.helger.css.decl.CSSSelectorSimpleMember
import com.helger.css.decl.CSSStyleRule
import com.helger.css.reader.CSSReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.lessonnotes.LessonNote
import org.openwilma.kotlin.classes.lessonnotes.TimeRange
import org.openwilma.kotlin.classes.misc.CSSResource
import org.openwilma.kotlin.utils.LocalDateGSONAdapter
import org.openwilma.kotlin.utils.LocalDateTimeGSONAdapter
import org.openwilma.kotlin.utils.LocalTimeGSONAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.roundToInt


class WilmaLessonNoteParser {
    companion object {
        fun parseWilmaLessonNotes(htmlDocument: String): List<LessonNote> {
            val jsoupDocument = Jsoup.parse(htmlDocument)

            val decimalRegex = "[0-9]*[.]?[0-9]+".toRegex()

            val table = jsoupDocument.getElementsByClass("attendance-single").first()

            val cssStyling = jsoupDocument.getElementsByAttributeValue("type", "text/css")
                .firstOrNull { item -> item.tagName() == "style" }

            val bgColors: HashMap<String, String> = HashMap()
            val foregroundColors: HashMap<String, String> = HashMap()

            // Parsing colors from CSS rules
            if (cssStyling != null) {
                val styleSheet = CSSReader.readFromString(cssStyling.data().replace("<!--", "").replace("-->", ""), ECSSVersion.LATEST)
                val items: ICommonsList<CSSStyleRule>? = styleSheet?.allStyleRules

                val cssResources: MutableList<CSSResource> = mutableListOf()
                if (items != null) {
                    for (rule in items) {
                        val classes: MutableList<String> = ArrayList()
                        for (selector in rule.allSelectors) {
                            for (member in selector.allMembers) {
                                if (member is CSSSelectorSimpleMember) {
                                    if (member.isClass) {
                                        classes.add(member.value.drop(1))
                                    }
                                }
                            }
                        }
                        val cssResource = CSSResource(classes)
                        for (declaration in rule.allDeclarations) {
                            for (icssExpressionMembers in declaration.expression.allMembers) {
                                val expressionMemberTermSimple = icssExpressionMembers as CSSExpressionMemberTermSimple
                                cssResource.cssParams[declaration.property] = expressionMemberTermSimple.value
                            }
                        }
                        if (cssResource.cssParams.containsKey("background") || cssResource.cssParams
                                .containsKey("background-color")
                        ) cssResources.add(cssResource)
                    }
                }
                for (cssResource in cssResources) {
                    for (trayItemId in cssResource.typeIds) {
                        if (cssResource.cssParams.containsKey("background")) {
                            bgColors[trayItemId] =
                                cssResource.cssParams["background"] as String
                        } else if (cssResource.cssParams
                                .containsKey("background-color")) {
                            bgColors[trayItemId] =
                                cssResource.cssParams["background-color"] as String
                        }

                        if (cssResource.cssParams.containsKey("color")) {
                            foregroundColors[trayItemId] =
                                cssResource.cssParams["color"] as String
                        }
                    }
                }
            }

            val lessonNotes: MutableList<LessonNote> = mutableListOf()
            if (table != null) {
                val tableSpacingConfigs = table.getElementsByAttributeValueStarting("style", "width :")
                val tableTimeConfigs = table.getElementsByTag("th")
                val tableRows = table.getElementsByTag("tbody").first()?.getElementsByTag("tr")

                // Create time ranges for lesson note slots
                var lastIndex = 0
                val timeRanges: MutableList<TimeRange> = mutableListOf()
                tableTimeConfigs.removeFirst()
                tableTimeConfigs.removeLast()
                tableTimeConfigs.removeLast()
                for (timeConfig in tableTimeConfigs) {
                    val num = timeConfig.text().toInt()
                    val colSpan: Int = (timeConfig.attr("colspan").toIntOrNull() ?: 1)-1
                    var startMinutes = num*60
                    var endMinutes = num*60
                    for (span in 0..colSpan) {
                        val spacingConfig: Element = tableSpacingConfigs[lastIndex+span]

                        decimalRegex.find(spacingConfig.attr("style"))?.let {width ->
                            val decimal = width.value.toDouble()
                            if (decimal != 1.13) {
                                startMinutes +=
                                    ((decimal / OpenWilma.lessonNoteFullHourWidth) * 60).roundToInt()
                            }
                        }

                        if (span < colSpan+1 && lastIndex+span+1 < tableSpacingConfigs.count()) {
                            decimalRegex.find(tableSpacingConfigs[lastIndex+span+1].attr("style"))?.let {width ->
                                val decimal = width.value.toDouble()
                                if (endMinutes < startMinutes) {
                                    endMinutes += startMinutes-endMinutes
                                }
                                endMinutes +=
                                    ((decimal / OpenWilma.lessonNoteFullHourWidth) * 60).roundToInt()
                            }
                        }

                        if (startMinutes > endMinutes) {
                            continue
                        }


                        val localStartTime: LocalTime = LocalTime.MIN.plusMinutes(startMinutes.toLong())
                        val localEndTime: LocalTime = LocalTime.MIN.plusMinutes(endMinutes.toLong())
                        timeRanges.add(TimeRange(localStartTime, localEndTime))
                    }
                    lastIndex += 1+colSpan
                }

                // Parse lesson notes
                if (tableRows != null) {
                    var lastDate: LocalDate? = null;
                    for (note in tableRows) {
                        val tableData = note.getElementsByTag("td")
                        lateinit var localDate: LocalDate
                        if (tableData[1].text().isBlank() && lastDate != null) {
                            localDate = lastDate
                        } else {
                            localDate = LocalDate.parse(tableData[1].text(), DateTimeFormatter.ofPattern("d.M.yyyy", Locale.getDefault()))
                            lastDate = localDate
                        }
                        // Parse notices
                        val noticeTexts: List<String>? = tableData.lastOrNull()?.getElementsByTag("small")?.map {
                            it.getElementsByClass("lem").remove()
                            it.text()
                        }
                        val noticesMap: HashMap<String, String> = hashMapOf()
                        noticeTexts?.forEach {
                            noticesMap[it.split(": ").first()] = it.split(": ").last()
                        }

                        tableData.removeFirst()
                        tableData.removeFirst()
                        tableData.removeLast()
                        tableData.removeLast()
                        var spanCounter = 0
                        var eventCounter = 0
                        for (event in tableData) {
                            val span = event.attr("colspan").toIntOrNull() ?: 1
                            if (event.hasClass("event")) {
                                // Course info
                                val courseCode: String? = if (event.attr("title").contains(";")) event.attr("title").split(";").first() else null

                                // Teacher info
                                val teacherFullName = event.attr("title").split(" - ").first().split("/").lastOrNull()
                                val typeIdClass = event.classNames().find {it.startsWith("at-tp") && it.replace("at-tp", "").toIntOrNull() != null}

                                // Disc name, comments and teacher code
                                val discName = event.getElementsByTag("small").first()?.text()
                                event.getElementsByTag("small").forEach { it.remove() }
                                var comments: String? = null
                                if (event.getElementsByTag("sup").first() != null && noticesMap.contains(event.getElementsByTag("sup").first()!!.text().trim())) {
                                    comments = noticesMap[event.getElementsByTag("sup").first()!!.text().trim()]
                                    event.getElementsByTag("sup").first()?.remove()
                                }
                                val teacherCode: String = event.text()

                                // Clarification record
                                var clarificationMaker: String? = null
                                if (event.attr("title").contains(" - ") && event.attr("title").split("/").count() > 2) {
                                    clarificationMaker = event.attr("title").split("/").lastOrNull()?.split(" - ")?.last()?.split("/")?.last()
                                }

                                // Colors and names
                                val typeDesc = jsoupDocument.getElementsByClass("$typeIdClass text-center").first()?.parent()
                                val codeName: String? = typeDesc?.children()?.first()?.text()
                                val fullName: String? = typeDesc?.children()?.last()?.text()
                                val bgColor: String? = bgColors.getOrDefault(typeIdClass, null);
                                val fgColor: String? = foregroundColors.getOrDefault(typeIdClass, null);

                                // Duration
                                var start = timeRanges[abs(spanCounter-1)]
                                var end: TimeRange
                                if (spanCounter+span-1 != timeRanges.count()) {
                                    end = timeRanges[spanCounter+span-1]
                                } else if (eventCounter == 0) {
                                    start = timeRanges[2]
                                    end = timeRanges[3]
                                } else {
                                    start = timeRanges[spanCounter-2]
                                    end = timeRanges[spanCounter-1]
                                }
                                var duration = ChronoUnit.MINUTES.between(start.start, end.start)
                                lateinit var startDate: LocalDateTime
                                lateinit var endDate: LocalDateTime

                                if (duration < 0) {
                                    startDate = LocalDateTime.of(localDate, end.start)
                                    endDate = LocalDateTime.of(localDate, start.start)
                                    duration = ChronoUnit.MINUTES.between(end.start, start.start)
                                } else {
                                    startDate = LocalDateTime.of(localDate, start.start)
                                    endDate = LocalDateTime.of(localDate, end.start)
                                }



                                // Course name not available in attendance/view
                                lessonNotes.add(LessonNote(codeName, fullName, discName, courseCode, null, teacherCode, teacherFullName, comments, bgColor, fgColor, startDate, endDate, duration.toInt(), clarificationMaker ))
                                eventCounter += 1
                            }
                            spanCounter += span
                        }
                    }
                }
            }
            return lessonNotes
        }
    }
}