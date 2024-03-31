package com.example.employeeShiftScheduling


import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.solver.Solver
import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.SolverConfig
import ai.timefold.solver.core.config.solver.termination.TerminationConfig
import com.example.employeeShiftScheduling.model.Shift
import com.example.employeeShiftScheduling.model.ShiftSchedule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

class ShiftScheduleSolverUnitTest {

    private val logger: Logger = LoggerFactory.getLogger(ShiftScheduleSolverUnitTest::class.java)

    @Test
    fun given3Employees5Shifts_whenSolve_thenScoreIsOptimalAndAllShiftsAssigned() {
        val solverFactory: SolverFactory<ShiftSchedule> = SolverFactory.create(
            SolverConfig()
                .withSolutionClass(ShiftSchedule::class.java)
                .withEntityClasses(Shift::class.java)
                .withConstraintProviderClass(ShiftScheduleConstraintProvider::class.java)
                .withTerminationConfig(TerminationConfig().withBestScoreLimit("0hard/0soft"))
        )
        val solver: Solver<ShiftSchedule> = solverFactory.buildSolver()

        val problem: ShiftSchedule = loadProblem()
        val solution: ShiftSchedule = solver.solve(problem)
        assertThat(solution.score).isEqualTo(HardSoftScore.ZERO)
        assertThat(solution.shifts?.size).isNotZero
        solution.shifts?.forEach { shift ->
            assertThat(shift.employee).isNotNull
        }
        printSolution(solution)
    }

    private fun loadProblem(): ShiftSchedule {
        val wednesday = LocalDate.of(2024, 3, 20)
        val thrusday = LocalDate.of(2024, 3, 21)

        val employees = listOf(
            Employee("Shubham", setOf("Bartender(daru supplier)")),
            Employee("Jeet", setOf("Waiter", "Bartender(daru supplier)")),
            Employee("suraj", setOf("Waiter"))
        )

        val shifts = listOf(
            Shift(wednesday.atTime(6, 0), wednesday.atTime(14, 0), "Waiter"),
            Shift(wednesday.atTime(9, 0), wednesday.atTime(17, 0), "Bartender"),
            Shift(wednesday.atTime(14, 0), wednesday.atTime(22, 0), "Bartender"),
            Shift(thrusday.atTime(6, 0), thrusday.atTime(14, 0), "Waiter"),
            Shift(thrusday.atTime(14, 0), thrusday.atTime(22, 0), "Bartender")
        )

        return ShiftSchedule(employees, shifts)
    }

    public fun printSolution(solution: ShiftSchedule) {
        logger.info("Shift assignments")
        solution.shifts?.forEach { shift ->
            logger.info("${shift.start.toLocalDate()} ${shift.start.toLocalTime()} - ${shift.end.toLocalTime()}: ${shift.employee?.name}")
        }
    }
}