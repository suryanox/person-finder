package com.persons.finder.domain.ports

import com.persons.finder.data.InsertPersonRow
import com.persons.finder.data.Person

interface PersonRepository {
    fun save(row: InsertPersonRow): Person
    fun findById(id: Long): Person?
    fun findAllByIds(ids: List<Long>): List<Person>
}
