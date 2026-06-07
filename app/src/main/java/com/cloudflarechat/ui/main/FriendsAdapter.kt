package com.cloudflarechat.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudflarechat.data.model.Friend
import com.cloudflarechat.databinding.ItemUserBinding

class FriendsAdapter(private val onLongClick: (Friend) -> Unit) :
    ListAdapter<Friend, FriendsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.tvName.text = friend.remark ?: friend.nickname
            if (friend.remark != null) {
                binding.tvName.text = "${friend.remark} (${friend.nickname})"
            }
            binding.btnAction.visibility = android.view.View.GONE
            binding.root.setOnLongClickListener {
                onLongClick(friend)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Friend, newItem: Friend) = oldItem == newItem
    }
}