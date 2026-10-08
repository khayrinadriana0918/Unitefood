package com.example.groupproject.donor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupproject.adapter.FoodAdapter
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivityReservationsBinding

class ReservationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationsBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FoodAdapter
    private val donorId = "donor_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Reservations"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        adapter = FoodAdapter(this, null) { foodId ->
            // Buka Schedule Pickup
            val intent = Intent(this, SchedulePickupActivity::class.java)
            intent.putExtra("foodId", foodId)
            startActivity(intent)
        }
        binding.rvReservations.layoutManager = LinearLayoutManager(this)
        binding.rvReservations.adapter = adapter

        // Test button — reserve a random Available item
        binding.btnAddTest.setOnClickListener {
            val cursor = db.searchFood(donorId, "All", "", "Available")
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID))
                db.reserveForTest(id, "recipient_test_001")
                Toast.makeText(this, "✅ Reserved item #$id (test)", Toast.LENGTH_SHORT).show()
                loadReservations()
            } else {
                Toast.makeText(this, "Add some food first!", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        loadReservations()
    }

    private fun loadReservations() {
        val cursor = db.getReservedFoods(donorId)
        adapter.swapCursor(cursor)

        if (cursor.count == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvReservations.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvReservations.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadReservations()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}