package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.ports.AiService
import com.persons.finder.domain.ports.LocationRepository
import com.persons.finder.domain.ports.PersonRepository
import com.persons.finder.infrastructure.sanitization.PromptSanitizer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class PersonsServiceImplTest {

    private val personRepository: PersonRepository = mock()
    private val locationRepository: LocationRepository = mock()
    private val aiService: AiService = mock()
    private val promptSanitizer: PromptSanitizer = mock()
    private val service = PersonsServiceImpl(personRepository, locationRepository, aiService, promptSanitizer)

    @Test
    fun `createPerson sanitizes inputs before calling AI`() {
        val savedPerson = Person(1L, "Alice", "Engineer", "hiking,cooking", "A quirky bio")
        whenever(promptSanitizer.sanitize("Engineer")).thenReturn("Engineer")
        whenever(promptSanitizer.sanitize("hiking, cooking")).thenReturn("hiking, cooking")
        whenever(aiService.generateBio("Engineer", "hiking, cooking")).thenReturn("A quirky bio")
        whenever(personRepository.save(any())).thenReturn(savedPerson)

        service.createPerson("Alice", "Engineer", listOf("hiking", "cooking"), 13.75, 100.50)

        verify(promptSanitizer).sanitize("Engineer")
        verify(promptSanitizer).sanitize("hiking, cooking")
    }

    @Test
    fun `createPerson sets bio from AI service on saved person`() {
        val savedPerson = Person(1L, "Alice", "Engineer", "hiking,cooking", "A quirky bio")
        whenever(promptSanitizer.sanitize(any())).thenAnswer { it.arguments[0] }
        whenever(aiService.generateBio(any(), any())).thenReturn("A quirky bio")
        whenever(personRepository.save(any())).thenReturn(savedPerson)

        val result = service.createPerson("Alice", "Engineer", listOf("hiking", "cooking"), 13.75, 100.50)

        assertEquals("A quirky bio", result.bio)
        verify(locationRepository).save(any<Location>())
    }
}
