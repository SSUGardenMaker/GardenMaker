package com.ssu.gardenmaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ssu.gardenmaker.databinding.ActivityPlantBookBinding

class PlantBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlantBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}