package com.example.mobilerepairshop.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

    class RepairViewHolder(private val binding: ItemRepairBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repair: Repair) {
            binding.textViewOrderId.text = "ID: ${repair.id}"
            binding.textViewCustomerName.text = repair.customerName
            binding.chipStatus.text = repair.status
            // TODO: We will add logic here later to change the chip color based on status
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
