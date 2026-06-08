package com.cloudflarechat.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.Message
import com.cloudflarechat.databinding.ItemMessageBinding
import com.cloudflarechat.util.TimeUtils

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    var currentUserId: String = ""
    /** 发送者ID -> 显示名称映射（包含好友备注和群成员昵称） */
    var senderNames: Map<String, String> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    fun submitList(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            val isSelf = message.senderId == currentUserId
            // 优先从名称映射中查找，其次用 API 返回的昵称，最后用 ID
            val displayName = if (isSelf) {
                message.senderNickname ?: message.senderId
            } else {
                senderNames[message.senderId]
                    ?: message.senderNickname
                    ?: message.senderId
            }
            binding.tvSender.text = displayName
            binding.tvContent.text = message.content
            binding.tvTime.text = TimeUtils.formatTime(message.createdAt)

            if (isSelf) {
                binding.tvSender.gravity = Gravity.END
                binding.tvContent.gravity = Gravity.END
                binding.tvTime.gravity = Gravity.END
                binding.root.setBackgroundColor(0xFFE3F2FD.toInt())
            } else {
                binding.tvSender.gravity = Gravity.START
                binding.tvContent.gravity = Gravity.START
                binding.tvTime.gravity = Gravity.START
                binding.root.setBackgroundColor(0xFFF5F5F5.toInt())
            }
        }
    }
}