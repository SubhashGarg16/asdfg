package com.example.mapsworking

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DownloadURL {
    @Throws(IOException::class)
    fun readURL(placeURL: String?): String {
        var httpURLConnection: HttpURLConnection? = null
        var Data = ""
        var inputStream: InputStream? = null
        try {
            val url = URL(placeURL)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            inputStream = httpURLConnection!!.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuffer = StringBuffer()
            var Line: String? = ""
            while (bufferedReader.readLine().also { Line = it } != null) {
                stringBuffer.append(Line)
            }
            Data = stringBuffer.toString()
            bufferedReader.close()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream!!.close()
            httpURLConnection!!.disconnect()
        }
        return Data
    }
}