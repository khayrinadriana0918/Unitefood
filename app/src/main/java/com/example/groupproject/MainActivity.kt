package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.donor.DonorHomeActivity
import com.example.groupproject.recipient.RecipientActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val donorButton = findViewById<Button>(R.id.donorBtn)
        val recipientButton = findViewById<Button>(R.id.recipientBtn)
        val volunteerButton = findViewById<Button>(R.id.volunteerBtn)

        donorButton.setOnClickListener {
            startActivity(Intent(this, DonorHomeActivity::class.java))
        }

        recipientButton.setOnClickListener {
            startActivity(Intent(this, RecipientActivity::class.java))
        }

        volunteerButton.setOnClickListener {
            // Volunteer page will be connected here
        }
    }
}