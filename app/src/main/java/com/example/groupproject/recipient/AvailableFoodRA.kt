package com.example.groupproject.recipient

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R

class AvailableFoodRA : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_food_r)

        val claimFoodBtn = findViewById<Button>(R.id.claimFoodBtn)

        claimFoodBtn.setOnClickListener {
            Toast.makeText(
                this,
                "Food Claimed! Please proceed to pickup location.",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}