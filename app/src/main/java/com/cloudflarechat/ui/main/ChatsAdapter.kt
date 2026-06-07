package com.cloudflarechat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.ChatInfo
import com.cloudflarechat.databinding.ItemChatBinding

class ChatsAdapter(private val onClick: (ChatInfo) -> Unit) :
    ListAdapter<ChatInfo, ChatsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatInfo) {
            binding.tvName.text = chat.name ?: "未知"
            binding.tvLastMessage.text = chat.lastMessage?.content ?: ""
            binding.tvTime.text = chat.lastMessage?.createdAt?.takeLast(8) ?: ""
            binding.root.setOnClickListener { onClick(chat) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatInfo>() {
        override fun areItemsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem == newItem
    }
}