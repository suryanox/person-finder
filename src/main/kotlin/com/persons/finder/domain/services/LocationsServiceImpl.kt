package com.persons.finder.domain.services

import com.persons.finder.domain.exceptions.PersonNotFoundException
import com.persons.finder.domain.ports.LocationRepository
import com.persons.finder.domain.ports.PersonRepository
import org.springframework.stereotype.Service
import kotlin.math.cos

@Service
class LocationsServiceImpl(
    private val locationRepository: LocationRepository,
    private val personRepository: PersonRepository
) : LocationsService {

    override fun updateLocation(id: Long, latitude: Double, longitude: Double) {
        personRepository.findById(id) ?: throw PersonNotFoundException(id)
        locationRepository.updateLocation(id, latitude, longitude)
    }

    override fun findNearby(latitude: Double, longitude: Double, radiusKm: Double): List<NearbyResult> {
        val latDelta = radiusKm / 111.0
        val lonDelta = radiusKm / (111.0 * cos(Math.toRadians(latitude)))
        val locations = locationRepository.findWithinBoundingBox(
            latitude - latDelta, latitude + latDelta,
            longitude - lonDelta, longitude + lonDelta
        )
        val filtered = locations
            .map { it to HaversineCalculator.distanceKm(latitude, longitude, it.latitude, it.longitude) }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }
        val persons = personRepository.findAllByIds(filtered.map { (loc, _) -> loc.referenceId })
        val personMap = persons.associateBy { it.id }
        return filtered.mapNotNull { (loc, distance) ->
            personMap[loc.referenceId]?.let { NearbyResult(it, distance) }
        }
    }
}
