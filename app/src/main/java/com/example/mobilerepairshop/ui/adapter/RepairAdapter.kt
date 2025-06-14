package com.example.mobilerepairshop.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilerepairshop.R
import com.example.mobilerepairshop.data.model.Repair
import com.example.mobilerepairshop.databinding.ItemRepairBinding

class RepairAdapter(private val onItemClicked: (Repair) -> Unit) :
    ListAdapter<Repair, RepairAdapter.RepairViewHolder>(RepairDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepairViewHolder {
        val binding = ItemRepairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepairViewHolder, position: Int) {
        val currentRepair = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentRepair)
        }
        holder.bind(currentRepair)
    }

    // This is just the ViewHolder class from inside your RepairAdapter.kt
// Replace the existing RepairViewHolder class with this one.
    class RepairViewHolder(private val binding: ItemRepairBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repair: Repair) {
            val context = binding.root.context
            binding.textViewOrderId.text = "ID: ${repair.id}"
            binding.textViewCustomerName.text = repair.customerName
            binding.chipStatus.text = repair.status

            // --- THIS IS THE NEW LOGIC TO CALCULATE AND DISPLAY DAYS ---
            if (repair.status == context.getString(R.string.status_out)) {
                binding.textViewDaysAgo.text = "Completed"
            } else {
                val currentTime = System.currentTimeMillis()
                val diff = currentTime - repair.dateAdded
                val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)

                binding.textViewDaysAgo.text = when (days) {
                    0L -> "Today"
                    1L -> "1 day ago"
                    else -> "$days days ago"
                }
            }
            // --- END OF NEW LOGIC ---

            when (repair.status) {
                context.getString(R.string.status_in) -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.status_in_background)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_in_text))
                }
                context.getString(R.string.status_pending) -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.status_pending_background)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text))
                }
                context.getString(R.string.status_out) -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.status_out_background)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.status_out_text))
                }
            }
        }
    }

    companion object {
        private val RepairDiffCallback = object : DiffUtil.ItemCallback<Repair>() {
            override fun areItemsTheSame(oldItem: Repair, newItem: Repair): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repair, newItem: Repair): Boolean {
                return oldItem == newItem
            }
        }
    }
}
