package com.persons.finder.domain.services

interface LocationsService {
    fun updateLocation(id: Long, latitude: Double, longitude: Double)
    fun findNearby(latitude: Double, longitude: Double, radiusKm: Double): List<NearbyResult>
}
