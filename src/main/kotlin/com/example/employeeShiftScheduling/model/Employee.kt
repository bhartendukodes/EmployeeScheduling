package com.example.employeeShiftScheduling.model

//The Employee class doesn’t need any Timefold annotation because it does not change during solving:
data class Employee(
    val name:String,
    val skills:Set<String>?
)
