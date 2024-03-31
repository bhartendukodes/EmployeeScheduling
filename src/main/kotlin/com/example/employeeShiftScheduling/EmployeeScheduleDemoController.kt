package com.example.employeeShiftScheduling

import com.example.employeeShiftScheduling.domain.EmployeeSchedule
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/demo-data")
class EmployeeScheduleDemoResource(private val dataGenerator: DemoDataGenerator) {

    @GetMapping
    fun list(): ResponseEntity<Array<DemoDataGenerator.DemoData>> {
        return ResponseEntity.ok(DemoDataGenerator.DemoData.values())
    }

    @GetMapping("/{demoDataId}")
    fun generate(@PathVariable demoDataId: String): ResponseEntity<EmployeeSchedule> {
        val demoDataType = DemoDataGenerator.DemoData.valueOf(demoDataId.toUpperCase())
        val generatedData = dataGenerator.generateDemoData(demoDataType)
        return ResponseEntity.ok(generatedData)
    }
}
