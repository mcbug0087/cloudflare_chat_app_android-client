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
import com.cloudflarechat.data.model.ChatInfo
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.FragmentGroupsBinding
import com.cloudflarechat.ui.chat.ChatActivity
import com.cloudflarechat.ui.group.GroupInfoActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private val repository = ChatRepository()
    private val groupsAdapter = GroupsAdapter(
        onClick = { group -> onGroupClick(group) },
        onLongClick = { group -> onGroupLongClick(group) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = groupsAdapter

        binding.fabCreateGroup.setOnClickListener { showCreateGroupDialog() }
        binding.fabJoinGroup.setOnClickListener { showJoinGroupDialog() }
        loadGroups()
    }

    private fun loadGroups() {
        lifecycleScope.launch {
            try {
                val groups = repository.getGroups().getOrDefault(emptyList())
                groupsAdapter.submitList(groups)
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message ?: "加载失败", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCreateGroupDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "群名称"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("创建群聊")
            .setView(editText)
            .setPositiveButton("创建") { _, _ ->
                createGroup(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun createGroup(name: String) {
        if (name.isEmpty()) {
            Snackbar.make(binding.root, "请输入群名称", Snackbar.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val result = repository.createGroup(name)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "群聊创建成功", Snackbar.LENGTH_SHORT).show()
                    loadGroups()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "创建失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showJoinGroupDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "输入群号（9位数字）"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("加入群聊")
            .setView(editText)
            .setPositiveButton("搜索") { _, _ ->
                searchGroup(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun searchGroup(code: String) {
        lifecycleScope.launch {
            val result = repository.searchGroups(code)
            result.fold(
                onSuccess = { group ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("加入群聊")
                        .setMessage("是否加入群聊: ${group.name}?")
                        .setPositiveButton("加入") { _, _ ->
                            joinGroup(group.id)
                        }
                        .setNegativeButton("取消", null)
                        .show()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, "未找到群聊", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun joinGroup(groupId: String) {
        lifecycleScope.launch {
            val result = repository.joinGroup(groupId)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "加入群聊成功", Snackbar.LENGTH_SHORT).show()
                    loadGroups()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "加入失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun onGroupClick(group: ChatInfo) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("chat_id", group.id)
            putExtra("chat_type", "group")
            putExtra("chat_name", group.name ?: "")
        }
        startActivity(intent)
    }

    private fun onGroupLongClick(group: ChatInfo) {
        val intent = Intent(requireContext(), GroupInfoActivity::class.java).apply {
            putExtra("group_id", group.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}