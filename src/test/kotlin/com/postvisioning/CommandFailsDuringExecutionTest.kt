package com.postvisioning

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CommandFailsDuringExecutionTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
): FunSpec({
    context("""Command fails during execution test """){
        lateinit var res: MvcResult
        lateinit var resJsonBody: JsonNode

        test("Request"){
            val requestBody = objectMapper.writeValueAsString(mapOf(
                "commands" to listOf(
                    "ls", "-non-existing-option1"
                )
            ))

            res = mockMvc.perform(post("/cmd/exec")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
            ).andReturn()
        }

        test("Response has got StatusCode=200"){
            assertThat(res.response.status).isEqualTo(200)
        }

        test("Response has valid JSON body"){
            resJsonBody = objectMapper.readTree(res.response.contentAsString)
        }

        test("Response has got Exit Code=0"){
            assertThat(resJsonBody["exitCode"].intValue()).isEqualTo(1)
        }

        test("""Response has got stdout=[]"""){
            val stderr = resJsonBody.withArrayProperty("stdout")
            assertThat(stderr).isEmpty()
        }

        test("""Response has got stderr=["ls: unrecognized option `--existing-option1", "..."]"""){
            val stdoutArray = resJsonBody.withArrayProperty("stderr")
            val stdoutLines = stdoutArray.map { it.textValue() }
            assertThat(stdoutLines.firstOrNull()).isEqualTo("""ls: unrecognized option `--existing-option1'""")
        }
    }

})