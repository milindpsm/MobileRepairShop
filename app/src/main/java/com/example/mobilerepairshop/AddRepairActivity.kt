package com.example.mobilerepairshop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilerepairshop.databinding.ActivityAddRepairBinding

class AddRepairActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRepairBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listeners
        binding.buttonCapturePhoto.setOnClickListener {
            // TODO: Implement camera logic in a later task
            Toast.makeText(this, "Camera will be implemented later", Toast.LENGTH_SHORT).show()
        }

        binding.buttonSaveRepair.setOnClickListener {
            // TODO: Implement save logic in a later task
            Toast.makeText(this, "Save functionality will be implemented later", Toast.LENGTH_SHORT).show()
        }
    }
}