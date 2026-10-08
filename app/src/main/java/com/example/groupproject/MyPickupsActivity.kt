package com.example.groupproject

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyPickupsActivity : AppCompatActivity() {

    private lateinit var myPickupsContainer: LinearLayout
    private lateinit var noPickupsTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pickups)

        myPickupsContainer = findViewById(R.id.myPickupsContainer)
        noPickupsTV = findViewById(R.id.noPickupsTV)

        displayPickups()
    }

    override fun onResume() {
        super.onResume()

        if (::myPickupsContainer.isInitialized) {
            displayPickups()
        }
    }

    private fun displayPickups() {

        // Remove old pickup cards
        while (myPickupsContainer.childCount > 2) {
            myPickupsContainer.removeViewAt(2)
        }

        val pickups = PickupRepository.scheduledPickups

        if (pickups.isEmpty()) {
            noPickupsTV.visibility = View.VISIBLE
            return
        }

        noPickupsTV.visibility = View.GONE

        for (pickup in pickups) {

            val card = LinearLayout(this)

            card.orientation = LinearLayout.VERTICAL
            card.setPadding(20, 20, 20, 20)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 20, 0, 0)

            card.layoutParams = params

            // Food name
            val foodNameTV = TextView(this)

            foodNameTV.text = pickup.foodName
            foodNameTV.textSize = 20f
            foodNameTV.setTextColor(
                getColor(R.color.primary_text)
            )
            foodNameTV.setTypeface(
                null,
                Typeface.BOLD
            )

            // Details
            val detailsTV = TextView(this)

            detailsTV.text =
                "Category: ${pickup.category}\n" +
                "Quantity: ${pickup.quantity}\n" +
                "Area: ${pickup.area}\n" +
                "Date: ${pickup.date}\n" +
                "Time: ${pickup.time}\n" +
                "Status: ${pickup.status}"

            detailsTV.textSize = 15f
            detailsTV.setPadding(0, 10, 0, 10)

            // Update status button
            val statusButton = Button(this)

            statusButton.text = "Update Status"

            statusButton.setOnClickListener {

                updatePickupStatus(pickup)
            }

            card.addView(foodNameTV)
            card.addView(detailsTV)
            card.addView(statusButton)

            myPickupsContainer.addView(card)
        }
    }

    private fun updatePickupStatus(pickup: PickupData) {

        when (pickup.status) {

            "Scheduled" -> {
                pickup.status = "On the Way"
            }

            "On the Way" -> {
                pickup.status = "Collected"
            }

            "Collected" -> {
                pickup.status = "Completed"
            }

            "Completed" -> {
                Toast.makeText(
                    this,
                    "This pickup is already completed.",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        displayPickups()

        Toast.makeText(
            this,
            "Status updated to ${pickup.status}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
