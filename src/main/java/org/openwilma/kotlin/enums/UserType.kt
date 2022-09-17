package org.openwilma.kotlin.enums

import com.google.gson.annotations.SerializedName


enum class UserType(val userType: Int, val userTypeString: String) {
    @SerializedName("teacher")
    TEACHER(1, "teacher"),
    @SerializedName("student")
    STUDENT(2, "student"),
    @SerializedName("personnel")
    STAFF(3, "personnel"),
    @SerializedName("guardian")
    GUARDIAN(4, "guardian"),
    @SerializedName("instructor")
    WORKPLACE_INSTRUCTOR(5, "instructor"),
    @SerializedName("board")
    BOARD(6, "board"),
    // Wilma account is the account which has roles, when you enter to the role selector in browser, that account that
    // you're currently logged in is called Wilma Account, and when you click the user, it's a Role
    @SerializedName("passwd")
    WILMA_ACCOUNT(7, "passwd"),
    @SerializedName("trainingcoordinator")
    TRAINING_COORDINATOR(8, "trainingcoordinator"),
    @SerializedName("training")
    TRAINING(9, "training"),
    @SerializedName("applicant")
    APPLICANT(10, "applicant"),
    @SerializedName("applicantguardian")
    APPLICANT_GUARDIAN(11, "applicantguardian");

    override fun toString(): String {
        return userTypeString
    }

    companion object {
        fun fromInt(value: Int) = UserType.values().first { it.userType == value }
        fun fromString(value: String) = UserType.values().first { it.userTypeString == value }
    }
}