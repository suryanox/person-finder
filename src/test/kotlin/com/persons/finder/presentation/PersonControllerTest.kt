package com.persons.finder.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.data.Person
import com.persons.finder.domain.exceptions.PersonNotFoundException
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.NearbyResult
import com.persons.finder.domain.services.PersonsService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PersonController::class)
class PersonControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var personsService: PersonsService

    @MockBean
    lateinit var locationsService: LocationsService

    private val basePath = "/api/v1/persons"

    @Test
    fun `POST persons returns 201 with person response`() {
        val person = Person(1L, "Alice", "Engineer", "hiking,cooking", "A quirky bio")
        whenever(personsService.createPerson(any(), any(), any(), any(), any())).thenReturn(person)

        val body = mapOf(
            "name" to "Alice",
            "jobTitle" to "Engineer",
            "hobbies" to listOf("hiking", "cooking"),
            "latitude" to 13.75,
            "longitude" to 100.50
        )

        mockMvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.bio").value("A quirky bio"))
    }

    @Test
    fun `POST persons returns 400 when name is blank`() {
        val body = mapOf(
            "name" to "",
            "jobTitle" to "Engineer",
            "hobbies" to listOf("hiking"),
            "latitude" to 13.75,
            "longitude" to 100.50
        )

        mockMvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `PUT location returns 204 on success`() {
        val body = mapOf("latitude" to 13.75, "longitude" to 100.50)

        mockMvc.perform(
            put("$basePath/1/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `PUT location returns 404 when person not found`() {
        whenever(locationsService.updateLocation(any(), any(), any())).thenThrow(PersonNotFoundException(99L))

        val body = mapOf("latitude" to 13.75, "longitude" to 100.50)

        mockMvc.perform(
            put("$basePath/99/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Person 99 not found"))
    }

    @Test
    fun `GET nearby returns 200 with sorted results`() {
        val person = Person(1L, "Alice", "Engineer", "hiking", "bio")
        whenever(locationsService.findNearby(any(), any(), any())).thenReturn(
            listOf(NearbyResult(person, 2.5))
        )

        mockMvc.perform(get("$basePath/nearby?lat=13.75&lon=100.50&radius=10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].person.id").value(1))
            .andExpect(jsonPath("$[0].distanceKm").value(2.5))
    }

    @Test
    fun `GET nearby returns 400 when radius is missing`() {
        mockMvc.perform(get("$basePath/nearby?lat=13.75&lon=100.50"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET nearby returns 400 when radius is zero`() {
        mockMvc.perform(get("$basePath/nearby?lat=13.75&lon=100.50&radius=0"))
            .andExpect(status().isBadRequest)
    }
}
