
package com.example.groupproject.volunteer

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R

data class Pickup(
    val foodName: String,
    val category: String,
    val quantity: String,
    val area: String,
    val pickupTime: String
)

class AvailablePickupsActivity : AppCompatActivity() {

    private lateinit var searchET: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var pickupContainer: LinearLayout

    private val pickups = listOf(
        Pickup("Chicken Rice", "Cooked Food", "10 portions",
            "Putra Heights", "25 Sept, 2:00 PM - 4:00 PM"),
        Pickup("Bread", "Bakery", "20 packs",
            "Subang Jaya", "25 Sept, 4:00 PM - 6:00 PM"),
        Pickup("Fresh Vegetables", "Vegetables", "15 kg",
            "Shah Alam", "26 Sept, 10:00 AM - 12:00 PM"),
        Pickup("Fruit Boxes", "Fruits", "8 boxes",
            "USJ", "26 Sept, 2:00 PM - 4:00 PM")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_pickups)

        searchET = findViewById(R.id.searchET)
        categorySpinner = findViewById(R.id.categorySpinner)
        pickupContainer = findViewById(R.id.pickupContainer)

        setupCategorySpinner()
        displayPickups(pickups)

        searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                filterPickups()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    filterPickups()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf(
            "All Categories",
            "Cooked Food",
            "Bakery",
            "Vegetables",
            "Fruits"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        categorySpinner.adapter = adapter
    }

    private fun filterPickups() {
        val searchText = searchET.text.toString().trim().lowercase()
        val selectedCategory =
            categorySpinner.selectedItem?.toString() ?: "All Categories"

        val filteredList = pickups.filter { pickup ->
            val matchesSearch =
                pickup.foodName.lowercase().contains(searchText) ||
                        pickup.area.lowercase().contains(searchText)

            val matchesCategory =
                selectedCategory == "All Categories" ||
                        pickup.category == selectedCategory

            matchesSearch && matchesCategory
        }

        displayPickups(filteredList)
    }

    private fun displayPickups(list: List<Pickup>) {
        pickupContainer.removeAllViews()

        if (list.isEmpty()) {
            pickupContainer.addView(TextView(this).apply {
                text = "No available pickups found."
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (pickup in list) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            card.layoutParams = params

            val foodName = TextView(this).apply {
                text = pickup.foodName
                textSize = 20f
                setTextColor(getColor(R.color.primary_text))
                setTypeface(null, Typeface.BOLD)
            }

            val details = TextView(this).apply {
                text = "Category: ${pickup.category}\n" +
                        "Quantity: ${pickup.quantity}\n" +
                        "Area: ${pickup.area}\n" +
                        "Pickup: ${pickup.pickupTime}"
                textSize = 15f
                setPadding(0, 8, 0, 8)
            }

            val scheduleButton = Button(this).apply {
                text = "Schedule Pickup"
                setOnClickListener {
                    val intent = Intent(
                        this@AvailablePickupsActivity,
                        SchedulePickupActivity::class.java
                    )
                    intent.putExtra("foodName", pickup.foodName)
                    intent.putExtra("category", pickup.category)
                    intent.putExtra("quantity", pickup.quantity)
                    intent.putExtra("area", pickup.area)
                    startActivity(intent)
                }
            }

            card.addView(foodName)
            card.addView(details)
            card.addView(scheduleButton)
            pickupContainer.addView(card)
        }
    }
}
