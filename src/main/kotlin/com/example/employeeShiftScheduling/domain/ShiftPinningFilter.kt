package com.example.employeeShiftScheduling.domain

import ai.timefold.solver.core.api.domain.entity.PinningFilter

class ShiftPinningFilter : PinningFilter<EmployeeSchedule, Shift> {
    override fun accept(employeeSchedule: EmployeeSchedule, shift: Shift): Boolean {
        val scheduleState = employeeSchedule.scheduleState
        return !scheduleState.isDraft(shift)
    }
}



//ek restaurant ke employees aur unke shifts ke schedule ko manage kar rahe ho. Agar restaurant ke schedule mein koi shift draft phase se bahar hai, matlab woh final hai aur tay ho chuki hai ki us samay kaam karna hai.
//
//Isi tarah, yeh class kaam karegi jaise ek supervisor jo dekhega ki kis shift ko final approve kiya ja sakta hai aur kaun sa shift draft phase mein hai aur usko aur improvements ki zarurat hai.