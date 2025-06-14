package com.example.mobilerepairshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels // <-- This is the important import that fixes the error
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilerepairshop.databinding.ActivityMainBinding
import com.example.mobilerepairshop.ui.adapter.RepairAdapter
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModel
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Lazily initialize the ViewModel using the factory
    private val repairViewModel: RepairViewModel by viewModels {
        RepairViewModelFactory((application as RepairShopApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        val adapter = RepairAdapter { repair ->
            // This is the click listener for each item in the list
            val intent = Intent(this, RepairDetailActivity::class.java).apply {
                putExtra(RepairDetailActivity.REPAIR_ID, repair.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewRepairs.adapter = adapter
        binding.recyclerViewRepairs.layoutManager = LinearLayoutManager(this)

        // Observe the LiveData from the ViewModel
        repairViewModel.allRepairs.observe(this) { repairs ->
            // Update the cached copy of the repairs in the adapter.
            repairs?.let { adapter.submitList(it) }
        }

        // Set click listener for the Floating Action Button
        binding.fabAddRepair.setOnClickListener {
            val intent = Intent(this, AddRepairActivity::class.java)
            startActivity(intent)
        }
    }
}
