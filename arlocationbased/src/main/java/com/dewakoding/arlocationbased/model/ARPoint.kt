package com.dewakoding.arlocationbased.model

import android.location.Location


class ARPoint(name: String, lat: Double, lon: Double, x: Float? = null, y: Float? = null, altitude: Double? = null) {
    var location: Location
    var name: String
    var x: Float?
    var y: Float?

    init {
        this.name = name
        location = Location("ARPoint")
        location.latitude = lat
        location.longitude = lon
        this.x = x
        this.y = y
    }

    fun getCoordinate(): Location {
        return location
    }

    fun getNamePoint(): String {
        return name
    }
}