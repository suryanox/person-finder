package com.persons.finder.infrastructure.persistence

import com.persons.finder.data.Person
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPersonRepository : JpaRepository<Person, Long>
