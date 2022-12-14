package org.openwilma.kotlin.parsers

import org.jsoup.Jsoup
import org.openwilma.kotlin.classes.announcements.Announcement
import org.openwilma.kotlin.enums.UserType
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class WilmaAnnouncementsParser {
    companion object {
        private val dateFormatWithTime: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm")
        private val dateFormat: SimpleDateFormat = SimpleDateFormat("d.M.yyyy")
        private val creatorRegex = "\\((.*?)\\)".toRegex()


        fun parseAnnouncements(htmlDocument: String): List<Announcement> {
            val jsoupDocument = Jsoup.parse(htmlDocument)
            val announcementItems = jsoupDocument.getElementsByClass("left").first()?.getElementsByClass("panel-body")?.first()?.children()

            val announcements: MutableList<Announcement> = mutableListOf()

            if (announcementItems != null) {
                var currentDate:Date?  = null
                for (element in announcementItems) {
                    if (element.tagName() == "h2") {
                        // Title
                        currentDate = if (element.text().split(".").last().isBlank()) {
                            // autofill current year
                            dateFormat.parse("${element.text().trim()}${Calendar.getInstance().get(Calendar.YEAR)}")
                        } else if (element.text().lowercase() == "tänään") {
                            Date()
                        } else if (element.text().lowercase() == "eilen") {
                            Date.from(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                        } else {
                            dateFormat.parse(element.text().trim())
                        }
                    } else if (element.tagName() == "div" && element.hasClass("well") && currentDate != null) {
                        val title = element.getElementsByTag("h3").first()!!.text()
                        val description = element.getElementsByClass("sub-text").first()?.wholeText()
                        // If news id is not present, user is unable to view its full content.
                        val newsId = element.getElementsByTag("a").first()?.attr("href")?.split("/")?.last()?.trim()?.toInt() ?: -1
                        val linkContainer = element.getElementsByClass("horizontal-link-container small").first()
                        val creatorElement = linkContainer?.getElementsByAttribute("title")?.first()
                        val creatorCode: String? = creatorElement?.text()
                        val creatorName: String? = creatorElement?.attr("title") ?: creatorRegex.find(linkContainer?.text() ?: "")?.value
                        var creatorId: Int? = null
                        var creatorType: UserType? = null
                        val important = element.getElementsByClass("vismaicon-info").isNotEmpty()
                        val permanent = element.getElementsByClass("vismaicon-locked").isNotEmpty()

                        if (creatorElement?.tagName() == "a") {
                            // Profile link
                            creatorType = UserType.fromString(creatorElement.attr("href").split("profiles/").last().split("/").first().dropLast(1))
                            creatorId = creatorElement.attr("href").split("/").last().toIntOrNull()
                        }
                        announcements.add(Announcement(newsId, title, description, null, creatorName, creatorCode, creatorId, creatorType, currentDate, important, permanent))
                    }
                }
            }

            return announcements
        }

        fun parseAnnouncement(htmlDocument: String): Announcement? {
            val jsoupDocument = Jsoup.parse(htmlDocument)
            val card = jsoupDocument.getElementsByClass("panel-body").first() ?: return null
            val newsId = jsoupDocument.getElementsByAttributeValue("target", "preview").first()?.attr("href")?.split("/")?.last()?.trim()?.toInt() ?: return null
            val newsContent = card.getElementById("news-content")?.html()
            val title = card.getElementsByTag("h2").first()!!.text()
            val createDateElement = card.getElementsByClass("small semi-bold").first()
            val timestamp: Date? = if (createDateElement != null && createDateElement.text().split(".").last().isBlank()) {
                dateFormat.parse("${createDateElement.text().trim()}${Calendar.getInstance().get(Calendar.YEAR)}")
            } else if (createDateElement != null) {
                dateFormat.parse(createDateElement.text().trim())
            } else {
                null
            }

            val description = if (card.getElementById("news-content")?.previousElementSibling()?.tagName() == "p") {
                card.getElementById("news-content")?.previousElementSibling()?.text()
            } else {
                null
            }

            val creatorBox = card.getElementsByClass("horizontal-link-container").first()
            val creatorName: String? = creatorRegex.find(creatorBox?.children()?.get(1)?.text() ?: "")?.value?.replace("[()]".toRegex(), "") ?: creatorBox?.children()?.get(1)?.text()?.replace("[()]".toRegex(), "")
            val creatorCode: String? = creatorBox?.children()?.get(1)?.text()?.split("(")?.first()?.trim()
            var creatorId: Int? = null
            var creatorType: UserType? = null
            if (creatorBox?.children()?.get(1)?.tagName() == "a") {
                // Profile link
                creatorType = UserType.fromString(creatorBox.children()[1].attr("href").split("profiles/").last().split("/").first().dropLast(1))
                creatorId = creatorBox.children()[1].attr("href").split("/").last().toIntOrNull()
            }

            return Announcement(newsId, title, description, newsContent, creatorName, creatorCode, creatorId, creatorType, timestamp)
        }
    }
}