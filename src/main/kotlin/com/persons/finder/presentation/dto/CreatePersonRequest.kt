package com.persons.finder.presentation.dto

import javax.validation.constraints.*

data class CreatePersonRequest(
    @field:NotBlank(message = "name must not be blank")
    val name: String,

    @field:NotBlank(message = "jobTitle must not be blank")
    val jobTitle: String,

    @field:NotEmpty(message = "hobbies must not be empty")
    val hobbies: List<String>,

    @field:DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @field:DecimalMax(value = "90.0", message = "latitude must be <= 90")
    val latitude: Double,

    @field:DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @field:DecimalMax(value = "180.0", message = "longitude must be <= 180")
    val longitude: Double
)
