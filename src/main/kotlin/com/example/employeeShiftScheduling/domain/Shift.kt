package com.example.employeeShiftScheduling.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import java.time.LocalDateTime


@PlanningEntity(pinningFilter = ShiftPinningFilter::class)
data class Shift(
    @PlanningId
    var id:String,
    val start: LocalDateTime,
    val end:LocalDateTime,
    val location:String,
    val requiredSkills:String,
    @PlanningVariable
    val employee: Employee? = null
)