package com.persons.finder.infrastructure.persistence

import com.persons.finder.data.Location
import com.persons.finder.domain.ports.LocationRepository
import org.springframework.stereotype.Component

@Component
class LocationRepositoryAdapter(
    private val jpaLocationRepository: JpaLocationRepository
) : LocationRepository {

    override fun save(location: Location) {
        jpaLocationRepository.save(location)
    }

    override fun updateLocation(referenceId: Long, latitude: Double, longitude: Double) {
        val location = jpaLocationRepository.findById(referenceId).orElse(Location(referenceId, latitude, longitude))
        location.latitude = latitude
        location.longitude = longitude
        jpaLocationRepository.save(location)
    }

    override fun findWithinBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<Location> =
        jpaLocationRepository.findWithinBoundingBox(minLat, maxLat, minLon, maxLon)
}
