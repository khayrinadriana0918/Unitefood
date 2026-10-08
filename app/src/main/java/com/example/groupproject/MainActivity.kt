package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.groupproject.databinding.ActivityMainBinding
import com.example.groupproject.donor.DonorHomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenDonor.setOnClickListener {
            startActivity(Intent(this, DonorHomeActivity::class.java))
        }
    }
}