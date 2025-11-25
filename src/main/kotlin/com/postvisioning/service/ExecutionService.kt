package com.postvisioning.service

import com.postvisioning.service.dto.ExecutionResult
import org.springframework.stereotype.Service

@Service
class ExecutionService {

    fun exec(command: List<String>): ExecutionResult {
        try {
            val process = ProcessBuilder(command)
                .start()

            val exitCode = process.waitFor()
            val stderr = process.errorReader().use { it.readLines() }
            val stdout = process.inputReader().use { it.readLines() }

            return ExecutionResult(
                exitCode = exitCode,
                stdout = stdout,
                stderr = stderr,
            )
        } catch (ex: Exception){
            return ExecutionResult(
                exitCode = 900,
                stdout = emptyList(),
                stderr = listOf(ex.message ?: "Something wrong during execution."),
            )
        }
    }
}

