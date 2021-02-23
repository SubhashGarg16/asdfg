package com.example.mapsworking

import android.os.AsyncTask
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class getNearbyPlaces  {
    private var PlaceData: String? = null
    private var url: String? = null
    private var googleMap: GoogleMap? = null
    protected fun doInBackground(vararg objects: Any): String? {
        googleMap = objects[0] as GoogleMap
        url = objects[1] as String
        val downloadURL = DownloadURL()
        try {
            PlaceData = downloadURL.readURL(url)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return PlaceData
    }

    override fun onPostExecute(s: String?) {
        val nearbyPlacesList: List<HashMap<String, String>>
        val dataParser = DataParser()
        nearbyPlacesList = dataParser.parse(s) as List<HashMap<String, String>>
    }

    private fun DisplayNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>>) {
        for (i in nearbyPlacesList.indices) {
            val markerOptions = MarkerOptions()
            val googlenearbyPlace = nearbyPlacesList[i]
            val nameOfPlace = googlenearbyPlace["Place Name"]
            val vicinity = googlenearbyPlace["vicinity"]
            val lat = googlenearbyPlace["Lat"]!!.toDouble()
            val lng = googlenearbyPlace["Lng"]!!.toDouble()
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title("$nameOfPlace : $vicinity")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            googleMap!!.addMarker(markerOptions)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
        }
    }
}