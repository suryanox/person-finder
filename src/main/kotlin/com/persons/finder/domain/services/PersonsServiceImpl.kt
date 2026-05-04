package com.persons.finder.domain.services

import com.persons.finder.data.InsertPersonRow
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.ports.AiService
import com.persons.finder.domain.ports.LocationRepository
import com.persons.finder.domain.ports.PersonRepository
import com.persons.finder.infrastructure.sanitization.PromptSanitizer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl(
    private val personRepository: PersonRepository,
    private val locationRepository: LocationRepository,
    private val aiService: AiService,
    private val promptSanitizer: PromptSanitizer
) : PersonsService {

    private val log = LoggerFactory.getLogger(PersonsServiceImpl::class.java)

    override fun createPerson(
        name: String,
        jobTitle: String,
        hobbies: List<String>,
        latitude: Double,
        longitude: Double
    ): Person {

        val hobbiesText = hobbies.joinToString(", ")

        val sanitizedJobTitle = promptSanitizer.sanitize(jobTitle)
        val sanitizedHobbies = promptSanitizer.sanitize(hobbiesText)

        log.info("Generating bio for jobTitle='{}', hobbies='{}'", sanitizedJobTitle, sanitizedHobbies)

        val bio = generateBioSafely(sanitizedJobTitle, sanitizedHobbies)

        log.info("Bio result: {}", bio ?: "null (saving without bio)")

        val savedPerson = personRepository.save(
            InsertPersonRow(
                name = name,
                jobTitle = jobTitle,
                hobbies = hobbiesText,
                bio = bio
            )
        )

        locationRepository.save(Location(savedPerson.id, latitude, longitude))

        return savedPerson
    }

    private fun generateBioSafely(jobTitle: String, hobbies: String): String? {
        return runCatching {
            aiService.generateBio(jobTitle, hobbies)
        }.onFailure { ex ->
            log.error("Bio generation failed", ex)
        }.getOrNull()
    }
}