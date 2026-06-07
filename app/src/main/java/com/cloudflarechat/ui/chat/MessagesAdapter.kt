package com.cloudflarechat.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.Message
import com.cloudflarechat.databinding.ItemMessageBinding

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val messages = mutableListOf<Message>()
    var currentUserId: String = ""

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
            binding.tvSender.text = message.senderNickname ?: message.senderId
            binding.tvContent.text = message.content
            binding.tvTime.text = message.createdAt?.takeLast(8) ?: ""

            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            if (isSelf) {
                params.gravity = Gravity.END
                binding.tvSender.gravity = Gravity.END
                binding.root.setBackgroundColor(0xFFE3F2FD.toInt())
            } else {
                params.gravity = Gravity.START
                binding.tvSender.gravity = Gravity.START
                binding.root.setBackgroundColor(0xFFF5F5F5.toInt())
            }
        }
    }
}