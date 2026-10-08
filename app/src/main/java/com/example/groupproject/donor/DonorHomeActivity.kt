package com.example.groupproject.donor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.databinding.ActivityDonorHomeBinding
import android.content.Intent

class DonorHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "UniteFood — Donor"

         binding.cardAddFood.setOnClickListener {
             startActivity(Intent(this, AddFoodActivity::class.java))
    }

         binding.cardMyListings.setOnClickListener {
             startActivity(Intent(this, MyListingsActivity::class.java))
         }

         binding.cardReservations.setOnClickListener {
             startActivity(Intent(this, ReservationsActivity::class.java))
         }

         binding.cardHistory.setOnClickListener {
             startActivity(Intent(this, DonorHistoryActivity::class.java))
         }
    }
}