package com.postvisioning.service.dto

data class ExecutionResult(
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>,
)