package com.persons.finder.domain.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HaversineCalculatorTest {

    @Test
    fun `Bangkok to Singapore is approximately 1432 km`() {
        val distance = HaversineCalculator.distanceKm(13.7563, 100.5018, 1.3521, 103.8198)
        assertTrue(distance in 1400.0..1450.0)
    }

    @Test
    fun `same point returns zero`() {
        val distance = HaversineCalculator.distanceKm(48.8566, 2.3522, 48.8566, 2.3522)
        assertEquals(0.0, distance, 0.0001)
    }

    @Test
    fun `distance is symmetric`() {
        val ab = HaversineCalculator.distanceKm(13.7563, 100.5018, 1.3521, 103.8198)
        val ba = HaversineCalculator.distanceKm(1.3521, 103.8198, 13.7563, 100.5018)
        assertEquals(ab, ba, 0.0001)
    }

    @Test
    fun `distance is always non-negative`() {
        val distance = HaversineCalculator.distanceKm(-33.8688, 151.2093, 51.5074, -0.1278)
        assertTrue(distance >= 0)
    }
}
