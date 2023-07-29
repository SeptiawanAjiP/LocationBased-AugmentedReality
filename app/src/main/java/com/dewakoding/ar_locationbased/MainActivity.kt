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
        currentARPoints.add(ARPoint("MONAS", -6.175311688689925, 106.82604761289097))
        currentARPoints.add(ARPoint("TMII",  -6.304430001834124, 106.89161623298563))

    }

    override fun onARPointSelected(arPoint: ARPoint) {
        Toast.makeText(applicationContext, arPoint.name, Toast.LENGTH_SHORT).show()
    }
}