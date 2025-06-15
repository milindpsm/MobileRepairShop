package com.example.mobilerepairshop.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilerepairshop.data.RepairRepository
import com.example.mobilerepairshop.data.model.DashboardStats
import com.example.mobilerepairshop.data.model.Repair
import kotlinx.coroutines.launch

/**
 * ViewModel to provide data to the UI and survive configuration changes.
 * It acts as a bridge between the Repository and the UI.
 */
class RepairViewModel(private val repository: RepairRepository) : ViewModel() {

    // Using LiveData to hold all repairs. The UI will observe this list for changes.
    // .asLiveData() converts the Flow from the repository into a LiveData object.
    val allRepairs: LiveData<List<Repair>> = repository.allRepairs.asLiveData()
    val pendingCount: LiveData<Int> = repository.pendingCount.asLiveData()

    /**
     * Launch a new coroutine to insert the data in a non-blocking way
     */
    fun insert(repair: Repair) = viewModelScope.launch {
        repository.insert(repair)
    }

    /**
     * Launch a new coroutine to update the data in a non-blocking way
     */
    fun update(repair: Repair) = viewModelScope.launch {
        repository.update(repair)
    }

    fun delete(repair: Repair) = viewModelScope.launch {
        repository.delete(repair)
    }


    fun getRepairById(id: Long): LiveData<Repair?> {
        return repository.getRepairById(id).asLiveData()
    }

    fun searchRepairs(query: String): LiveData<List<Repair>> {
        return repository.searchRepairs(query).asLiveData()
    }

    fun getStatsForPeriod(startDate: Long, endDate: Long): LiveData<DashboardStats?> {
        return repository.getStatsForPeriod(startDate, endDate).asLiveData()
    }

}

/**
 * ViewModelFactory to instantiate the RepairViewModel.
 * This is necessary because our ViewModel has a constructor dependency (the repository).
 */
class RepairViewModelFactory(private val repository: RepairRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepairViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepairViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
