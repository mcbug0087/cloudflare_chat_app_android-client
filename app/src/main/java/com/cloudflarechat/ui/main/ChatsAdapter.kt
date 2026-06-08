package com.cloudflarechat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.ChatInfo
import com.cloudflarechat.databinding.ItemChatBinding
import com.cloudflarechat.util.TimeUtils

class ChatsAdapter(private val onClick: (ChatInfo) -> Unit) :
    ListAdapter<ChatInfo, ChatsAdapter.ViewHolder>(DiffCallback()) {

    /** 好友ID -> 备注/昵称映射，用于私聊时显示好友备注 */
    var friendNames: Map<String, String> = emptyMap()

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
            // 私聊时优先用好友备注，否则用 API 返回的 name
            val displayName = if (chat.chatType != "group") {
                friendNames[chat.id] ?: chat.name ?: "未知"
            } else {
                chat.name ?: "未知群聊"
            }
            binding.tvName.text = displayName
            binding.tvLastMessage.text = chat.lastMessage?.content ?: ""
            binding.tvTime.text = TimeUtils.formatTime(chat.lastMessage?.createdAt)
            binding.root.setOnClickListener { onClick(chat) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatInfo>() {
        override fun areItemsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatInfo, newItem: ChatInfo) = oldItem == newItem
    }
}