package com.persons.finder.domain.ports

interface AiService {
    fun generateBio(jobTitle: String, hobbies: String): String
}
