package com.dewakoding.ar_locationbased

import android.os.Bundle
import android.widget.Toast
import com.dewakoding.arlocationbased.model.ARPoint
import com.dewakoding.arlocationbased.ui.ARActivity

class MainActivity : ARActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentARPoints = getARPoints()
        currentARPoints.clear()
        currentARPoints.add(ARPoint("Coffee Shop", -6.174870735058176, 106.82620041234728))
        currentARPoints.add(ARPoint("Restaurant",  -6.122310891453182, 106.83357892611079))

    }

    override fun onARPointSelected(arPoint: ARPoint) {
        Toast.makeText(applicationContext, arPoint.name, Toast.LENGTH_SHORT).show()
    }
}