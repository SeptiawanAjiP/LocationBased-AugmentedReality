package com.dewakoding.arlocationbased.model


/**

Created by
name : Septiawan Aji Pradana
email : septiawanajipradana@gmail.com
website : dewakoding.com

 **/
class Place(
    id: String,
    name: String,
    lat: Double,
    lon: Double,
    x: Float? = null,
    y: Float? = null,
    altitude: Double? = null,
    description: String? = null,
    distance: Float? = null,
    photoUrl: String? = null
): ARPoint(id, lat, lon, x, y) {
    var name: String = name
    var description: String? = description
    var distance: Float? = distance
    var photoUrl: String? = photoUrl
}