package com.example.employeeShiftScheduling.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId

data class Employee(
    @PlanningId
    val id:String,
    val skills:Set<String>,
)
