package com.dewakoding.arlocationbased.ui

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.hardware.SensorManager
import android.location.Location
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.dewakoding.arlocationbased.R
import com.dewakoding.arlocationbased.helper.LocationHelper.ECEFtoENU
import com.dewakoding.arlocationbased.helper.LocationHelper.WSG84toECEF
import com.dewakoding.arlocationbased.listener.PointClickListener
import com.dewakoding.arlocationbased.model.ARPoint
import kotlin.math.roundToInt

class AROverlayView constructor(activity: Activity, val arPoints: MutableList<ARPoint>?, val icon: Int = R.drawable.house, val pointClickListener: PointClickListener):
    View(activity) {

    private var projectionMatrix = FloatArray(16)
    private var currentLocation: Location? = null
    private val arFrameLayout: FrameLayout = activity.findViewById(R.id.ar_frame_layout)
    val pointSize = 70

    private val onPointClickListener = OnTouchListener { _, event ->
        event?.let { safeEvent ->
            when (safeEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    performClick()
                    val x = safeEvent.x
                    val y = safeEvent.y

                    arPoints?.forEach {
                        if (x >= it.x!! - pointSize && x <= it.x!! + pointSize
                            && y >= it.y!! - pointSize && y <= it.y!! + pointSize) {
                            pointClickListener.onClick(it)
                        }
                    }
                }

                else -> {}
            }
        }

        false
    }

    fun start() {
        if (parent != null) {
            val viewGroup = parent as ViewGroup
            viewGroup.removeView(this)
        }
        arFrameLayout.addView(this)
        setOnTouchListener(onPointClickListener)
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

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 60f

        arPoints?.let {
            for (i in this.arPoints!!.indices) {
                val currentLocationInECEF = WSG84toECEF(
                    currentLocation!!
                )
                val pointInECEF = WSG84toECEF(arPoints?.get(i)!!.getCoordinate())
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

                    arPoints[i].x = x
                    arPoints[i].y = y

                    canvas.drawCircle(x, y, pointSize.toFloat(), paint)
                    canvas.drawText(arPoints.get(i).name, x - (30 * arPoints.get(i).name.length / 2), y - 80, paint);

                    val icon = ResourcesCompat.getDrawable(
                        context.resources,
                        this.icon,
                        null
                    )

                    icon?.let {
                        it.setBounds(x.roundToInt() - pointSize,y.roundToInt() - pointSize,x.roundToInt() + pointSize,y.roundToInt() + pointSize)
                        it.draw(canvas)
                    }
                }
            }
        }
    }
}