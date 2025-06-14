package com.example.mobilerepairshop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mobilerepairshop.data.model.Repair
import com.example.mobilerepairshop.databinding.ActivityRepairDetailBinding
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModel
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepairDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepairDetailBinding
    private var currentRepair: Repair? = null
    private val repairViewModel: RepairViewModel by viewModels {
        RepairViewModelFactory((application as RepairShopApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repairId = intent.getLongExtra(REPAIR_ID, -1L)
        if (repairId == -1L) {
            Toast.makeText(this, "Error: Invalid Repair ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupStatusSpinner()

        repairViewModel.getRepairById(repairId).observe(this) { repair ->
            repair?.let {
                // Only bind data the first time to avoid overwriting user edits
                if (currentRepair == null) {
                    currentRepair = it
                    bindDataToViews(it)
                }
            }
        }

        binding.buttonSaveChanges.setOnClickListener {
            saveChanges()
        }

        binding.buttonSendMessage.setOnClickListener {
            sendWhatsAppMessage()
        }
    }

    private fun bindDataToViews(repair: Repair) {
        binding.detailTitle.text = "Repair ID: ${repair.id}"
        binding.detailCustomerName.text = "Customer: ${repair.customerName}"
        binding.detailCustomerContact.text = "Contact: ${repair.customerContact}"
        binding.detailAlternateContact.text = "Alt Contact: ${repair.alternateContact ?: "N/A"}"
        binding.detailImei.text = "IMEI: ${repair.imeiNumber ?: "N/A"}"
        binding.detailDateAdded.text = "Date Added: ${formatDate(repair.dateAdded)}"

        if (repair.imagePath != null) {
            Glide.with(this)
                .load(File(repair.imagePath))
                .into(binding.detailImageViewPhone)
        }

        // Set the text in the EditTexts
        binding.detailTotalCost.setText(repair.totalCost.toString())
        binding.detailAdvanceTaken.setText(repair.advanceTaken.toString())
        updateRemainingDue()

        val statusArray = resources.getStringArray(R.array.status_array)
        val statusPosition = statusArray.indexOf(repair.status)
        if (statusPosition >= 0) {
            binding.spinnerStatus.setSelection(statusPosition)
        }
    }

    // NEW: Function to calculate and display remaining due
    private fun updateRemainingDue() {
        val totalCost = binding.detailTotalCost.text.toString().toDoubleOrNull() ?: 0.0
        val advanceTaken = binding.detailAdvanceTaken.text.toString().toDoubleOrNull() ?: 0.0
        val remainingDue = totalCost - advanceTaken
        binding.detailRemainingDue.text = "Remaining Due: ₹${"%.2f".format(remainingDue)}"
    }

    private fun setupStatusSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = adapter
        }
    }

    // MODIFIED: This function now saves all changes, not just status
    private fun saveChanges() {
        val selectedStatus = binding.spinnerStatus.selectedItem.toString()
        val totalCostText = binding.detailTotalCost.text.toString()
        val advanceTakenText = binding.detailAdvanceTaken.text.toString()

        if (totalCostText.isEmpty() || advanceTakenText.isEmpty()) {
            Toast.makeText(this, "Cost and Advance fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        currentRepair?.let { repair ->
            repair.status = selectedStatus
            repair.totalCost = totalCostText.toDouble()
            repair.advanceTaken = advanceTakenText.toDouble()

            if (selectedStatus == getString(R.string.status_out) && repair.dateCompleted == null) {
                repair.dateCompleted = System.currentTimeMillis()
            }

            repairViewModel.update(repair)
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
            finish() // Go back to the dashboard after saving
        }
    }

    private fun sendWhatsAppMessage() {
        currentRepair?.let { repair ->
            val alternateContact = repair.alternateContact
            if (alternateContact.isNullOrEmpty()) {
                Toast.makeText(this, "No alternate contact number available to send message.", Toast.LENGTH_LONG).show()
                return
            }
            if(repair.status != getString(R.string.status_out)) {
                Toast.makeText(this, "Can only send message for 'Out' status repairs.", Toast.LENGTH_LONG).show()
                return
            }

            val remainingDue = repair.totalCost - repair.advanceTaken
            val message = "Hello! Your mobile repair is complete. The pending amount is ₹${"%.2f".format(remainingDue)}. Please collect your device from the shop. Thank you."

            val formattedNumber = "+91$alternateContact"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}")

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp is not installed on this device.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    companion object {
        const val REPAIR_ID = "repair_id"
    }
}
