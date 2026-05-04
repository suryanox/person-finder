package com.persons.finder.data

import javax.persistence.*

@Entity
@Table(
    name = "location",
    indexes = [Index(columnList = "latitude, longitude")]
)
class Location(
    @Id
    var referenceId: Long,

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double
) {
    constructor() : this(0, 0.0, 0.0)
}
