
package com.example.groupproject.donor

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupproject.adapter.FoodAdapter
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ActivityDonorHistoryBinding

class DonorHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorHistoryBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FoodAdapter

    private val donorId = "donor_001"
    private val filters = listOf("All", "Reserved", "Collected", "Expired")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Donation History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DatabaseHelper(this)

        adapter = FoodAdapter(this, null) { }
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        val filterAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            filters
        )

        binding.spHistoryFilter.setAdapter(filterAdapter)
        binding.spHistoryFilter.setText("All", false)

        binding.spHistoryFilter.setOnClickListener {
            binding.spHistoryFilter.showDropDown()
        }

        binding.spHistoryFilter.setOnItemClickListener { _, _, _, _ ->
            loadHistory()
        }

        loadHistory()
    }

    private fun loadHistory() {
        val selected =
            binding.spHistoryFilter.text.toString().ifEmpty { "All" }

        val filtered: Cursor = if (selected == "All") {
            db.getDonorHistory(donorId)
        } else {
            db.readableDatabase.query(
                DatabaseHelper.TABLE_FOOD,
                null,
                "${DatabaseHelper.COL_DONOR_ID} = ? AND " +
                        "${DatabaseHelper.COL_STATUS} = ?",
                arrayOf(donorId, selected),
                null,
                null,
                "${DatabaseHelper.COL_CREATED_AT} DESC"
            )
        }

        adapter.swapCursor(filtered)

        if (filtered.count == 0) {
            binding.tvHistoryEmpty.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvHistoryEmpty.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        binding.rvHistory.adapter = null
        super.onDestroy()
    }
}
