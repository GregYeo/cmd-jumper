package com.postvisioning.controller

import com.postvisioning.controller.dto.ExecRequest
import com.postvisioning.controller.dto.ExecutionResponse
import com.postvisioning.service.ExecutionService
import jakarta.servlet.ServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("cmd")
class CmdRelayController(
    private val executionService: ExecutionService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/exec")
    fun exec(
        request: ServletRequest,
        @RequestBody
        body: ExecRequest
    ): ExecutionResponse {
        logger.debug("request[{}].body >> {}", request.requestId, body)

        val result = executionService.exec(body.commands)

        val responseBody = ExecutionResponse(
            exitCode = result.exitCode,
            stderr = result.stderr,
            stdout = result.stdout,
        )
        logger.debug("response[{}].body >> {}", request.requestId, responseBody)

        return responseBody
    }
}