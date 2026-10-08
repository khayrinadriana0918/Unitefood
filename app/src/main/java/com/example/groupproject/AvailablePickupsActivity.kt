package com.example.groupproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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
        Pickup(
            "Chicken Rice",
            "Cooked Food",
            "10 portions",
            "Putra Heights",
            "25 Sept, 2:00 PM - 4:00 PM"
        ),
        Pickup(
            "Bread",
            "Bakery",
            "20 packs",
            "Subang Jaya",
            "25 Sept, 4:00 PM - 6:00 PM"
        ),
        Pickup(
            "Fresh Vegetables",
            "Vegetables",
            "15 kg",
            "Shah Alam",
            "26 Sept, 10:00 AM - 12:00 PM"
        ),
        Pickup(
            "Fruit Boxes",
            "Fruits",
            "8 boxes",
            "USJ",
            "26 Sept, 2:00 PM - 4:00 PM"
        )
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
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterPickups()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
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
        val selectedCategory = categorySpinner.selectedItem.toString()

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

            val emptyMessage = TextView(this)

            emptyMessage.text = "No available pickups found."
            emptyMessage.textSize = 16f
            emptyMessage.gravity = Gravity.CENTER
            emptyMessage.setPadding(0, 40, 0, 40)

            pickupContainer.addView(emptyMessage)

            return
        }

        for (pickup in list) {

            val card = LinearLayout(this)

            card.orientation = LinearLayout.VERTICAL
            card.setPadding(20, 20, 20, 20)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 0, 0, 16)

            card.layoutParams = params

            val foodName = TextView(this)
            foodName.text = pickup.foodName
            foodName.textSize = 20f
            foodName.setTextColor(
                getColor(R.color.primary_text)
            )
            foodName.setTypeface(null, android.graphics.Typeface.BOLD)

            val details = TextView(this)

            details.text =
                "Category: ${pickup.category}\n" +
                "Quantity: ${pickup.quantity}\n" +
                "Area: ${pickup.area}\n" +
                "Pickup: ${pickup.pickupTime}"

            details.textSize = 15f
            details.setPadding(0, 8, 0, 8)

            val scheduleButton = Button(this)

           scheduleButton.setOnClickListener {

    val intent = Intent(
        this,
        SchedulePickupActivity::class.java
    )

    intent.putExtra("foodName", pickup.foodName)
    intent.putExtra("category", pickup.category)
    intent.putExtra("quantity", pickup.quantity)
    intent.putExtra("area", pickup.area)

    startActivity(intent)
}

            card.addView(foodName)
            card.addView(details)
            card.addView(scheduleButton)

            pickupContainer.addView(card)
        }
    }
}
