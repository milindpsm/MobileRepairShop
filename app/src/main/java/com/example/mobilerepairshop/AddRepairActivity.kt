package com.example.mobilerepairshop

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.mobilerepairshop.data.model.Repair
import com.example.mobilerepairshop.databinding.ActivityAddRepairBinding
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModel
import com.example.mobilerepairshop.ui.viewmodel.RepairViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRepairActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRepairBinding
    private var latestTmpUri: Uri? = null
    private var latestImageSavedPath: String? = null

    // Get a reference to the ViewModel
    private val repairViewModel: RepairViewModel by viewModels {
        RepairViewModelFactory((application as RepairShopApplication).repository)
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(binding.imageViewPhone)
                // The latestImageSavedPath is already set when the URI is created
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCapturePhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.buttonSaveRepair.setOnClickListener {
            saveRepair()
        }
    }

    private fun saveRepair() {
        // Get text from EditTexts
        val customerName = binding.editTextCustomerName.text.toString().trim()
        val customerContact = binding.editTextCustomerContact.text.toString().trim()
        val alternateContact = binding.editTextAlternateContact.text.toString().trim()
        val imei = binding.editTextImei.text.toString().trim()
        val totalCostText = binding.editTextTotalCost.text.toString().trim()
        val advanceTakenText = binding.editTextAdvanceTaken.text.toString().trim()

        // --- Validation ---
        if (customerName.isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show()
            return
        }
        if (customerContact.isEmpty()) {
            Toast.makeText(this, "Please enter customer contact", Toast.LENGTH_SHORT).show()
            return
        }
        if (totalCostText.isEmpty()) {
            Toast.makeText(this, "Please enter total cost", Toast.LENGTH_SHORT).show()
            return
        }

        val totalCost = totalCostText.toDoubleOrNull() ?: 0.0
        val advanceTaken = advanceTakenText.toDoubleOrNull() ?: 0.0

        // Create a Repair object with the collected data
        val newRepair = Repair(
            customerName = customerName,
            customerContact = customerContact,
            alternateContact = alternateContact.ifEmpty { null },
            imeiNumber = imei.ifEmpty { null },
            imagePath = latestImageSavedPath,
            totalCost = totalCost,
            advanceTaken = advanceTaken,
            status = getString(R.string.status_in), // Default status is "In"
            dateAdded = System.currentTimeMillis(), // Current time as a timestamp
            dateCompleted = null
        )

        // Use the ViewModel to insert the new repair into the database
        repairViewModel.insert(newRepair)

        Toast.makeText(this, "Repair saved successfully!", Toast.LENGTH_LONG).show()
        finish() // Close the activity and go back to the dashboard
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "Camera access is needed to add a photo.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    private fun getTmpFileUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val tmpFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            latestImageSavedPath = absolutePath
        }

        return FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            tmpFile
        )
    }
}
