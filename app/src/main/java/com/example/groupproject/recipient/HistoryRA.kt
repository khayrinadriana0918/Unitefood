
package com.example.groupproject.recipient

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import com.example.groupproject.data.DatabaseHelper

class HistoryRA : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        val scrollView = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(getColor(R.color.background))
        }

        root.addView(TextView(this).apply {
            text = "History"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(getColor(R.color.primary_text))
        })

        root.addView(TextView(this).apply {
            text = "View your previous food assistance."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 24)
            setTextColor(getColor(R.color.secondary_text))
        })

        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(historyContainer)
        scrollView.addView(root)
        setContentView(scrollView)

        loadHistory()
    }

    private fun loadHistory() {
        historyContainer.removeAllViews()

        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_FOOD,
            null,
            "${DatabaseHelper.COL_RESERVED_BY} = ? AND " +
                    "${DatabaseHelper.COL_STATUS} IN (?, ?)",
            arrayOf("recipient", "Collected", "Expired"),
            null,
            null,
            "${DatabaseHelper.COL_CREATED_AT} DESC"
        )

        if (cursor.count == 0) {
            showMessage("No food assistance history yet.")
            cursor.close()
            return
        }

        while (cursor.moveToNext()) {
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
            ) ?: "Food"

            val quantity = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)
            )

            val unit = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)
            ) ?: ""

            val area = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_AREA)
            ) ?: "Not specified"

            val status = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)
            ) ?: "Unknown"

            addCard(
                "Food: $name\n" +
                        "Quantity: $quantity $unit\n" +
                        "Collection Area: $area\n" +
                        "Status: $status"
            )
        }

        cursor.close()
    }

    private fun addCard(details: String) {
        val card = TextView(this).apply {
            text = details
            textSize = 16f
            setTextColor(getColor(R.color.primary_text))
            setBackgroundColor(getColor(R.color.white))
            setPadding(32, 32, 32, 32)
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 24
        historyContainer.addView(card, params)
    }

    private fun showMessage(message: String) {
        historyContainer.addView(TextView(this).apply {
            text = message
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 48)
            setTextColor(getColor(R.color.secondary_text))
        })
    }

    override fun onResume() {
        super.onResume()
        if (::db.isInitialized && ::historyContainer.isInitialized) {
            loadHistory()
        }
    }
}
