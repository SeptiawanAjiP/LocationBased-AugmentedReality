package com.dewakoding.arlocationbased.ui

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dewakoding.arlocationbased.databinding.ActivityCameraBinding
import com.dewakoding.arlocationbased.listener.PointClickListener
import com.dewakoding.arlocationbased.model.Place
import com.dewakoding.arlocationbased.util.CameraUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**

Created by
name : Septiawan Aji Pradana
email : septiawanajipradana@gmail.com
website : dewakoding.com

 **/
typealias LumaListener = (luma: Double) -> Unit
open class ARActivity() : AppCompatActivity(), EasyPermissions.PermissionCallbacks, SensorEventListener, LocationListener{

    private lateinit var places: MutableList<Place>

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private var arOverlayView: AROverlayView? = null
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    fun ARInitData(places: MutableList<Place>) {
        this.places = places

        initView()
    }

    private fun initView() {
        arOverlayView = AROverlayView(this, places, pointClickListener =  object :
            PointClickListener {
            override fun onClick(place: Place) {
                onARPointSelected(place)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        requestPermissions()

        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onResume() {
        super.onResume()
        registerSensors();
        arOverlayView?.start()
    }

    private fun registerSensors() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                    })
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun requestPermissions() {
        if (CameraUtility.hasCameraPermissions(this) &&
            hasLocationPermissions()
        ) {
            startCamera()
            requestLastLocation()
            requestLocationUpdates()
        } else {
            val permissions = mutableListOf<String>()
            if (!CameraUtility.hasCameraPermissions(this)) {
                permissions.add(Manifest.permission.CAMERA)
            }
            if (!hasLocationPermissions()) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the required permissions to use this app",
                1,
                *permissions.toTypedArray()
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (CameraUtility.hasCameraPermissions(this) &&
            hasLocationPermissions()
        ) {
            startCamera()
            requestLastLocation()
            requestLocationUpdates()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.forEach {
                arOverlayView?.updateCurrentLocation(it);
            }
        }
    }

    private fun requestLastLocation() {
        if (hasLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    arOverlayView?.updateCurrentLocation(it)
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        if (hasLocationPermissions()) {
            val locationRequest = LocationRequest.create()?.apply {
                interval = 6000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                arOverlayView?.updateProjectionMatrix(it.values)
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("AR-LOCATION-BASED", "onAccuracyChanged")
    }

    override fun onLocationChanged(p0: Location) {
        Log.d("AR-LOCATION-BASED\"", "onLocationChanged")
    }

    open fun onARPointSelected(place: Place) {

    }


}