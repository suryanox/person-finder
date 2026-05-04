package com.persons.finder.infrastructure.persistence

import com.persons.finder.data.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaLocationRepository : JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE l.latitude BETWEEN :minLat AND :maxLat AND l.longitude BETWEEN :minLon AND :maxLon")
    fun findWithinBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<Location>
}
