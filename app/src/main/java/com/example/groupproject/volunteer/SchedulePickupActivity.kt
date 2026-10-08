package com.example.groupproject.volunteer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.R
import java.util.Calendar

class SchedulePickupActivity : AppCompatActivity() {

    private lateinit var foodNameTV: TextView
    private lateinit var foodDetailsTV: TextView
    private lateinit var dateBtn: Button
    private lateinit var timeBtn: Button
    private lateinit var confirmPickupBtn: Button

    private var selectedDate = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_pickup)

        foodNameTV = findViewById(R.id.foodNameTV)
        foodDetailsTV = findViewById(R.id.foodDetailsTV)
        dateBtn = findViewById(R.id.dateBtn)
        timeBtn = findViewById(R.id.timeBtn)
        confirmPickupBtn = findViewById(R.id.confirmPickupBtn)

        val foodName = intent.getStringExtra("foodName")
        val category = intent.getStringExtra("category")
        val quantity = intent.getStringExtra("quantity")
        val area = intent.getStringExtra("area")

        foodNameTV.text = foodName

        foodDetailsTV.text =
            "Category: $category\n" +
                    "Quantity: $quantity\n" +
                    "Collection Area: $area"

        dateBtn.setOnClickListener {
            showDatePicker()
        }

        timeBtn.setOnClickListener {
            showTimePicker()
        }

        confirmPickupBtn.setOnClickListener {


            if (selectedDate.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select a pickup date.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select a pickup time.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val foodName = intent.getStringExtra("foodName") ?: ""
            val category = intent.getStringExtra("category") ?: ""
            val quantity = intent.getStringExtra("quantity") ?: ""
            val area = intent.getStringExtra("area") ?: ""

            val newPickup = PickupData(
                foodName = foodName,
                category = category,
                quantity = quantity,
                area = area,
                date = selectedDate,
                time = selectedTime,
                status = "Scheduled"
            )

            PickupRepository.scheduledPickups.add(newPickup)

            Toast.makeText(
                this,
                "Pickup scheduled successfully!",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                selectedDate =
                    "$selectedDay/${selectedMonth + 1}/$selectedYear"

                dateBtn.text = selectedDate
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun showTimePicker() {

        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->

                selectedTime =
                    String.format(
                        "%02d:%02d",
                        selectedHour,
                        selectedMinute
                    )

                timeBtn.text = selectedTime
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }
}
