package com.example.nearbyplaces_maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.mapsworking.R
import com.example.mapsworking.getNearbyPlaces
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var mMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var lastLocation: Location? = null
    private var currentLocationMarker: Marker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val ProximityRadius = 10000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun onClick(view: View) {
        val restaurant = "restaurant"
        val transferdata = arrayOfNulls<Any>(2)
        val getnearbyplaces = getNearbyPlaces()
        when (view.id) {
            R.id.search_IB -> {
                val writeAddress = findViewById<View>(R.id.ET_search) as EditText
                val address = writeAddress.text.toString()
                var addressList: List<Address>? = null
                val userMarkerOptions = MarkerOptions()
                if (!TextUtils.isEmpty(address)) {
                    val geocoder = Geocoder(this)
                    try {
                        addressList = geocoder.getFromLocationName(address, 8)
                        if (addressList != null) {
                            var i = 0
                            while (i < addressList.size) {
                                val userAddress = addressList[i]
                                val latLng = LatLng(userAddress.latitude, userAddress.longitude)
                                userMarkerOptions.position(latLng)
                                userMarkerOptions.title(address)
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                mMap!!.addMarker(userMarkerOptions)
                                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
                                i++
                            }
                        } else {
                            Toast.makeText(this, "Location Not Found", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Write Any Place", Toast.LENGTH_LONG).show()
                }
            }
            R.id.restaurants_nearby -> {
                val url = getUrl(latitude, longitude, restaurant)
                transferdata[0] = mMap
                transferdata[1] = url
                getnearbyplaces.execute(*transferdata)
                Toast.makeText(this, "Searching for nearby Restaurats...", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "Showing nearby Restaurats...", Toast.LENGTH_LONG).show()
                mMap!!.clear()
            }
        }
    }

    private fun getUrl(latitude: Double, longitude: Double, restaurant: String): String {
        val googleURL = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googleURL.append("Location=$latitude,$longitude")
        googleURL.append("&radius=$ProximityRadius")
        googleURL.append("&sensor=true")
        googleURL.append("&key=" + "AIzaSyB0D5cjQywbwiyTM5fN43Hgm5tAQN7XqlE")
        Log.d("MapsActivity", "url = $googleURL")
        return googleURL.toString()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    fun checkUserLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), request_userLocationCode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), request_userLocationCode)
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            request_userLocationCode -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (googleApiClient == null);
                    run { buildGoogleApiClient() }
                    mMap!!.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this, "Permissioin Denied...", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        lastLocation = location
        if (currentLocationMarker != null) {
            currentLocationMarker!!.remove()
        }
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("user Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        currentLocationMarker = mMap!!.addMarker(markerOptions)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomBy(14f))
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
        }
    }

    override fun onConnected(bundle: Bundle?) {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 1100
        locationRequest!!.fastestInterval = 1100
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    companion object {
        private const val request_userLocationCode = 99
    }
}