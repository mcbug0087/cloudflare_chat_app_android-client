package com.cloudflarechat.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.AdminGroup
import com.cloudflarechat.databinding.ItemAdminGroupBinding

class AdminGroupAdapter(
    private val onDisband: (AdminGroup) -> Unit
) : RecyclerView.Adapter<AdminGroupAdapter.ViewHolder>() {

    private var groups = listOf<AdminGroup>()

    fun submitList(list: List<AdminGroup>) {
        groups = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount() = groups.size

    inner class ViewHolder(private val binding: ItemAdminGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: AdminGroup) {
            binding.tvName.text = group.name
            binding.tvCode.text = "群号: ${group.groupCode ?: "--"}"
            binding.btnDisband.setOnClickListener { onDisband(group) }
        }
    }
}