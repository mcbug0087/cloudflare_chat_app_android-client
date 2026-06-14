package com.cloudflarechat.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudflarechat.data.model.Friend
import com.cloudflarechat.data.model.User
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.FragmentFriendsBinding
import com.cloudflarechat.ui.chat.ChatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val repository = ChatRepository()
    private val friendsAdapter = FriendsAdapter(
        onClick = { friend -> onFriendClick(friend) },
        onLongClick = { friend -> onFriendLongClick(friend) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = friendsAdapter

        binding.fabAddFriend.setOnClickListener { showAddFriendDialog() }
        loadFriends()
    }

    private fun loadFriends() {
        lifecycleScope.launch {
            try {
                val friends = repository.getFriends().getOrDefault(emptyList())
                friendsAdapter.submitList(friends)
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message ?: "加载失败", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddFriendDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "输入用户昵称搜索"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("搜索用户")
            .setView(editText)
            .setPositiveButton("搜索") { _, _ ->
                searchUser(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun searchUser(query: String) {
        lifecycleScope.launch {
            val result = repository.searchUsers(query)
            result.fold(
                onSuccess = { users ->
                    if (users.isEmpty()) {
                        Snackbar.make(binding.root, "未找到用户", Snackbar.LENGTH_SHORT).show()
                    } else {
                        showUserResultDialog(users)
                    }
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "搜索失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showUserResultDialog(users: List<User>) {
        val names = users.map { it.nickname }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("搜索结果")
            .setItems(names) { _, which ->
                addFriend(users[which])
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun addFriend(user: User) {
        lifecycleScope.launch {
            val result = repository.addFriend(user.id, null)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "已添加好友: ${user.nickname}", Snackbar.LENGTH_SHORT).show()
                    loadFriends()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "添加失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun onFriendClick(friend: Friend) {
        lifecycleScope.launch {
            val result = repository.createPrivateChat(friend.id)
            result.fold(
                onSuccess = { chat ->
                    startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
                        putExtra("chat_id", chat.id)
                        putExtra("chat_name", friend.remark ?: friend.nickname)
                        putExtra("chat_type", "private")
                    })
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "创建私聊失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun onFriendLongClick(friend: Friend) {
        AlertDialog.Builder(requireContext())
            .setTitle(friend.nickname)
            .setItems(arrayOf("删除好友", "修改备注")) { _, which ->
                when (which) {
                    0 -> deleteFriend(friend)
                    1 -> showEditRemarkDialog(friend)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteFriend(friend: Friend) {
        lifecycleScope.launch {
            val result = repository.deleteFriend(friend.id)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "已删除好友", Snackbar.LENGTH_SHORT).show()
                    loadFriends()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "删除失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showEditRemarkDialog(friend: Friend) {
        val editText = EditText(requireContext()).apply {
            hint = "输入备注"
            setText(friend.remark ?: "")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("修改备注")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                updateRemark(friend, editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun updateRemark(friend: Friend, remark: String) {
        lifecycleScope.launch {
            val result = repository.updateRemark(friend.id, remark)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "备注已更新", Snackbar.LENGTH_SHORT).show()
                    loadFriends()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "更新失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}