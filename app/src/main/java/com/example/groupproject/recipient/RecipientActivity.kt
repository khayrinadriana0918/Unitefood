package com.example.groupproject.recipient

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Geocoder

class RecipientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient)

        val currentLocationText= findViewById<TextView>(R.id.currentLocationText)

        if (ContextCompat.checkSelfPermission(
            this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }else{
            showCurrentLocation(currentLocationText)
        }

        val availableFood= findViewById<LinearLayout>(R.id.availableFood)

        availableFood.setOnClickListener {
            startActivity(
                Intent(this, AvailableFoodRA::class.java)
            )
        }

        val pickup = findViewById<LinearLayout>(R.id.pickup)

        pickup.setOnClickListener {
            startActivity(
                Intent(this, PickupRA::class.java)
            )
        }

        val history= findViewById<LinearLayout>(R.id.history)

        history.setOnClickListener {
            startActivity(
                Intent(this, HistoryRA::class.java)
            )
        }
    }

    private fun showCurrentLocation(locationText: TextView){

        val locationManager=
            getSystemService(LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationText.text = "Location is turned off"
            return
        }
        if (ActivityCompat.checkSelfPermission(
            this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            return
        }
        val location= locationManager.getLastKnownLocation(
            LocationManager.GPS_PROVIDER
        )
        if (location != null){
            val geocoder= Geocoder(this)

            val addresses= geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (!addresses.isNullOrEmpty()){
                locationText.text= addresses[0].getAddressLine(0)
            }else{
                locationText.text= "Unable to find address."
            }
        }else{
            locationText.text= "Unable to get current location"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            val currentLocationText =
                findViewById<TextView>(R.id.currentLocationText)

            showCurrentLocation(currentLocationText)
        }
    }

}