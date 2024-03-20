package com.example.employeeShiftScheduling

import com.example.employeeShiftScheduling.model.Employee
import com.example.employeeShiftScheduling.model.Shift
import com.example.employeeShiftScheduling.model.ShiftSchedule
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.reflect.Method
import java.time.LocalDate

@SpringBootTest
class EmployeeShiftSchedulingApplicationTests {

	@Test
	fun contextLoads() {
		val shiftScheduleSolverUnitTest = ShiftScheduleSolverUnitTest()

		val problem: ShiftSchedule = shiftScheduleSolverUnitTest.loadProblem()
		val solution: ShiftSchedule = shiftScheduleSolverUnitTest.solveProblem(problem)

		shiftScheduleSolverUnitTest.printSolution(solution)

	}

	private fun ShiftScheduleSolverUnitTest.loadProblem(): ShiftSchedule {
		val wednesday = LocalDate.of(2024, 3, 20)
		val thrusday = LocalDate.of(2024, 3, 21)

		val employees = listOf(
			Employee("Shubham", setOf("Bartender")),
			Employee("Jeet", setOf("Waiter", "Bartender")),
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

	private fun ShiftScheduleSolverUnitTest.solveProblem(problem: ShiftSchedule): ShiftSchedule {
		// Ensure the list is not null and is a MutableList, then sort it in place
		problem.shifts?.let { shifts ->
			if (shifts is MutableList<Shift>) {
				shifts.sortBy { it.start }
			}
		}

		// Simulate assigning employees to shifts in a round-robin fashion
		problem.shifts?.forEachIndexed { index, shift ->
			val employee = problem.employee?.get(index % (problem.employee?.size ?: 1))
			// Ensure your Shift class has a property to set its employee
			shift.employee = employee
		}

		return problem
	}




}
