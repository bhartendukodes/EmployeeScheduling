package com.example.employeeShiftScheduling.solver.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class EmployeeScheduleExceptionHandler {

    @ExceptionHandler(EmployeeScheduleSolverException::class)
    fun handleEmployeeScheduleSolverException(exception: EmployeeScheduleSolverException): ResponseEntity<ErrorInfo> {
        val errorInfo = ErrorInfo(exception.jobId, exception.message ?: "")
        return ResponseEntity.status(exception.status).body(errorInfo)
    }
}
