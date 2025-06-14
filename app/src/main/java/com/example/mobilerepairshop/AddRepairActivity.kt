package com.example.mobilerepairshop

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.mobilerepairshop.databinding.ActivityAddRepairBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRepairActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRepairBinding
    private var latestTmpUri: Uri? = null
    private var latestImageSavedPath: String? = null

    // Launcher for taking a picture
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                // Photo was saved successfully to the temp URI, now display it
                Glide.with(this)
                    .load(uri)
                    .into(binding.imageViewPhone)
            }
        }
    }

    // Launcher for requesting camera permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted. Continue the action
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
            // TODO: Implement save logic in a later task
            Toast.makeText(this, "Save functionality will be implemented later", Toast.LENGTH_SHORT).show()
        }
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
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            latestImageSavedPath = absolutePath
        }

        // Using packageName directly is a more robust way to get the application ID
        // and avoids issues with the generated BuildConfig file.
        return FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            tmpFile
        )
    }
}
