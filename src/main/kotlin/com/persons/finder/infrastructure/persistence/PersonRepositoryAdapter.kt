package com.persons.finder.infrastructure.persistence

import com.persons.finder.data.InsertPersonRow
import com.persons.finder.data.Person
import com.persons.finder.domain.ports.PersonRepository
import org.springframework.stereotype.Component

@Component
class PersonRepositoryAdapter(
    private val jpaPersonRepository: JpaPersonRepository
) : PersonRepository {

    override fun save(row: InsertPersonRow): Person = jpaPersonRepository.save(
        Person(id = 0, name = row.name, jobTitle = row.jobTitle, hobbies = row.hobbies, bio = row.bio)
    )

    override fun findById(id: Long): Person? = jpaPersonRepository.findById(id).orElse(null)

    override fun findAllByIds(ids: List<Long>): List<Person> = jpaPersonRepository.findAllById(ids)
}
