package com.example.mapsworking

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DataParser {
    private fun getOnePlace(jsonGoogleObject: JSONObject): HashMap<String, String> {
        val gogglePlaceMap = HashMap<String, String>()
        var NameOfPlace = "NA"
        var vicinity = "NA"
        var latitude = ""
        var longitude = ""
        var reference = ""
        try {
            if (!jsonGoogleObject.isNull("name")) {
                NameOfPlace = jsonGoogleObject.getString("Name")
            }
            if (!jsonGoogleObject.isNull("vicinity")) {
                vicinity = jsonGoogleObject.getString("vicinity")
            }
            latitude = jsonGoogleObject.getJSONObject("geomatery").getJSONObject("Location").getString("lat")
            longitude = jsonGoogleObject.getJSONObject("geomatery").getJSONObject("Location").getString("Lng")
            reference = jsonGoogleObject.getString("reference")
            gogglePlaceMap["Place Name"] = NameOfPlace
            gogglePlaceMap["vicinity"] = vicinity
            gogglePlaceMap["Lat"] = latitude
            gogglePlaceMap["Lng"] = longitude
            gogglePlaceMap["reference"] = reference
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return gogglePlaceMap
    }

    private fun allNearbyPlaces(jsonArray: JSONArray?): List<HashMap<String, String>?> {
        val counter = jsonArray!!.length()
        val nearbyPlacesList: MutableList<HashMap<String, String>?> = ArrayList()
        var NearbyPlacesMap: HashMap<String, String>? = null
        for (i in 0 until counter) {
            try {
                NearbyPlacesMap = getOnePlace(jsonArray[i] as JSONObject)
                nearbyPlacesList.add(NearbyPlacesMap)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return nearbyPlacesList
    }

    fun parse(JSONData: String?): List<HashMap<String, String>?> {
        var jsonArray: JSONArray? = null
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(JSONData)
            jsonArray = jsonObject.getJSONArray("result")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return allNearbyPlaces(jsonArray)
    }
}