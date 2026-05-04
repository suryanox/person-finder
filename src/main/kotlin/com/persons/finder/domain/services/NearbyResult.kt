package com.persons.finder.domain.services

import com.persons.finder.data.Person

data class NearbyResult(val person: Person, val distanceKm: Double)
