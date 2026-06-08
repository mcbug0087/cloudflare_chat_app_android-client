package com.cloudflarechat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.ChatInfo
import com.cloudflarechat.databinding.ItemChatBinding
import com.cloudflarechat.util.TimeUtils

class GroupsAdapter(
    private val onClick: (ChatInfo) -> Unit,
    private val onLongClick: (ChatInfo) -> Unit
) : ListAdapter<ChatInfo, GroupsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: ChatInfo) {
            binding.tvName.text = group.name ?: "未知群聊"
            binding.tvLastMessage.text = group.lastMessage?.content ?: ""
            binding.tvTime.text = TimeUtils.formatTime(group.lastMessage?.createdAt)
            binding.root.setOnClickListener { onClick(group) }
            binding.root.setOnLongClickListener {
                onLongClick(group)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatInfo>() {
        override fun areItemsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem == newItem
    }
}