package com.example.employeeShiftScheduling


//class ShiftScheduleConstraintProviderUnitTest {
//
//    private val MONDAY = LocalDate.of(2030, 4, 1)
//    private val TUESDAY = LocalDate.of(2030, 4, 2)
//
//    private val constraintVerifier = ConstraintVerifier.build(ShiftScheduleConstraintProvider(), ShiftSchedule::class.java, Shift::class.java)
//
//    @Test
//    fun `given two shifts on one day, when applying AtMostOneShiftPerDay constraint, then penalize`() {
//        val ann = Employee("Ann", null)
//        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::atMostOneShiftPerDay)
//            .given(ann, Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), null.toString(), ann), Shift(MONDAY.atTime(14, 0), MONDAY.atTime(22, 0),
//                null.toString(), ann))
//            .penalizesBy(2)
//    }
//
//    @Test
//    fun `given two shifts on different days, when applying AtMostOneShiftPerDay constraint, then do not penalize`() {
//        val ann = Employee("Ann", null)
//        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::atMostOneShiftPerDay)
//            .given(ann, Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), null.toString(), ann), Shift(TUESDAY.atTime(14, 0), TUESDAY.atTime(22, 0),
//                null.toString(), ann))
//            .penalizesBy(0)
//    }
//
//    @Test
//    fun `given employee lacks required skill, when applying RequiredSkill constraint, then penalize`() {
//        val ann = Employee("Ann", setOf("Waiter"))
//        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::requiredSkill)
//            .given(ann, Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), "Cook", ann))
//            .penalizesBy(1)
//    }
//
//    @Test
//    fun `given employee has required skill, when applying RequiredSkill constraint, then do not penalize`() {
//        val ann = Employee("Ann", setOf("Waiter"))
//        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::requiredSkill)
//            .given(ann, Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), "Waiter", ann))
//            .penalizesBy(0)
//    }
//}