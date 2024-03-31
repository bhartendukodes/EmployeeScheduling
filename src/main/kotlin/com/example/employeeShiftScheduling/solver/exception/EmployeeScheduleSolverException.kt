package com.example.employeeShiftScheduling.solver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class EmployeeScheduleSolverException(
    val jobId: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    constructor(jobId: String, cause: Throwable) : this(jobId, HttpStatus.INTERNAL_SERVER_ERROR, cause.message ?: "", cause)
}
