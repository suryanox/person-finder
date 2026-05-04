package com.persons.finder.domain.exceptions

class PersonNotFoundException(id: Long) : RuntimeException("Person $id not found")
