package com.dewakoding.arlocationbased.ui

import android.app.Activity
import android.graphics.Canvas
import android.hardware.SensorManager
import android.location.Location
import android.opengl.Matrix
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dewakoding.arlocationbased.R
import com.dewakoding.arlocationbased.helper.LocationHelper.ECEFtoENU
import com.dewakoding.arlocationbased.helper.LocationHelper.WSG84toECEF
import com.dewakoding.arlocationbased.listener.PointClickListener
import com.dewakoding.arlocationbased.model.Place

class AROverlayView constructor(activity: Activity, val places: MutableList<Place>?, val radiusInMeter: Double, val pointClickListener: PointClickListener):
    View(activity) {

    private var projectionMatrix = FloatArray(16)
    private var currentLocation: Location? = null
    private val arFrameLayout: FrameLayout = activity.findViewById(R.id.ar_frame_layout)
    private val arPointLayouts: MutableList<View> = mutableListOf()

    fun start() {
        if (parent != null) {
            val viewGroup = parent as ViewGroup
            viewGroup.removeView(this)
        }
        arFrameLayout.addView(this)
    }

    fun updateProjectionMatrix(matrix: FloatArray) {
        val rotationMatrix = FloatArray(16)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, matrix)

        val ratio: Float = when {
            width < height -> {
                width.toFloat() / height.toFloat()
            }
            else -> {
                height.toFloat() / width.toFloat()
            }
        }

        val viewMatrix = FloatArray(16)
        Matrix.frustumM(
            viewMatrix, 0, -ratio, ratio,
            -1f, 1f, 0.5f, 10000f
        )

        val projectionMatrix = FloatArray(16)
        Matrix.multiplyMM(
            projectionMatrix, 0, viewMatrix, 0,
            rotationMatrix, 0
        )
        this.projectionMatrix = projectionMatrix
        invalidate()
    }

    fun updateCurrentLocation(currentLocation: Location?) {
        this.currentLocation = currentLocation
        invalidate()
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (currentLocation == null) {
            return
        }
        places?.let {
            for (i in this.places!!.indices) {
                val currentLocationInECEF = WSG84toECEF(
                    currentLocation!!
                )
                val pointInECEF = WSG84toECEF(places?.get(i)!!.getCoordinate())
                val pointInENU = ECEFtoENU(currentLocation!!, currentLocationInECEF, pointInECEF)
                val cameraCoordinateVector = FloatArray(4)
                Matrix.multiplyMV(
                    cameraCoordinateVector,
                    0,
                    projectionMatrix,
                    0,
                    pointInENU,
                    0
                )
                if (cameraCoordinateVector[2] < 0) {
                    val x =
                        (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas!!.width
                    val y =
                        (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas!!.height

                    places[i].x = x
                    places[i].y = y
                    places[i].distance = distance(currentLocation!!, places!!.get(i).location)

                    if (radiusInMeter != null && places[i].distance!! <= radiusInMeter) {

                        if (arPointLayouts.size <= i) {
                            // Create a new AR point layout

                            val arPointCardView = LayoutInflater.from(context).inflate(R.layout.cardview_point_with_image, null)

                            val arPointIcon = arPointCardView.findViewById<ImageView>(R.id.img_status)
                            val arPointName = arPointCardView.findViewById<TextView>(R.id.tv_title)
                            val arPointDescription = arPointCardView.findViewById<TextView>(R.id.tv_description)
                            val arPointDistance = arPointCardView.findViewById<TextView>(R.id.tv_distance)

                            // Set the AR point image
                            if (places[i].photoUrl != null) {
                                arPointIcon.visibility = VISIBLE
                                Glide.with(this)
                                    .load(places[i].photoUrl)
                                    .into(arPointIcon)
                            }

                            // Set the AR point detail
                            arPointName.text = places[i].name
                            arPointDescription.text = places[i].description
                            arPointDistance.text = distanceStr(places[i].distance!!.toDouble())

                            // Measure the cardview to get its actual width and height
                            arPointCardView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

                            // Set the click listener for the AR point layout
                            arPointCardView.setOnClickListener {
                                pointClickListener.onClick(places[i])
                            }

                            // Add the AR point layout to the AROverlayView
                            val parentView = parent as ViewGroup
                            if (parentView != null) {
                                parentView.addView(arPointCardView)
                            }

                            // Save the current AR point layout reference
                            arPointLayouts.add(arPointCardView)

                            // Update the AR point layout position
                            val arPointCardViewLayoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            arPointCardViewLayoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                            arPointCardView.layoutParams = arPointCardViewLayoutParams

                            val marginInPixels = resources.getDimensionPixelSize(R.dimen.margin)
                            arPointCardViewLayoutParams.setMargins(0, marginInPixels, 0, 0)

                            arPointCardView.x = x
                            arPointCardView.y = y
                        }
                        if (arPointLayouts.size > 0) {
                            if (i < arPointLayouts.size) {
                                // Update the AR point layout position
                                val arPointLayout = arPointLayouts[i]
                                arPointLayout.x = x
                                arPointLayout.y = y
                            }

                        }
                    }
                }
            }

            // Remove any extra AR point layouts if present
            if (arPointLayouts.size > places.size) {
                for (x in places.size until arPointLayouts.size) {
                    val arPointLayout = arPointLayouts[x]
                    val parentView = parent as ViewGroup
                    parentView.removeView(arPointLayout)
                }
                arPointLayouts.removeAll { true }
            }
        }
    }



    fun distance(currentLoc: Location, pointLocation: Location): Float {
        return currentLoc.distanceTo(pointLocation)

    }

    fun distanceStr(distance: Double): String {
        if (distance < 1000) {
            return (("%.2f".format(distance)).toString() + " m")
        } else {
            return (("%.2f".format(distance / 1000)).toString() + " km")
        }
    }


}