package com.dewakoding.ar_locationbased

import android.os.Bundle
import android.widget.Toast
import com.dewakoding.arlocationbased.model.Place
import com.dewakoding.arlocationbased.ui.ARActivity

class MainActivity : ARActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val list = ArrayList<Place>()
        list.add(
            Place("1",
                "Coffee Shop",
                -6.174870735058176,
                106.82620041234728,
                description = "Promotion available here",
                photoUrl = "https://images.unsplash.com/photo-1606787366850-de6330128bfc?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80")
        )
        list.add(
            Place("2",
                "Restaurant",
                -6.122310891453182,
                106.83357892611079,
                description = "Good Resto",
                photoUrl = "https://images.unsplash.com/photo-1498837167922-ddd27525d352?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80")
        )
        // You want to display places within a radius of 10.000 meters / 10 km.
        ARInitData(list, 10000.00)

    }

    override fun onARPointSelected(place: Place) {
        Toast.makeText(applicationContext, place.name, Toast.LENGTH_SHORT).show()
    }
}