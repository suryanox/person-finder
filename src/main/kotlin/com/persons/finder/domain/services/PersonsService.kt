package com.persons.finder.domain.services

import com.persons.finder.data.Person

interface PersonsService {
    fun createPerson(name: String, jobTitle: String, hobbies: List<String>, latitude: Double, longitude: Double): Person
}

