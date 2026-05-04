package com.persons.finder.infrastructure.ai

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.domain.ports.AiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OpenAiService(
    @Value("\${openai.api-key}") private val apiKey: String,
    @Value("\${openai.base-url}") private val baseUrl: String,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : AiService {

    private val model = "gpt-5-nano"
    private val systemPrompt = ClassPathResource("prompts/bio_system_prompt.txt").inputStream.bufferedReader().readText()

    override fun generateBio(jobTitle: String, hobbies: String): String? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(apiKey)
        }

        val inputJson = objectMapper.writeValueAsString(mapOf("jobTitle" to jobTitle, "hobbies" to hobbies))

        val body = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf("role" to "system", "content" to systemPrompt),
                mapOf("role" to "user", "content" to inputJson)
            )
        )

        val response = restTemplate.postForObject(
            "$baseUrl/chat/completions",
            HttpEntity(body, headers),
            OpenAiResponse::class.java
        ) ?: throw RuntimeException("No response from OpenAI")

        return response.choices.first().message.content.trim().ifBlank { null }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class OpenAiResponse(val choices: List<Choice>)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Choice(val message: Message)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Message(val content: String)
}
