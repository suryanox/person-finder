package com.persons.finder.presentation.dto

data class PersonResponse(
    val id: Long,
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val bio: String?
)
