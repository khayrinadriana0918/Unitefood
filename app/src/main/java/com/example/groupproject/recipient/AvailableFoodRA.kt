
package com.example.groupproject.recipient

import android.content.ContentValues
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import com.example.groupproject.data.DatabaseHelper

class AvailableFoodRA : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var foodContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        val scrollView = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(getColor(R.color.background))
        }

        val title = TextView(this).apply {
            text = "Available Food"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(getColor(R.color.primary_text))
        }

        val subtitle = TextView(this).apply {
            text = "Browse food donated by our community."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 24)
            setTextColor(getColor(R.color.secondary_text))
        }

        foodContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(foodContainer)
        scrollView.addView(root)
        setContentView(scrollView)

        loadAvailableFood()
    }

    private fun loadAvailableFood() {
        foodContainer.removeAllViews()

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_FOOD,
            null,
            "${DatabaseHelper.COL_STATUS} = ?",
            arrayOf("Available"),
            null,
            null,
            "${DatabaseHelper.COL_CREATED_AT} DESC"
        )

        if (cursor.count == 0) {
            cursor.close()
            showSampleFood()
            return
        }

        while (cursor.moveToNext()) {
            val id = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)
            )
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
            ) ?: "Food"
            val category = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)
            ) ?: "Not specified"
            val quantity = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)
            )
            val unit = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)
            ) ?: ""
            val area = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_AREA)
            ) ?: "Not specified"
            val address = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_ADDRESS)
            ) ?: ""

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
                setBackgroundColor(getColor(R.color.white))
            }

            card.addView(TextView(this).apply {
                text = name
                textSize = 20f
                setTextColor(getColor(R.color.primary_text))
            })

            card.addView(TextView(this).apply {
                text = "Category: $category\n" +
                        "Quantity: $quantity $unit\n" +
                        "Collection Area: $area\n" +
                        "Address: $address"
                textSize = 15f
                setTextColor(getColor(R.color.secondary_text))
                setPadding(0, 12, 0, 12)
            })

            val claimButton = Button(this).apply {
                text = "Claim Food"
                setBackgroundColor(getColor(R.color.button))
                setTextColor(getColor(R.color.white))
                setOnClickListener {
                    claimFood(id)
                }
            }

            card.addView(claimButton)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 24
            foodContainer.addView(card, params)
        }

        cursor.close()
    }

    private fun showSampleFood() {
        val samples = listOf(
            Triple("Cooked Meals", "10 portions", "Shah Alam"),
            Triple("Fresh Vegetables", "5 packs", "Section 7, Shah Alam"),
            Triple("Bread and Pastries", "12 packs", "Klang"),
            Triple("Rice and Curry", "8 portions", "Petaling Jaya")
        )

        samples.forEach { (name, quantity, area) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
                setBackgroundColor(getColor(R.color.white))
            }

            card.addView(TextView(this).apply {
                text = name
                textSize = 20f
                setTextColor(getColor(R.color.primary_text))
            })

            card.addView(TextView(this).apply {
                text = "Quantity: $quantity\nCollection Area: $area"
                textSize = 15f
                setPadding(0, 12, 0, 12)
                setTextColor(getColor(R.color.secondary_text))
            })

            card.addView(Button(this).apply {
                text = "View Details"
                setOnClickListener {
                    Toast.makeText(
                        this@AvailableFoodRA,
                        "$name — sample food item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 24
            foodContainer.addView(card, params)
        }
    }


    private fun claimFood(foodId: Int) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_STATUS, "Reserved")
            put(DatabaseHelper.COL_RESERVED_BY, "recipient")
        }

        val updated = db.writableDatabase.update(
            DatabaseHelper.TABLE_FOOD,
            values,
            "${DatabaseHelper.COL_ID} = ? AND " +
                    "${DatabaseHelper.COL_STATUS} = ?",
            arrayOf(foodId.toString(), "Available")
        )

        if (updated > 0) {
            Toast.makeText(
                this,
                "Food claimed successfully. Check Pickup for details.",
                Toast.LENGTH_LONG
            ).show()
            loadAvailableFood()
        } else {
            Toast.makeText(
                this,
                "This food is no longer available.",
                Toast.LENGTH_LONG
            ).show()
            loadAvailableFood()
        }
    }

    private fun addMessage(message: String) {
        foodContainer.addView(TextView(this).apply {
            text = message
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 48)
            setTextColor(getColor(R.color.secondary_text))
        })
    }

    override fun onResume() {
        super.onResume()
        if (::db.isInitialized && ::foodContainer.isInitialized) {
            loadAvailableFood()
        }
    }
}
