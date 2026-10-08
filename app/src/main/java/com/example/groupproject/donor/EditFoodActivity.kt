package com.example.groupproject.donor

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivityEditFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class EditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFoodBinding
    private lateinit var db: DatabaseHelper
    private var foodId: Int = -1
    private var expiryDate: String = ""

    private val categories = listOf(
        "Cooked Food", "Bakery", "Fruits", "Vegetables",
        "Dairy", "Canned", "Beverages", "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Edit Listing"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)
        foodId = intent.getIntExtra("foodId", -1)

        if (foodId == -1) {
            Toast.makeText(this, "Invalid food ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Category dropdown
        val catAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categories
        )
        binding.spCategory.setAdapter(catAdapter)
        binding.spCategory.setOnClickListener {
            binding.spCategory.showDropDown()
        }

        // Load data
        loadFood()

        // Listeners
        binding.btnPickExpiry.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { validateAndUpdate() }
        binding.btnDelete.setOnClickListener { confirmDelete() }
    }

    private fun loadFood() {
        val cursor = db.getFoodById(foodId)
        if (cursor.moveToFirst()) {
            binding.etFoodName.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)))
            binding.spCategory.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)), false)
            binding.etQuantity.setText(cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)).toString())
            binding.etUnit.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)))
            binding.etDescription.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)))
            binding.etPickupArea.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_AREA)))
            binding.etPickupAddress.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_ADDRESS)))

            expiryDate = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY_DATE)) ?: ""
            binding.tvExpiry.text = "Expires: $expiryDate"
        }
        cursor.close()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            val picked = Calendar.getInstance().apply { set(y, m, d) }
            expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(picked.time)
            binding.tvExpiry.text = "Expires: $expiryDate"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun validateAndUpdate() {
        val name = binding.etFoodName.text.toString().trim()
        val category = binding.spCategory.text.toString().trim()
        val qtyStr = binding.etQuantity.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val area = binding.etPickupArea.text.toString().trim()
        val address = binding.etPickupAddress.text.toString().trim()

        when {
            name.isEmpty() -> { binding.etFoodName.error = "Required"; return }
            name.length < 3 -> { binding.etFoodName.error = "Min 3 chars"; return }
            category.isEmpty() -> { toast("Select category"); return }
            qtyStr.toIntOrNull() == null || qtyStr.toInt() <= 0 -> {
                binding.etQuantity.error = "Positive number only"; return
            }
            unit.isEmpty() -> { binding.etUnit.error = "Required"; return }
            area.isEmpty() -> { binding.etPickupArea.error = "Required"; return }
            address.isEmpty() -> { binding.etPickupAddress.error = "Required"; return }
            expiryDate.isEmpty() -> { toast("Pick expiry date"); return }
        }

        val rows = db.updateFood(
            foodId, name, category, qtyStr.toInt(), unit, desc,
            area, address, expiryDate
        )

        if (rows > 0) {
            toast("✅ Listing updated")
            finish()
        } else {
            toast("❌ Update failed")
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Listing?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val rows = db.deleteFood(foodId)
                if (rows > 0) {
                    toast("✅ Listing deleted")
                    finish()
                } else {
                    toast("❌ Delete failed")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}