package com.persons.finder.data

import javax.persistence.*

@Entity
@Table(name = "person")
class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var name: String,

    @Column(name = "job_title", nullable = false)
    var jobTitle: String,

    @Column(nullable = false)
    var hobbies: String,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null
) {
    fun hobbiesList(): List<String> = hobbies.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
