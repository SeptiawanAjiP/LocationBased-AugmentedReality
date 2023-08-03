package com.dewakoding.ar_locationbased

import android.os.Bundle
import android.widget.Toast
import com.dewakoding.arlocationbased.model.Place
import com.dewakoding.arlocationbased.ui.ARActivity

class MainActivity : ARActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentARPoints = getARLocations()
        currentARPoints.clear()

        currentARPoints.add(Place( "1", "Coffee Shop", -6.29462263776453, 106.88430472863504, description = "Promotion available here"))
        currentARPoints.add(Place("1", "Restaurant", -6.236549700515769, 106.89388957362365, description = "Good Resto"))

    }

    override fun onARPointSelected(place: Place) {
        Toast.makeText(applicationContext, place.name, Toast.LENGTH_SHORT).show()
    }
}