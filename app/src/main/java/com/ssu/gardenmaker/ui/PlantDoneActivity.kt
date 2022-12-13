package com.ssu.gardenmaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ssu.gardenmaker.databinding.ActivityPlantDoneBinding

class PlantDoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantDoneBinding
    private val TAG = "PlantDoneActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlantDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}