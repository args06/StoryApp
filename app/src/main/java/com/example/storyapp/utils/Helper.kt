package com.example.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import com.example.storyapp.data.local.entity.StoryEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Helper {

    private val timeStamp: String = SimpleDateFormat(
        "dd-MMM-yyyy", Locale.US
    ).format(System.currentTimeMillis())

    fun convertDateTime(item: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter =
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault())
        val formattedDate = parser.parse(item)?.let { formatter.format(it) }
        return formattedDate.toString()
    }

    fun checkTheme(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun constructAuthToken(token: String): String = "Bearer $token"

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun rotateFile(file: File, isBackCamera: Boolean = false) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotation = if (isBackCamera) 90f else -90f
        matrix.postRotate(rotation)
        if (!isBackCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileImage(file: File): File {
        val MAXIMAL_SIZE = 1000000

        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun getLatLngFormat(lat: Double?, lon: Double?) = LatLng(lat ?: 0.0, lon ?: 0.0)

    fun placeMarkerOnMap(mMap: GoogleMap, storyData: List<StoryEntity>) {
        val builder = LatLngBounds.Builder()
        storyData.forEach { storyItem ->
            val position = getLatLngFormat(
                storyItem.lat, storyItem.lon
            )
            builder.include(position)
            val markerOptions = MarkerOptions().position(position)
            val name = storyItem.name
            val createdAt = convertDateTime(storyItem.createdAt)
            markerOptions.title(name)
            markerOptions.snippet(createdAt)
            mMap.addMarker(markerOptions)
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300))
    }

    fun getCityName(context: Context, lat: Double, lon: Double): String {
        var cityName: String?
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat,lon,1)
        cityName = address?.get(0)?.subAdminArea
        if (cityName == null){
            cityName = address?.get(0)?.adminArea
        }
        return cityName.toString()
    }

}