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

        currentARPoints.add(Place("1", "Coffee Shop", -6.174870735058176, 106.82620041234728, description = "Promotion available here"))
        currentARPoints.add(Place("2", "Restaurant", -6.122310891453182, 106.83357892611079, description = "Good Resto"))

    }

    override fun onARPointSelected(place: Place) {
        Toast.makeText(applicationContext, place.name, Toast.LENGTH_SHORT).show()
    }
}