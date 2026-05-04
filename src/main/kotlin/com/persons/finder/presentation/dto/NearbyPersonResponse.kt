package com.persons.finder.presentation.dto

data class NearbyPersonResponse(
    val person: PersonResponse,
    val distanceKm: Double
)
