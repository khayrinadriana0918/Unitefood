package com.example.groupproject.donor

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivityAddFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var db: DatabaseHelper
    private var expiryDate: String = ""
    private val donorId = "donor_001"  // sementara

    private val categories = listOf(
        "Cooked Food",
        "Bakery",
        "Fruits",
        "Vegetables",
        "Dairy",
        "Canned",
        "Beverages",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Surplus Food"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        // Category dropdown
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.spCategory.setAdapter(categoryAdapter)
        binding.spCategory.setOnClickListener {
            binding.spCategory.showDropDown()
        }

        // Date picker
        binding.btnPickExpiry.setOnClickListener { showDatePicker() }

        // Save button
        binding.btnSave.setOnClickListener { validateAndSave() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(picked.time)
                binding.tvExpiry.text = "Expires: $expiryDate"
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        // Halang tarikh lampau
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun validateAndSave() {
        val name = binding.etFoodName.text.toString().trim()
        val category = binding.spCategory.text.toString().trim()
        val qtyStr = binding.etQuantity.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val pickupArea = binding.etPickupArea.text.toString().trim()
        val pickupAddress = binding.etPickupAddress.text.toString().trim()

        // Validation
        when {
            name.isEmpty() -> {
                binding.etFoodName.error = "Food name is required"
                binding.etFoodName.requestFocus()
                return
            }
            name.length < 3 -> {
                binding.etFoodName.error = "Name must be at least 3 characters"
                return
            }
            category.isEmpty() -> {
                toast("Please select a category")
                return
            }
            qtyStr.isEmpty() -> {
                binding.etQuantity.error = "Quantity is required"
                binding.etQuantity.requestFocus()
                return
            }
            qtyStr.toIntOrNull() == null || qtyStr.toInt() <= 0 -> {
                binding.etQuantity.error = "Quantity must be a positive number"
                return
            }
            unit.isEmpty() -> {
                binding.etUnit.error = "Unit is required"
                binding.etUnit.requestFocus()
                return
            }
            pickupArea.isEmpty() -> {
                binding.etPickupArea.error = "Pickup area is required"
                binding.etPickupArea.requestFocus()
                return
            }
            pickupAddress.isEmpty() -> {
                binding.etPickupAddress.error = "Pickup address is required"
                binding.etPickupAddress.requestFocus()
                return
            }
            expiryDate.isEmpty() -> {
                toast("Please select expiry date")
                return
            }
        }

        // Save to SQLite
        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val result = db.insertFood(
            donorId = donorId,
            name = name,
            category = category,
            quantity = qtyStr.toInt(),
            unit = unit,
            description = description,
            pickupArea = pickupArea,
            pickupAddress = pickupAddress,
            expiryDate = expiryDate,
            createdAt = createdAt
        )

        if (result != -1L) {
            toast("✅ Food listed successfully")
            finish()
        } else {
            toast("❌ Failed to save. Try again.")
        }
    }

    // Back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}