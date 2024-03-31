package com.example.employeeShiftScheduling.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.time.LocalDateTime

class ScheduleState {
    var tenantId: String = ""
    var publishLength: Int = 0
    var draftLength: Int = 0
    lateinit var firstDraftDate: LocalDate
    lateinit var lastHistoricDate: LocalDate

    @JsonIgnore
    fun isHistoric(dateTime: LocalDateTime): Boolean {
        return dateTime.isBefore(getFirstPublishedDate().atStartOfDay())
    }
    @JsonIgnore
    fun isDraft(dateTime: LocalDateTime): Boolean {
        return !dateTime.isBefore(firstDraftDate.atStartOfDay())
    }
    @JsonIgnore
    fun isPublished(dateTime: LocalDateTime): Boolean {
        return !isHistoric(dateTime) && !isDraft(dateTime)
    }
    @JsonIgnore
    fun isHistoric(shift: Shift): Boolean {
        return isHistoric(shift.start)
    }
    @JsonIgnore
    fun isDraft(shift: Shift): Boolean {
        return isDraft(shift.start)
    }
    @JsonIgnore
    fun isPublished(shift: Shift): Boolean {
        return isPublished(shift.start)
    }
    @JsonIgnore
    fun getFirstPublishedDate(): LocalDate {
        return lastHistoricDate.plusDays(1)
    }
}
