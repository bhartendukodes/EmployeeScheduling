package com.example.employeeShiftScheduling.model

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import java.time.LocalDateTime

//PlanningEntity, indicating that it can change during solving. This class represents a shift that can be assigned to an employee
// at a specific date and time.
@PlanningEntity
data class Shift(

    var start: LocalDateTime,

    var end: LocalDateTime,

    //skills required for the shift
    var requiredSkill: String,

    /* @PlanningVariable used when we changed during solving   // The employee property represents an employee assigned to the shift. */
    @PlanningVariable(valueRangeProviderRefs = ["employees"])
    var employee: Employee?=null
) {

    constructor() : this(LocalDateTime.MIN, LocalDateTime.MIN, "", null)


    constructor(start: LocalDateTime, end: LocalDateTime, requiredSkill: String) :
            this(start, end, requiredSkill, null)


    // Override toString method to provide custom string representation of Shift object
    override fun toString(): String = "$start - $end"
}


//overall solution A Shift object represents a work shift assigned to an employee. It can be adjusted dynamically.
// By manipulating these entities, a planning algorithm can generate an optimal schedule for shifts and employees