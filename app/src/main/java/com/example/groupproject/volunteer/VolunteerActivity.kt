package com.example.groupproject.volunteer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R

class VolunteerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer)

        val availablePickupsBtn =
            findViewById<Button>(R.id.availablePickupsBtn)

        val myPickupsBtn =
            findViewById<Button>(R.id.myPickupsBtn)

        val historyBtn =
            findViewById<Button>(R.id.historyBtn)

        availablePickupsBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AvailablePickupsActivity::class.java
                )
            )
        }

        myPickupsBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MyPickupsActivity::class.java
                )
            )
        }

        historyBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CollectionHistoryActivity::class.java
                )
            )
        }
    }
}