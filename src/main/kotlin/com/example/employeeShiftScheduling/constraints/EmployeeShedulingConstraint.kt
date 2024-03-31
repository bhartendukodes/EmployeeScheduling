package com.example.employeeShiftScheduling.constraints

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners
import com.example.employeeShiftScheduling.domain.Availability
import com.example.employeeShiftScheduling.domain.AvailabilityTypes
import com.example.employeeShiftScheduling.domain.Shift
import java.time.Duration

class EmployeeSchedulingConstraint:ConstraintProvider{
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            requiredSkill(constraintFactory),
            noOverlappingShifts(constraintFactory),
            atLeast10HoursBetweenTwoShifts(constraintFactory),
            oneShiftPerDay(constraintFactory),
            unavailableEmployee(constraintFactory),
            desiredDayForEmployee(constraintFactory),
            undesiredDayForEmployee(constraintFactory)
        )
    }

    private fun noOverlappingShifts(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair(
            Shift::class.java,
            Joiners.equal(Shift::employee),
            Joiners.overlapping(Shift::start, Shift::end)
        )
            .penalizeLong(HardSoftScore.ONE_HARD) { shift1, shift2 ->
                val overlapMinutes = Duration.between(
                    shift1.start.coerceAtLeast(shift2.start),
                    shift1.end.coerceAtMost(shift2.end)
                ).toMinutes()
                overlapMinutes.toInt().toLong()
            }
            .asConstraint("Overlapping shift")
    }

    private fun atLeast10HoursBetweenTwoShifts(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair(
            Shift::class.java,
            Joiners.equal(Shift::employee),
            Joiners.lessThanOrEqual(Shift::end, Shift::start)
        )
            .filter { firstShift, secondShift ->
                val breakLength = Duration.between(firstShift.end, secondShift.start).toHours()
                breakLength < 10
            }
            .penalizeLong(HardSoftScore.ONE_HARD) { firstShift, secondShift ->
                val breakLength = Duration.between(firstShift.end, secondShift.start).toMinutes().toInt()
                ((10 * 60) - breakLength).toLong()
            }
            .asConstraint("At least 10 hours between 2 shifts")
    }

    private fun oneShiftPerDay(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair(
            Shift::class.java,
            Joiners.equal(Shift::employee),
            Joiners.equal { shift -> shift.start.toLocalDate() }
        )
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Max one shift per day")
    }

    private fun unavailableEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            .join(Availability::class.java,
                Joiners.equal({ shift -> shift.start.toLocalDate() }, { availability -> availability.localDate }),
                Joiners.equal(Shift::employee, Availability::employee))
            .filter { shift, availability -> availability.availability == AvailabilityTypes.UNAVAILABLE }
            .penalizeLong(HardSoftScore.ONE_HARD) { shift, _ ->
                Duration.between(shift.start, shift.end).toMinutes().toInt().toLong() }
            .asConstraint("Unavailable employee")
    }

    private fun desiredDayForEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            .join(Availability::class.java,
                Joiners.equal({ shift -> shift.start.toLocalDate() }, { availability -> availability.localDate }),
                Joiners.equal(Shift::employee, Availability::employee))
            .filter { shift, availability -> availability.availability == AvailabilityTypes.DESIRED }
            .reward(HardSoftScore.ONE_SOFT) { shift, _ ->
                Duration.between(shift.start, shift.end).toMinutes().toInt() }
            .asConstraint("Desired day for employee")
    }

    private fun undesiredDayForEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            .join(Availability::class.java,
                Joiners.equal({ shift -> shift.start.toLocalDate() }, { availability -> availability.localDate }),
                Joiners.equal(Shift::employee, Availability::employee))
            .filter { shift, availability -> availability.availability == AvailabilityTypes.UNDESIRED }
            .penalize(HardSoftScore.ONE_SOFT) { shift, _ ->
                Duration.between(shift.start, shift.end).toMinutes().toInt() }
            .asConstraint("Undesired day for employee")
    }



    private fun requiredSkill(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(Shift::class.java)
            .filter{shift -> shift.employee!!.skills.none{it==shift.requiredSkills}}
            .penalize(HardSoftScore.ZERO)
            .asConstraint("")
    }

}