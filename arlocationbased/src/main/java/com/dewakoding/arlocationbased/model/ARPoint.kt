package com.dewakoding.arlocationbased.model

import android.location.Location


open class ARPoint(id: String, lat: Double, lon: Double, x: Float? = null, y: Float? = null) {
    var location: Location
    var id: String
    var x: Float?
    var y: Float?

    init {
        this.id = id
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
        return id
    }
}