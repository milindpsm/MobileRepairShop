package com.example.mobilerepairshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilerepairshop.databinding.ActivityRepairDetailBinding

class RepairDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepairDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: We will implement the logic to get the repair ID,
        // fetch data from the ViewModel, and display it here in a later task.
    }

    companion object {
        const val REPAIR_ID = "repair_id"
    }
}
