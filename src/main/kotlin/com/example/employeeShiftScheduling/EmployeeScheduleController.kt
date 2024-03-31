package com.example.employeeShiftScheduling

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.solver.SolutionManager
import ai.timefold.solver.core.api.solver.SolverManager
import ai.timefold.solver.core.api.solver.SolverStatus
import com.example.employeeShiftScheduling.domain.EmployeeSchedule
import com.example.employeeShiftScheduling.solver.exception.EmployeeScheduleSolverException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
@RestController
@RequestMapping("/schedules")
class EmployeeScheduleController(
    private val solverManager: SolverManager<EmployeeSchedule, String>,
    private val solutionManager: SolutionManager<EmployeeSchedule, HardSoftScore>,
    private val dataGenerator: DemoDataGenerator
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(EmployeeScheduleController::class.java)
        const val SINGLETON_SCHEDULE_ID = "1"
    }

    private val jobIdToJob: ConcurrentHashMap<String, Job> = ConcurrentHashMap()

    @GetMapping
    fun list(): Collection<String> = jobIdToJob.keys

    @PostMapping("/schedules", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun solve(@RequestBody problem: EmployeeSchedule): String {
        val jobId = UUID.randomUUID().toString()
        jobIdToJob[jobId] = Job.ofSchedule(problem)
        solverManager.solveBuilder()
            .withProblemId(jobId)
            .withProblemFinder { jobIdToJob[it]?.schedule }
            .withBestSolutionConsumer { solution -> jobIdToJob[jobId] = Job.ofSchedule(solution) }
            .withExceptionHandler { id, exception ->
                jobIdToJob[id] = Job.ofException(exception)
                LOGGER.error("Failed solving jobId ($id).", exception)
            }
            .run()
        return jobId
    }

    @GetMapping("/{jobId}")
    fun getEmployeeSchedule(@PathVariable jobId: String): ResponseEntity<EmployeeSchedule?> {
        val job = jobIdToJob[jobId] ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        job.exception?.let {
            throw EmployeeScheduleSolverException(jobId, it)
        }
        job.schedule?.solverStatus = solverManager.getSolverStatus(jobId)
        return ResponseEntity.ok(job.schedule)
    }

    @DeleteMapping("/{jobId}")
    fun terminateSolving(@PathVariable jobId: String): ResponseEntity<String> {
        return try {
            solverManager.terminateEarly(jobId)
            ResponseEntity.ok("Solving for job ID $jobId has been terminated early.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    @GetMapping("/{jobId}/status")
    fun getStatus(@PathVariable jobId: String): ResponseEntity<Any> {
        val job = jobIdToJob[jobId] ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        job.exception?.let {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }
        val solverStatus = solverManager.getSolverStatus(jobId)
        return ResponseEntity.ok(mapOf("score" to job.schedule?.score, "solverStatus" to solverStatus))
    }

    @PostMapping("/{jobId}/publish")
    fun publish(@PathVariable jobId: String): ResponseEntity<String> {
        val job = jobIdToJob[jobId] ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val schedule = job.schedule ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No schedule available to publish.")

        if (solverManager.getSolverStatus(jobId) != SolverStatus.NOT_SOLVING) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot publish a schedule while solving is in progress for job ID $jobId.")
        }

        // Assuming `addDraftShifts` modifies the schedule correctly
        try {
            dataGenerator.addDraftShifts(schedule)
            jobIdToJob[jobId] = Job.ofSchedule(schedule)
            return ResponseEntity.ok("Schedule for job ID $jobId has been published.")
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to publish schedule for job ID $jobId: ${e.message}")
        }
    }

    private data class Job(val schedule: EmployeeSchedule?, val exception: Throwable?) {
        companion object {
            fun ofSchedule(schedule: EmployeeSchedule): Job = Job(schedule, null)
            fun ofException(error: Throwable): Job = Job(null, error)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<EmployeeScheduleController>(*args)
}
