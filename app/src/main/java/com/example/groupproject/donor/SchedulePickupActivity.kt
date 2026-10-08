package com.example.groupproject.donor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivitySchedulePickupBinding
import java.text.SimpleDateFormat
import java.util.*

class SchedulePickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySchedulePickupBinding
    private lateinit var db: DatabaseHelper
    private var foodId: Int = -1
    private var dateStr = ""
    private var timeStr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulePickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Schedule Pickup"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        foodId = intent.getIntExtra("foodId", -1)

        if (foodId == -1) {
            Toast.makeText(this, "Invalid food ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadFoodName()

        binding.btnPickDate.setOnClickListener { showDatePicker() }
        binding.btnPickTime.setOnClickListener { showTimePicker() }
        binding.btnConfirm.setOnClickListener { confirmPickup() }
    }

    private fun loadFoodName() {
        val cursor = db.getFoodById(foodId)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
            binding.tvFoodName.text = name
        }
        cursor.close()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(this, { _, y, m, d ->
            dateStr = String.format("%04d-%02d-%02d", y, m + 1, d)
            binding.tvDate.text = "Date: $dateStr"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, h, min ->
            timeStr = String.format("%02d:%02d", h, min)
            binding.tvTime.text = "Time: $timeStr"
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun confirmPickup() {
        if (dateStr.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "Please pick date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val pickupDateTime = "$dateStr $timeStr"
        // Status tukar ke "Collected" (atau boleh guna "Scheduled" kalau nak)
        val rows = db.schedulePickup(foodId, pickupDateTime, "Collected")

        if (rows > 0) {
            Toast.makeText(this,
                "✅ Pickup scheduled: $pickupDateTime", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "❌ Failed to schedule", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}