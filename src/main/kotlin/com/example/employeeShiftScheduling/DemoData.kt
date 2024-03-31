package com.example.employeeShiftScheduling

import com.example.employeeShiftScheduling.domain.*
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class DemoDataGenerator {

    enum class DemoData {
        SMALL,
        LARGE
    }

    private val FIRST_NAMES = arrayOf("Shubham", "Suraj", "Ankit", "Beauty", "Jeet", "Niraj", "Dhiraj", "Nikita", "Naman", "Virat")
    private val LAST_NAMES = arrayOf("Tomar", "Singh", "Angra", "Singh", "Yadav", "Singh", "Singh", "Sharma", "Ojha", "Kholi")
    private val REQUIRED_SKILLS = arrayOf("Doctor", "Nurse")
    private val OPTIONAL_SKILLS = arrayOf("Anaesthetics", "Cardiology")
    private val LOCATIONS = arrayOf("Ambulatory care", "Critical care", "Pediatric care")
    private val SHIFT_LENGTH = Duration.ofHours(8)
    private val MORNING_SHIFT_START_TIME = LocalTime.of(6, 0)
    private val DAY_SHIFT_START_TIME = LocalTime.of(9, 0)
    private val AFTERNOON_SHIFT_START_TIME = LocalTime.of(14, 0)
    private val NIGHT_SHIFT_START_TIME = LocalTime.of(22, 0)

    private val SHIFT_START_TIMES_COMBOS = arrayOf(
        arrayOf(MORNING_SHIFT_START_TIME, AFTERNOON_SHIFT_START_TIME),
        arrayOf(MORNING_SHIFT_START_TIME, AFTERNOON_SHIFT_START_TIME, NIGHT_SHIFT_START_TIME),
        arrayOf(MORNING_SHIFT_START_TIME, DAY_SHIFT_START_TIME, AFTERNOON_SHIFT_START_TIME, NIGHT_SHIFT_START_TIME)
    )

    private val locationToShiftStartTimeListMap = mutableMapOf<String, List<LocalTime>>()

    fun generateDemoData(demoData: DemoData): EmployeeSchedule {
        val employeeSchedule = EmployeeSchedule()

        val initialRosterLengthInDays = if (demoData == DemoData.SMALL) 7 else 14
        val startDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))

        val scheduleState = ScheduleState().apply {
            firstDraftDate = startDate
            draftLength = initialRosterLengthInDays
            publishLength = 7
            lastHistoricDate = startDate.minusDays(7)
            tenantId = "1" // Assuming SINGLETON_SCHEDULE_ID value
        }

        employeeSchedule.scheduleState = scheduleState

        val random = Random()

        var shiftTemplateIndex = 0
        for (location in LOCATIONS) {
            locationToShiftStartTimeListMap[location] = SHIFT_START_TIMES_COMBOS[shiftTemplateIndex].toList()
            shiftTemplateIndex = (shiftTemplateIndex + 1) % SHIFT_START_TIMES_COMBOS.size
        }

        val namePermutations = FIRST_NAMES.flatMap { firstName ->
            LAST_NAMES.map { lastName ->
                "$firstName $lastName"
            }
        }.shuffled(random)

        val employees = mutableListOf<Employee>()
        repeat(15) { index ->
            val skills = pickSubset(OPTIONAL_SKILLS.toList(), random, 3, 1).toMutableSet()
            skills.add(pickRandom(REQUIRED_SKILLS, random))
            val employee = Employee(namePermutations[index], skills.toSet())
            employees.add(employee)
        }
        employeeSchedule.employees = employees

        val availabilities = mutableListOf<Availability>()
        val shifts = mutableListOf<Shift>()
        var count = AtomicInteger(0)
        repeat(initialRosterLengthInDays) { dayIndex ->
            val date = startDate.plusDays(dayIndex.toLong())
            val dayShifts = generateShiftsForDay(date, random)
            shifts.addAll(dayShifts)

            employees.shuffled(random).take(dayShifts.size).forEach { employee ->
                val availabilityType = pickRandom(AvailabilityTypes.values(), random)
                availabilities.add(Availability(count.incrementAndGet().toString(), employee, date, availabilityType))
            }
        }
        employeeSchedule.availabilities = availabilities
        employeeSchedule.shifts = shifts

        return employeeSchedule
    }

    private fun generateShiftsForDay(date: LocalDate, random: Random): List<Shift> {
        val shifts = mutableListOf<Shift>()
        LOCATIONS.forEach { location ->
            locationToShiftStartTimeListMap[location]?.forEach { shiftStartTime ->
                val shiftStartDateTime = date.atTime(shiftStartTime)
                val shiftEndDateTime = shiftStartDateTime.plus(SHIFT_LENGTH)
                shifts.addAll(generateShiftForTimeslot(shiftStartDateTime, shiftEndDateTime, location, random))
            }
        }
        return shifts
    }

    private fun generateShiftForTimeslot(
        timeslotStart: LocalDateTime,
        timeslotEnd: LocalDateTime,
        location: String,
        random: Random
    ): List<Shift> {
        var shiftCount = 1

        if (random.nextDouble() > 0.9) {
            shiftCount++
        }

        val shifts = mutableListOf<Shift>()
        repeat(shiftCount) {
            val requiredSkill = if (random.nextBoolean()) {
                pickRandom(REQUIRED_SKILLS, random)
            } else {
                pickRandom(OPTIONAL_SKILLS, random)
            }
            shifts.add(Shift("", timeslotStart, timeslotEnd, location, requiredSkill))
        }
        return shifts
    }

    fun addDraftShifts(schedule: EmployeeSchedule) {
        val random = Random()

        val newAvailabilities = mutableListOf<Availability>()
        val newShifts = mutableListOf<Shift>()
        val currentMaxAvailabilityId = schedule.availabilities.maxOfOrNull { it.id.toInt() } ?: 0
        val currentMaxShiftId = schedule.shifts.maxOfOrNull { it.id.toInt() } ?: 0

        val countAvailability = AtomicInteger(currentMaxAvailabilityId)
        val countShift = AtomicInteger(currentMaxShiftId)

        repeat(schedule.scheduleState.publishLength) { i ->
            val date = schedule.scheduleState.firstDraftDate.plusDays(schedule.scheduleState.publishLength.toLong() + i)
            val employeesWithAvailabilitiesOnDay = pickSubset(ArrayList(schedule.employees), random, 4, 3, 2, 1)
            employeesWithAvailabilitiesOnDay.forEach { employee ->
                val availabilityType = pickRandom(AvailabilityTypes.values(), random)
                newAvailabilities.add(Availability((countAvailability.incrementAndGet()).toString(), employee, date, availabilityType))
            }
            newShifts.addAll(generateShiftsForDay(date, random))
        }

        schedule.availabilities.addAll(newAvailabilities)
        newShifts.forEach { it.id = countShift.incrementAndGet().toString() }
        schedule.shifts.addAll(newShifts)
    }

    private fun <T> pickRandom(source: Array<T>, random: Random): T {
        return source[random.nextInt(source.size)]
    }

    private fun <T> pickSubset(sourceSet: List<T>, random: Random, vararg distribution: Int): Set<T> {
        var probabilitySum = 0
        distribution.forEach { probabilitySum += it }
        var choice = random.nextInt(probabilitySum)
        var numOfItems = 0
        while (choice >= distribution[numOfItems]) {
            choice -= distribution[numOfItems]
            numOfItems++
        }
        val items = sourceSet.shuffled(random)
        return items.take(numOfItems + 1).toSet()
    }
}
