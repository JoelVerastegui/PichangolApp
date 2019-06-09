package com.example.joel.pichangol.activities

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.joel.pichangol.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener, GoogleMap.OnCameraMoveListener {


    // Location Permission Variables
    var accessGranted = false
    var LOCATION_PERMISSION = 1

    // GoogleMap Variables
    var mMap : GoogleMap? = null
    var markers = ArrayList<Marker?>()

    // GPS Variables
    var locationManager : LocationManager? = null
    var LOCATION_REFRESH_TIME = 10000L
    var LOCATION_REFRESH_DISTANCE = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        lblViewLocal.setOnClickListener {
            val localName = lblName.text
            val localAddress = lblAddress.text

            var localIntent = Intent(this, LocalActivity::class.java)
            localIntent.putExtra("localName", localName)
            localIntent.putExtra("localAddress", localAddress)
            startActivity(localIntent)
        }


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            validateAccessLocation()
        } else{
            accessGranted = true
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
    }

    // Location Permission fun
    @TargetApi(Build.VERSION_CODES.M)
    private fun validateAccessLocation(){

        val iGPS = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (iGPS != PackageManager.PERMISSION_GRANTED){

            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

            requestPermissions(permissions, LOCATION_PERMISSION)

        } else{
            accessGranted = true
        }

    }

    // Location Permission fun result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    accessGranted = true
                } else{
                    Toast.makeText(this,"Usted no tiene permisos de ubicacion",Toast.LENGTH_LONG).show()
                    accessGranted = false
                }
            }
        }

    }

    // GoogleMap Controller
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0

        mMap?.clear()


        val marker1 : Marker? = mMap?.addMarker(MarkerOptions()
            .position(LatLng(-12.122294, -77.028323))
            .title("Local de Doña Cecilia").visible(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ball_icon)))

        marker1?.tag = "1"
        markers.add(marker1)


        val marker2 : Marker? = mMap?.addMarker(MarkerOptions()
            .position(LatLng(-12.120545, -77.027553))
            .title("Local de Miguel Castro").visible(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ball_icon)))

        marker2?.tag = "2"
        markers.add(marker2)


        val marker3 : Marker? = mMap?.addMarker(MarkerOptions()
            .position(LatLng(-12.121637, -77.027582))
            .title("Local de Fausto Arevalo").visible(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ball_icon)))

        marker3?.tag = "3"
        markers.add(marker3)

        mMap?.setOnMarkerClickListener(this)

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-12.122294, -77.028323), 15f))

        try{
            mMap?.isMyLocationEnabled = true

            if(accessGranted){
                getLocation()
            }

        } catch(se : SecurityException){
            Toast.makeText(this,"Error al obtener ubicacion actual",Toast.LENGTH_LONG).show()
        }
    }

    // GPS Update Location
    override fun onLocationChanged(location: Location?) {

        if (location != null){
            val ubication = LatLng(location.latitude,location.longitude)

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 15f))
        }

    }

    // GPS Actual Location
    private fun getLocation(){
        try{
            locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager

            val gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(gps_enabled){
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this)
            }
        } catch(se : SecurityException) {
            Toast.makeText(this,"Error al mostrar ubicacion actual",Toast.LENGTH_LONG).show()
        }
    }

    // Marker Click Event
    override fun onMarkerClick(p0: Marker?): Boolean {
        val markerTitle = p0?.title

        //Toast.makeText(this,markerTitle,Toast.LENGTH_SHORT).show()

        val address = when(p0?.tag){
            "1" -> "Direccion 1"
            "2" -> "Direccion 2"
            "3" -> "Direccion 3"
            else -> "Ninguno"
        }

        // Setting data
        lblName.text = markerTitle
        lblAddress.text = address

        // localInfo Animation
        ObjectAnimator.ofFloat(lblLocalInfo,"translationY", 0f).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblName,"translationY", 0f).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblAddress,"translationY", 0f).apply {
            duration = 200
            start()
        }
        ObjectAnimator.ofFloat(lblViewLocal,"translationY", 0f).apply {
            duration = 200
            start()
        }

        /*
        var localIntent = Intent(this, LocalActivity::class.java)
        localIntent.putExtra("localTitle", markerTitle)
        startActivity(localIntent)
        */

        return true
    }

    // Camera Move Listener
    override fun onCameraMove() {
        var l1 : Location? = null
        l1?.latitude = -12.121637
        l1?.longitude = -77.027582

        var l2 : Location? = null
        l2?.latitude = mMap?.cameraPosition?.target?.latitude as Double
        l2?.longitude = mMap?.cameraPosition?.target?.longitude as Double

        var distance = l1?.distanceTo(l2)

        Toast.makeText(this,"Distance: $distance",Toast.LENGTH_SHORT).show()
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
