package org.openwilma.kotlin.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.openwilma.kotlin.OpenWilma
import org.openwilma.kotlin.classes.lessonnotes.TimeRange
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class WilmaLessonNoteParser {
    companion object {
        fun parseWilmaLessonNotes(htmlDocument: String) {
            val jsoupDocument = Jsoup.parse(htmlDocument)

            val table = jsoupDocument.getElementsByClass("attendance-single").first()

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

                        val decimalRegex = "[0-9]*[.]?[0-9]+".toRegex()
                        decimalRegex.find(spacingConfig.attr("style"))?.let {width ->
                            val decimal = width.value.toDouble()
                            startMinutes +=
                                ((decimal / OpenWilma.lessonNoteFullHourWidth) * 60).roundToInt()
                        }

                        if (span < colSpan+1 && lastIndex < tableSpacingConfigs.count()-2) {
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

                        val localStartTime: LocalTime= LocalTime.MIN.plusMinutes(startMinutes.toLong())
                        val localEndTime: LocalTime= LocalTime.MIN.plusMinutes(endMinutes.toLong())
                        timeRanges.add(TimeRange(localStartTime, localEndTime))
                    }
                    //timeHours.add(TimeHour(num, timeRanges))
                    lastIndex += 1+colSpan
                }

                // Parse lesson notes
                if (tableRows != null) {
                    for (note in tableRows) {
                        val tableData = note.getElementsByTag("td")
                        val date = tableData[1].text()
                        println(date)
                        val notices = tableData.lastOrNull()?.text()
                        tableData.removeFirst()
                        tableData.removeFirst()
                        tableData.removeLast()
                        tableData.removeLast()
                        var spanCounter = 0
                        for (event in tableData) {
                            val span = event.attr("colspan").toIntOrNull() ?: 1
                            if (event.hasClass("event")) {
                                val title = event.attr("title")
                                val teacherCode = event.text()
                                val start = timeRanges[spanCounter-1]
                                val end = timeRanges[spanCounter+span-1]
                                val duration = ChronoUnit.MINUTES.between(start.start, end.start)
                                println("$title,: $teacherCode,: ${start.start} - ${end.start} duration: $duration min")
                            }
                            spanCounter += span
                        }
                    }
                }
            }
        }
    }
}