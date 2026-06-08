package com.cloudflarechat.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.AdminUser
import com.cloudflarechat.databinding.ItemAdminUserBinding

class AdminUserAdapter(
    private val onBan: (AdminUser) -> Unit,
    private val onDelete: (AdminUser) -> Unit,
    private val onChangePwd: (AdminUser) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    private var users = listOf<AdminUser>()

    fun submitList(list: List<AdminUser>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(private val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: AdminUser) {
            binding.tvName.text = user.nickname
            binding.tvRole.text = if (user.role == "super_admin") "超级管理员" else user.role ?: "普通用户"
            binding.tvBanned.text = if (user.isBanned == true) "已封禁" else ""
            binding.tvBanned.visibility =
                if (user.isBanned == true) android.view.View.VISIBLE else android.view.View.GONE

            binding.btnBan.text = if (user.isBanned == true) "解封" else "封禁"
            if (user.role == "super_admin") {
                binding.btnBan.isEnabled = false
                binding.btnDelete.isEnabled = false
            } else {
                binding.btnBan.isEnabled = true
                binding.btnDelete.isEnabled = true
            }

            binding.btnBan.setOnClickListener { onBan(user) }
            binding.btnDelete.setOnClickListener { onDelete(user) }
            binding.btnChangePwd.setOnClickListener { onChangePwd(user) }
        }
    }
}