package com.example.employeeShiftScheduling.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.solver.SolverStatus

@PlanningSolution
class EmployeeSchedule {
    @ProblemFactCollectionProperty
    var availabilities: MutableList<Availability> = mutableListOf()

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    var employees: MutableList<Employee> = mutableListOf()

    @PlanningEntityCollectionProperty
    var shifts: MutableList<Shift> = mutableListOf()

    @PlanningScore
    lateinit var score: HardSoftScore

    lateinit var scheduleState: ScheduleState
    lateinit var solverStatus: SolverStatus
}


