package com.persons.finder.domain.ports

import com.persons.finder.data.Location

interface LocationRepository {
    fun save(location: Location)
    fun updateLocation(referenceId: Long, latitude: Double, longitude: Double)
    fun findWithinBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<Location>
}
