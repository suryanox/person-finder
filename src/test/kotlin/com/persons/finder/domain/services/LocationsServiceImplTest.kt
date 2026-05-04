package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.exceptions.PersonNotFoundException
import com.persons.finder.domain.ports.LocationRepository
import com.persons.finder.domain.ports.PersonRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class LocationsServiceImplTest {

    private val locationRepository: LocationRepository = mock()
    private val personRepository: PersonRepository = mock()
    private val service = LocationsServiceImpl(locationRepository, personRepository)

    @Test
    fun `updateLocation throws PersonNotFoundException for unknown id`() {
        whenever(personRepository.findById(99L)).thenReturn(null)

        assertThrows<PersonNotFoundException> {
            service.updateLocation(99L, 13.75, 100.50)
        }
    }

    @Test
    fun `updateLocation calls locationRepository when person exists`() {
        whenever(personRepository.findById(1L)).thenReturn(Person(1L, "Alice", "Engineer", "hiking", "bio"))

        service.updateLocation(1L, 13.75, 100.50)

        verify(locationRepository).updateLocation(1L, 13.75, 100.50)
    }

    @Test
    fun `findNearby returns only locations within radius`() {
        val near = Location(1L, 13.76, 100.51)
        val far = Location(2L, 20.0, 100.51)
        whenever(locationRepository.findWithinBoundingBox(any(), any(), any(), any())).thenReturn(listOf(near, far))
        whenever(personRepository.findAllByIds(listOf(1L))).thenReturn(
            listOf(Person(1L, "Alice", "Engineer", "hiking", "bio"))
        )

        val results = service.findNearby(13.75, 100.50, 5.0)

        assertEquals(1, results.size)
        assertEquals(1L, results[0].person.id)
    }

    @Test
    fun `findNearby returns results sorted by distance ascending`() {
        val closer = Location(1L, 13.76, 100.51)
        val further = Location(2L, 13.80, 100.55)
        whenever(locationRepository.findWithinBoundingBox(any(), any(), any(), any())).thenReturn(listOf(further, closer))
        whenever(personRepository.findAllByIds(any())).thenReturn(listOf(
            Person(1L, "Alice", "Engineer", "hiking", "bio"),
            Person(2L, "Bob", "Designer", "gaming", "bio")
        ))

        val results = service.findNearby(13.75, 100.50, 50.0)

        assertEquals(2, results.size)
        assertTrue(results[0].distanceKm < results[1].distanceKm)
    }

    @Test
    fun `findNearby includes location exactly on boundary`() {
        val onBoundary = Location(1L, 13.7949, 100.50)
        whenever(locationRepository.findWithinBoundingBox(any(), any(), any(), any())).thenReturn(listOf(onBoundary))
        whenever(personRepository.findAllByIds(any())).thenReturn(
            listOf(Person(1L, "Alice", "Engineer", "hiking", "bio"))
        )

        val results = service.findNearby(13.75, 100.50, 5.0)

        assertEquals(1, results.size)
    }
}
