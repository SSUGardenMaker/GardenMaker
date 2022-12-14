package com.ssu.gardenmaker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.adapter.GridAdapter
import com.ssu.gardenmaker.databinding.ActivityPlantDoneBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent

class PlantDoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantDoneBinding
    private val TAG = "PlantDoneActivity"

    private val plantDoneLists: MutableList<PlantDataContent> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlantDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibPlantDoneBack.setOnClickListener {
            finish()
        }

        setPlantDone()
    }

    private fun setPlantDone() {
        ApplicationClass.retrofitManager.plantDoneCheck(object : RetrofitPlantCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: List<PlantDataContent>) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")
                Toast.makeText(this@PlantDoneActivity, message, Toast.LENGTH_SHORT).show()

                plantDoneLists.addAll(data)

                binding.gridView.adapter = GridAdapter(this@PlantDoneActivity, plantDoneLists)
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                Toast.makeText(this@PlantDoneActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}