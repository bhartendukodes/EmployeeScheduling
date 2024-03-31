package com.example.employeeShiftScheduling.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import java.time.LocalDate

data class Availability(
    @PlanningId
    val id:String,
    val employee:Employee,
    val localDate: LocalDate,
    val availability: AvailabilityTypes,
    )