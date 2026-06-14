package com.cloudflarechat.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.GroupMember
import com.cloudflarechat.databinding.ItemUserBinding

class GroupMembersAdapter(private val onClick: (GroupMember) -> Unit) :
    ListAdapter<GroupMember, GroupMembersAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: GroupMember) {
            val displayName = member.groupNickname?.takeIf { it.isNotBlank() }
                ?: member.nickname.takeIf { it.isNotBlank() }
                ?: member.id
            binding.tvName.text = when (member.role) {
                "owner" -> "👑 $displayName"
                "admin" -> "⭐ $displayName"
                else -> displayName
            }
            binding.btnAction.visibility = android.view.View.GONE
            binding.root.setOnClickListener { onClick(member) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GroupMember>() {
        override fun areItemsTheSame(oldItem: GroupMember, newItem: GroupMember) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GroupMember, newItem: GroupMember) = oldItem == newItem
    }
}