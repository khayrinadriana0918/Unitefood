package com.example.groupproject.donor

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupproject.adapter.FoodAdapter
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivityMyListingsBinding

class MyListingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyListingsBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FoodAdapter
    private val donorId = "donor_001"

    private val categories = listOf(
        "All", "Cooked Food", "Bakery", "Fruits", "Vegetables",
        "Dairy", "Canned", "Beverages", "Other"
    )
    private val statuses = listOf(
        "All", "Available", "Reserved", "Collected", "Expired"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Listings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        // Adapter
        adapter = FoodAdapter(this, null) { foodId ->
            // Bila tekan item — buka edit screen (nanti kita buat)
            // Sementara: papar Toast je
            android.widget.Toast.makeText(
                this, "Food ID: $foodId", android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvListings.layoutManager = LinearLayoutManager(this)
        binding.rvListings.adapter = adapter

        // Category filter dropdown
        val catAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, categories
        )
        binding.spFilterCategory.setAdapter(catAdapter)
        binding.spFilterCategory.setOnClickListener {
            binding.spFilterCategory.showDropDown()
        }
        binding.spFilterCategory.setText("All", false)

        // Status filter dropdown
        val statusAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, statuses
        )
        binding.spFilterStatus.setAdapter(statusAdapter)
        binding.spFilterStatus.setOnClickListener {
            binding.spFilterStatus.showDropDown()
        }
        binding.spFilterStatus.setText("All", false)

        // Listeners — auto reload bila berubah
        binding.etSearchArea.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { loadListings() }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        binding.spFilterCategory.setOnItemClickListener { _, _, _, _ ->
            loadListings()
        }
        binding.spFilterStatus.setOnItemClickListener { _, _, _, _ ->
            loadListings()
        }

        loadListings()
    }

    private fun loadListings() {
        val category = binding.spFilterCategory.text.toString().ifEmpty { "All" }
        val status = binding.spFilterStatus.text.toString().ifEmpty { "All" }
        val area = binding.etSearchArea.text.toString().trim()

        val cursor: Cursor = db.searchFood(donorId, category, area, status)
        adapter.swapCursor(cursor)

        // Empty state
        if (cursor.count == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvListings.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvListings.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadListings()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}