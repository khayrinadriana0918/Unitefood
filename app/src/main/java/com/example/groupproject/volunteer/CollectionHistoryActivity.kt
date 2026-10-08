package com.example.groupproject.volunteer

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R

class CollectionHistoryActivity : AppCompatActivity() {

    private lateinit var historyContainer: LinearLayout
    private lateinit var noHistoryTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_history)

        historyContainer = findViewById(R.id.historyContainer)
        noHistoryTV = findViewById(R.id.noHistoryTV)

        displayHistory()
    }

    override fun onResume() {
        super.onResume()

        if (::historyContainer.isInitialized) {
            displayHistory()
        }
    }

    private fun displayHistory() {

        // Remove old history cards
        while (historyContainer.childCount > 2) {
            historyContainer.removeViewAt(2)
        }

        val completedPickups =
            PickupRepository.scheduledPickups.filter {
                it.status == "Completed"
            }

        if (completedPickups.isEmpty()) {

            noHistoryTV.visibility = View.VISIBLE

            return
        }

        noHistoryTV.visibility = View.GONE

        for (pickup in completedPickups) {

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

            // Collection information
            val detailsTV = TextView(this)

            detailsTV.text =
                "Category: ${pickup.category}\n" +
                "Quantity: ${pickup.quantity}\n" +
                "Collection Area: ${pickup.area}\n" +
                "Date: ${pickup.date}\n" +
                "Time: ${pickup.time}\n" +
                "Status: ${pickup.status}"

            detailsTV.textSize = 15f
            detailsTV.setPadding(0, 10, 0, 10)

            card.addView(foodNameTV)
            card.addView(detailsTV)

            historyContainer.addView(card)
        }
    }
}