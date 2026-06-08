package com.cloudflarechat.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudflarechat.R
import com.cloudflarechat.data.model.ChatInfo
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.FragmentChatsBinding
import com.cloudflarechat.ui.chat.ChatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private val repository = ChatRepository()
    private val chatsAdapter = ChatsAdapter { chat -> onChatClick(chat) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = chatsAdapter
        loadChats()
    }

    private fun loadChats() {
        lifecycleScope.launch {
            try {
                // 加载好友列表，用于显示备注
                val friends = repository.getFriends().getOrDefault(emptyList())
                val friendNameMap = mutableMapOf<String, String>()
                for (f in friends) {
                    // 用好友昵称匹配 ChatInfo.name，映射到备注
                    friendNameMap[f.nickname] = f.remark?.takeIf { it.isNotBlank() } ?: f.nickname
                    // 同时用好友 ID 匹配，以防 chat.id 就是好友的用户 ID
                    friendNameMap[f.id] = f.remark?.takeIf { it.isNotBlank() } ?: f.nickname
                }
                chatsAdapter.friendNames = friendNameMap

                val privateChats = repository.getPrivateChats().getOrDefault(emptyList())
                val groups = repository.getGroups().getOrDefault(emptyList())
                val allChats = privateChats + groups.map { it.copy(chatType = "group") }
                chatsAdapter.submitList(allChats)
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message ?: "加载失败", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun onChatClick(chat: ChatInfo) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("chat_id", chat.id)
            putExtra("chat_type", chat.chatType ?: "private")
            putExtra("chat_name", chat.name ?: "")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}