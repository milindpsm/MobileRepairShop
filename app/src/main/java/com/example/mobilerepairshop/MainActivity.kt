package com.example.mobilerepairshop

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilerepairshop.data.model.Repair
import com.example.mobilerepairshop.databinding.ActivityMainBinding
import com.example.mobilerepairshop.ui.adapter.RepairAdapter
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModel
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModelFactory
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repairViewModel: RepairViewModel by viewModels {
        RepairViewModelFactory((application as RepairShopApplication).repository)
    }
    private lateinit var adapter: RepairAdapter
    private var currentRepairListObserver: LiveData<List<Repair>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        observeData()

        updateDashboardForPeriod("Last 7 Days")
    }

    // --- NEW: Inflate the menu with the refresh icon ---
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // --- NEW: Handle menu item clicks ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                // When refresh is clicked, re-run the dashboard update
                // using the currently selected period on the button.
                val currentPeriod = binding.buttonDateFilter.text.toString()
                updateDashboardForPeriod(currentPeriod)
                Toast.makeText(this, "Dashboard refreshed", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = RepairAdapter { repair ->
            val intent = Intent(this, RepairDetailActivity::class.java).apply {
                putExtra(RepairDetailActivity.REPAIR_ID, repair.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewRepairs.adapter = adapter
        binding.recyclerViewRepairs.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.fabAddRepair.setOnClickListener {
            val intent = Intent(this, AddRepairActivity::class.java)
            startActivity(intent)
        }

        binding.buttonDateFilter.setOnClickListener { view ->
            showDateFilterMenu(view)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    observeRepairList(repairViewModel.allRepairs)
                } else {
                    observeRepairList(repairViewModel.searchRepairs(newText))
                }
                return true
            }
        })
    }

    private fun observeData() {
        observeRepairList(repairViewModel.allRepairs)
        repairViewModel.pendingCount.observe(this) { count ->
            binding.statPendingCount.text = count?.toString() ?: "0"
        }
    }

    private fun observeRepairList(repairsLiveData: LiveData<List<Repair>>) {
        currentRepairListObserver?.removeObservers(this)

        currentRepairListObserver = repairsLiveData
        currentRepairListObserver?.observe(this) { repairs ->
            repairs?.let { adapter.submitList(it) }
        }
    }

    private fun showDateFilterMenu(anchorView: android.view.View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.date_filter_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val period = menuItem.title.toString()
            binding.buttonDateFilter.text = period
            updateDashboardForPeriod(period)
            true
        }
        popupMenu.show()
    }

    private fun updateDashboardForPeriod(period: String) {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        when (period) {
            "Today" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0)
            }
            "Yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0)
                val endOfYesterday = Calendar.getInstance()
                endOfYesterday.add(Calendar.DAY_OF_YEAR, -1)
                endOfYesterday.set(Calendar.HOUR_OF_DAY, 23); endOfYesterday.set(Calendar.MINUTE, 59); endOfYesterday.set(Calendar.SECOND, 59)
                observeStats(calendar.timeInMillis, endOfYesterday.timeInMillis)
                return
            }
            "Last 7 Days" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "Last 30 Days" -> calendar.add(Calendar.DAY_OF_YEAR, -30)
        }

        val startDate = calendar.timeInMillis
        observeStats(startDate, endDate)
    }

    // This is just the observeStats function from inside your MainActivity.kt
// Replace the existing observeStats function with this one for now.
    private fun observeStats(startDate: Long, endDate: Long) {
        repairViewModel.getStatsForPeriod(startDate, endDate).observe(this) { stats ->
            binding.statInCount.text = stats?.inCount?.toString() ?: "0"
            binding.statOutCount.text = stats?.outCount?.toString() ?: "0"

            // --- TEMPORARY FIX to make the app compile ---
            // We will add the correct logic in the next step.
            binding.statEstimatedRevenue.text = "₹0.00"
            binding.statActualRevenue.text = "₹0.00"
            binding.statUpcomingRevenue.text = "₹0.00"
        }
    }

}
