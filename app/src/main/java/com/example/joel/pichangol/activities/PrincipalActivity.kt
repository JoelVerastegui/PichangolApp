package com.example.joel.pichangol.activities

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.models.Local
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener, GoogleMap.OnCameraMoveListener {

    // Activity variables
    var selectedLocal : Local? = null

    // Location Permission Variables
    var accessGranted = false
    var LOCATION_PERMISSION = 1

    // GoogleMap Variables
    var mMap : GoogleMap? = null

    // GPS Variables
    var locationManager : LocationManager? = null
    var LOCATION_REFRESH_TIME = 10000L
    var LOCATION_REFRESH_DISTANCE = 10f

    // Volley Variables
    var requestQueue : RequestQueue? = null
    var locals = ArrayList<Local>()

    // Server variables
    var serverIP = Server.instance.ip


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        //set loading gif to front
        imgLoading.visibility = View.VISIBLE
        imgLoading.bringToFront()

        lblViewLocal.setOnClickListener {
            val localName = lblName.text
            val localAddress = lblAddress.text

            var localIntent = Intent(this, LocalActivity::class.java)
            localIntent.putExtra("localId", selectedLocal?.id)
            localIntent.putExtra("localName", selectedLocal?.name)
            localIntent.putExtra("localAddress", selectedLocal?.address)
            localIntent.putExtra("localDescription", selectedLocal?.description)
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

        // Run local get request
        localGetRequest()

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

        selectedLocal = locals.find { l -> l.id == p0?.tag }

        // Setting data
        lblName.text = selectedLocal?.name
        lblAddress.text = selectedLocal?.address

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
        Toast.makeText(this,"Se cambio la ubicacion",Toast.LENGTH_SHORT).show()
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Local get Request
    fun localGetRequest(){

        requestQueue = Volley.newRequestQueue(this)

        var request = JsonArrayRequest(
            Request.Method.GET,
            "http://$serverIP:8080/api-rest/get/obtenerdatoslocal",
            null,
            Response.Listener { response ->
                var localList = ArrayList<Local>()

                for(i in 0 until response.length()){
                    val localJson = response.getJSONObject(i)

                    locals.add(Local(
                        localJson["id"] as? Int ?: 0,
                   localJson.getJSONObject("admin")["id"] as? Int ?: 0,
                        localJson["name"] as? String ?: "",
                        localJson["address"] as? String ?: "",
                        localJson["description"] as? String ?: "",
                        localJson["latitude"] as? Double ?: 0.0,
                        localJson["longitude"] as? Double ?: 0.0,
                        localJson["status"] as? String ?: ""
                    ))

                }

                for (local in locals){
                    val marker : Marker? = mMap?.addMarker(MarkerOptions()
                        .position(LatLng(local.latitude, local.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ball_icon)))

                    marker?.tag = local.id
                }

                imgLoading.visibility = View.GONE

            },
            Response.ErrorListener {
                imgLoading.visibility = View.GONE
                Toast.makeText(this,"Probablemente, el servicio es incorrecto. Error: $it", Toast.LENGTH_LONG).show()
            }
        )

        request.retryPolicy = DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(request)

    }

}
