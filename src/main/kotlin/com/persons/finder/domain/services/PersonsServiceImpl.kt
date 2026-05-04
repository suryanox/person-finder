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

    override fun createPerson(name: String, jobTitle: String, hobbies: List<String>, latitude: Double, longitude: Double): Person {
        val sanitizedJobTitle = promptSanitizer.sanitize(jobTitle)
        val sanitizedHobbies = promptSanitizer.sanitize(hobbies.joinToString(", "))
        log.info("Generating bio for jobTitle='{}' hobbies='{}'", sanitizedJobTitle, sanitizedHobbies)
        val bio = runCatching {
            aiService.generateBio(sanitizedJobTitle, sanitizedHobbies)
        }.onFailure { ex ->
            log.error("Bio generation failed", ex)
        }.getOrNull()
        log.info("Bio result: {}", bio ?: "null — saving person without bio")
        val person = personRepository.save(InsertPersonRow(name = name, jobTitle = jobTitle, hobbies = hobbies.joinToString(","), bio = bio))
        locationRepository.save(Location(person.id, latitude, longitude))
        return person
    }
}
