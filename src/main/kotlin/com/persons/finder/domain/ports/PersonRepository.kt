package com.persons.finder.domain.ports

import com.persons.finder.data.Person

interface PersonRepository {
    fun save(person: Person): Person
    fun findById(id: Long): Person?
    fun findAllByIds(ids: List<Long>): List<Person>
}