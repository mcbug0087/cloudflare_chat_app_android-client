package com.cloudflarechat.ui.settings

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudflarechat.data.model.AdminUser
import com.cloudflarechat.data.model.AdminGroup
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.ActivityAdminPanelBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AdminPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminPanelBinding
    private val repository = ChatRepository()
    private val userAdapter = AdminUserAdapter(
        onBan = { user -> toggleBan(user) },
        onDelete = { user -> confirmDeleteUser(user) },
        onChangePwd = { user -> showChangePasswordDialog(user) }
    )
    private val groupAdapter = AdminGroupAdapter(
        onDisband = { group -> confirmDisbandGroup(group) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "超级管理员面板"
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = userAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> loadUsers()
                    1 -> loadGroups()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        loadUsers()
    }

    private fun loadUsers() {
        binding.recyclerView.adapter = userAdapter
        lifecycleScope.launch {
            val result = repository.adminGetUsers()
            result.fold(
                onSuccess = { userAdapter.submitList(it) },
                onFailure = { Snackbar.make(binding.root, it.message ?: "加载失败", Snackbar.LENGTH_SHORT).show() }
            )
        }
    }

    private fun loadGroups() {
        binding.recyclerView.adapter = groupAdapter
        lifecycleScope.launch {
            val result = repository.adminGetGroups()
            result.fold(
                onSuccess = { groupAdapter.submitList(it) },
                onFailure = { Snackbar.make(binding.root, it.message ?: "加载失败", Snackbar.LENGTH_SHORT).show() }
            )
        }
    }

    private fun toggleBan(user: AdminUser) {
        lifecycleScope.launch {
            val result = if (user.isBanned == true) {
                repository.adminUnbanUser(user.id)
            } else {
                repository.adminBanUser(user.id)
            }
            result.fold(
                onSuccess = { loadUsers() },
                onFailure = { Snackbar.make(binding.root, it.message ?: "操作失败", Snackbar.LENGTH_SHORT).show() }
            )
        }
    }

    private fun confirmDeleteUser(user: AdminUser) {
        AlertDialog.Builder(this)
            .setTitle("删除用户")
            .setMessage("确认删除用户 ${user.nickname}？此操作不可撤销。")
            .setPositiveButton("删除") { _, _ ->
                lifecycleScope.launch {
                    val result = repository.adminDeleteUser(user.id)
                    result.fold(
                        onSuccess = { loadUsers() },
                        onFailure = { Snackbar.make(binding.root, it.message ?: "删除失败", Snackbar.LENGTH_SHORT).show() }
                    )
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showChangePasswordDialog(user: AdminUser) {
        val editText = EditText(this).apply { hint = "新密码（至少6位）" }
        AlertDialog.Builder(this)
            .setTitle("修改 ${user.nickname} 的密码")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                val pwd = editText.text.toString().trim()
                if (pwd.length >= 6) {
                    lifecycleScope.launch {
                        val result = repository.adminChangeUserPassword(user.id, pwd)
                        result.fold(
                            onSuccess = { Snackbar.make(binding.root, "密码已更新", Snackbar.LENGTH_SHORT).show() },
                            onFailure = { Snackbar.make(binding.root, it.message ?: "操作失败", Snackbar.LENGTH_SHORT).show() }
                        )
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun confirmDisbandGroup(group: AdminGroup) {
        AlertDialog.Builder(this)
            .setTitle("解散群聊")
            .setMessage("确认解散群聊 ${group.name}？所有消息将被删除。")
            .setPositiveButton("解散") { _, _ ->
                lifecycleScope.launch {
                    val result = repository.adminDisbandGroup(group.id)
                    result.fold(
                        onSuccess = { loadGroups() },
                        onFailure = { Snackbar.make(binding.root, it.message ?: "解散失败", Snackbar.LENGTH_SHORT).show() }
                    )
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}