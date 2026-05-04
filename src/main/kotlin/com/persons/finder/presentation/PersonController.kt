package com.persons.finder.presentation

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.presentation.dto.NearbyPersonResponse
import com.persons.finder.presentation.dto.PersonResponse
import com.persons.finder.presentation.dto.UpdateLocationRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/persons")
class PersonController(
    private val personsService: PersonsService,
    private val locationsService: LocationsService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPerson(@RequestBody req: CreatePersonRequest): PersonResponse {
        require(req.name.isNotBlank()) { "name must not be blank" }
        require(req.jobTitle.isNotBlank()) { "jobTitle must not be blank" }
        require(req.hobbies.isNotEmpty()) { "hobbies must not be empty" }
        require(req.latitude in -90.0..90.0) { "latitude must be between -90 and 90" }
        require(req.longitude in -180.0..180.0) { "longitude must be between -180 and 180" }
        return personsService.createPerson(req.name, req.jobTitle, req.hobbies, req.latitude, req.longitude).toResponse()
    }

    @PutMapping("/{id}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateLocation(@PathVariable id: Long, @RequestBody req: UpdateLocationRequest) {
        require(req.latitude in -90.0..90.0) { "latitude must be between -90 and 90" }
        require(req.longitude in -180.0..180.0) { "longitude must be between -180 and 180" }
        locationsService.updateLocation(id, req.latitude, req.longitude)
    }

    @GetMapping("/nearby")
    fun findNearby(
        @RequestParam lat: Double,
        @RequestParam lon: Double,
        @RequestParam radius: Double
    ): List<NearbyPersonResponse> {
        require(lat in -90.0..90.0) { "lat must be between -90 and 90" }
        require(lon in -180.0..180.0) { "lon must be between -180 and 180" }
        require(radius > 0) { "radius must be greater than 0" }
        return locationsService.findNearby(lat, lon, radius).map {
            NearbyPersonResponse(it.person.toResponse(), it.distanceKm)
        }
    }

    private fun com.persons.finder.data.Person.toResponse() = PersonResponse(
        id = id,
        name = name,
        jobTitle = jobTitle,
        hobbies = hobbiesList(),
        bio = bio
    )
}
