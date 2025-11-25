package com.postvisioning.controller.dto

data class ExecutionResponse(
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>,
)