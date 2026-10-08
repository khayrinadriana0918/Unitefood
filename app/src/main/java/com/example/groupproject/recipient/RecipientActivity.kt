package com.example.groupproject.recipient

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R

class RecipientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient)

        val availableFood= findViewById<LinearLayout>(R.id.availableFood)

        availableFood.setOnClickListener {
            startActivity(
                Intent(this, AvailableFoodRA::class.java)
            )
        }
    }
}