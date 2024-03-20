package com.example.employeeShiftScheduling

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners.equal
import com.example.employeeShiftScheduling.model.Shift

class ShiftScheduleConstraintProvider:ConstraintProvider {
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            atMostOneShiftPerDay(constraintFactory),
            requiredSkill(constraintFactory)
        )
    }
//we establish specific rules or constraints, such as one employee being limited to one shift per day, or ensuring that every shift has the necessary skills.
// These constraints are defined within the Constraint Factory
fun atMostOneShiftPerDay(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            //If an employee takes two shifts on the same day, penalize.
            .join(Shift::class.java, equal { it.start.toLocalDate() }, equal { it.employee })
            //Filter function removes pairs with identical shifts on the same day.
            .filter { shift1, shift2 -> shift1 != shift2 }
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("At most one shift per day")
    }

    fun requiredSkill(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            .filter { shift ->
                shift.employee?.skills?.contains(shift.requiredSkill) ?: false
            }
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Required skill")
    }
}