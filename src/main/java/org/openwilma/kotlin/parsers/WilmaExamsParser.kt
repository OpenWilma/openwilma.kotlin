package org.openwilma.kotlin.parsers

import org.jsoup.Jsoup
import org.openwilma.kotlin.classes.exams.Exam
import org.openwilma.kotlin.classes.people.WilmaTeacher
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WilmaExamsParser {
    companion object {
        private val dateFormatWithTime: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm")
        private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        private val dateRegex = "[0-9]+\\.[0-9]+\\.[0-9]+".toRegex()
        private val dateTimeRegex = "([0-9]+\\.[0-9]+\\.[0-9]+).*(([0-1][0-9]|2[0-3]):[0-5][0-9])".toRegex()


        fun parseUpcomingExams(htmlDocument: String): List<Exam> {
            val jsoupDocument = Jsoup.parse(htmlDocument)

            val examTables = jsoupDocument.getElementsByClass("table table-grey")

            val exams: MutableList<Exam> = mutableListOf()

            for (examTable in examTables) {
                val dataPoints = examTable.getElementsByTag("td")

                val dateAndTime = dataPoints.find {it.hasClass("col-lg-3 col-md-3") && it.getElementsByClass("badge").isEmpty()}
                val titleSplit = dateAndTime?.nextElementSibling()?.text()?.split(" : ")
                val creators: List<WilmaTeacher> = examTable.getElementsByClass("ope").map {WilmaTeacher(
                    primusId = it.attr("href").split("/").lastOrNull()?.toIntOrNull(),
                    codeName = it.text().split("(").first(),
                    fullName = it.text().split("(").last().split(")").first()
                )}
                val details = dataPoints.lastOrNull()

                var timestamp: LocalDateTime? = null
                if (dateTimeRegex.find(dateAndTime?.text()?.lowercase() ?: "") != null) {
                    val regexResult = dateTimeRegex.find(dateAndTime!!.text().lowercase())
                    timestamp = LocalDateTime.from(dateFormatWithTime.parse("${regexResult?.groups?.get(1)?.value} ${regexResult?.groups?.get(2)?.value}"))
                } else if (dateRegex.find(dateAndTime?.text()?.lowercase() ?: "") != null) {
                    timestamp = LocalDateTime.of(LocalDate.from(dateFormat.parse(dateRegex.find(dateAndTime!!.text().lowercase())?.value)), LocalTime.of(0, 0,0))
                }

                var subject: String? = null
                var courseCode: String? = null
                var courseName: String? = null
                var grade: String? = null
                var verbalGrade: String? = null

                if (titleSplit?.count() == 3) {
                    subject = titleSplit[0]
                    courseCode = titleSplit[1].trim()
                    courseName = titleSplit[2]
                } else if (titleSplit?.count() == 2) {
                    courseCode = titleSplit[0].trim()
                    courseName = titleSplit[1]
                }

                if (dataPoints.count() > 4) {
                    val teacherField = examTable.getElementsByClass("profile-link").first()?.parent()?.parent()
                    if (dataPoints.count() > 5) {
                        grade = teacherField?.nextElementSibling()?.getElementsByTag("td")?.text()
                        verbalGrade = teacherField?.nextElementSibling()?.nextElementSibling()?.getElementsByTag("td")?.text()
                    } else {
                        grade = teacherField?.nextElementSibling()?.text()
                    }
                }

                exams.add(Exam(timestamp, null, creators, courseCode, courseName, subject, details?.wholeText(), grade, verbalGrade))
            }
            return exams
        }


        fun parsePastExams(htmlDocument: String): List<Exam> {
            val jsoupDocument = Jsoup.parse(htmlDocument)

            val examTable = jsoupDocument.getElementById("examtable")

            val exams: MutableList<Exam> = mutableListOf()

            val examRows = examTable?.getElementsByTag("tbody")?.first()?.getElementsByTag("tr")
            if (examRows != null) {
                for (examRow in examRows) {
                    val dataPoints = examRow.getElementsByTag("td")

                    var seenTimestamp: LocalDateTime? = null

                    if (dataPoints.count() > 6) {
                        // This is maybe guardian which has seen field
                        val seenDateTime = dataPoints.firstOrNull()
                        if (dateRegex.find(seenDateTime?.text()?.lowercase() ?: "") != null) {
                            seenTimestamp = LocalDateTime.of(LocalDate.from(dateFormat.parse(dateRegex.find(seenDateTime!!.text().lowercase())?.value)), LocalTime.of(0, 0,0))
                        }
                        // Remove item to not mess with the normal order
                        dataPoints.removeAt(0)
                    }

                    val dateAndTime = dataPoints.firstOrNull()
                    val titleSplit = dataPoints[2].text().split(" : ")
                    val creators: List<WilmaTeacher> = examRow.getElementsByClass("profile-link").map {WilmaTeacher(
                        primusId = it.attr("href").split("/").lastOrNull()?.toIntOrNull(),
                        codeName = it.text(),
                        fullName = it.attr("title")
                    )}
                    val details = dataPoints.lastOrNull()?.previousElementSibling()?.previousElementSibling()

                    var timestamp: LocalDateTime? = null
                    if (dateRegex.find(dateAndTime?.text()?.lowercase() ?: "") != null) {
                        timestamp = LocalDateTime.of(LocalDate.from(dateFormat.parse(dateRegex.find(dateAndTime!!.text().lowercase())?.value)), LocalTime.of(0, 0,0))
                    }

                    var subject: String? = null
                    var courseCode: String? = null
                    var courseName: String? = null
                    val grade: String? = dataPoints.lastOrNull()?.previousElementSibling()?.text()?.ifBlank { null }
                    val verbalGrade: String? = dataPoints.lastOrNull()?.text()?.ifBlank { null }

                    if (titleSplit.count() == 3) {
                        subject = titleSplit[0].ifBlank { null }
                        courseCode = titleSplit[1].ifBlank { null }
                        courseName = titleSplit[2].ifBlank { null }
                    } else if (titleSplit.count() == 2) {
                        courseCode = titleSplit[0].ifBlank { null }
                        courseName = titleSplit[1].ifBlank { null }
                    }

                    exams.add(Exam(timestamp, seenTimestamp, creators, courseCode, courseName, subject, details?.wholeText()?.ifBlank { null }, grade, verbalGrade))
                }
            }
            return exams
        }
    }
}