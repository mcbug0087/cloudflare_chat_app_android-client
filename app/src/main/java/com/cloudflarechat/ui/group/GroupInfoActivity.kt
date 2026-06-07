package com.cloudflarechat.ui.group

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudflarechat.data.model.GroupDetail
import com.cloudflarechat.data.model.GroupMember
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.ActivityGroupInfoBinding
import com.cloudflarechat.util.PreferencesManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GroupInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupInfoBinding
    private lateinit var prefs: PreferencesManager
    private val repository = ChatRepository()
    private val membersAdapter = GroupMembersAdapter { member -> onMemberClick(member) }
    private var groupId: String = ""
    private var myRole: String = ""
    private var currentUserId: String = ""
    private var groupDetail: GroupDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        groupId = intent.getStringExtra("group_id") ?: ""

        supportActionBar?.apply {
            title = "群聊信息"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerViewMembers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMembers.adapter = membersAdapter

        binding.btnEditName.setOnClickListener { showEditNameDialog() }
        binding.btnDisband.setOnClickListener { showDisbandConfirm() }
        binding.btnLeave.setOnClickListener { leaveGroup() }
        binding.btnInvite.setOnClickListener { showInviteDialog() }

        lifecycleScope.launch {
            currentUserId = prefs.userId.first() ?: ""
            loadGroupDetail()
        }
    }

    private fun loadGroupDetail() {
        lifecycleScope.launch {
            val result = repository.getGroupDetail(groupId)
            result.fold(
                onSuccess = { detail ->
                    groupDetail = detail
                    myRole = detail.myRole
                    binding.tvGroupName.text = detail.group.name
                    binding.tvGroupCode.text = "群号: ${detail.group.groupCode ?: ""}"
                    binding.tvRole.text = "我的角色: ${roleText(detail.myRole)}"
                    membersAdapter.submitList(detail.members)
                    updateButtonVisibility()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "加载失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateButtonVisibility() {
        when (myRole) {
            "owner" -> {
                binding.btnEditName.visibility = android.view.View.VISIBLE
                binding.btnDisband.visibility = android.view.View.VISIBLE
                binding.btnLeave.visibility = android.view.View.GONE
                binding.btnInvite.visibility = android.view.View.VISIBLE
            }
            "admin" -> {
                binding.btnEditName.visibility = android.view.View.VISIBLE
                binding.btnDisband.visibility = android.view.View.GONE
                binding.btnLeave.visibility = android.view.View.VISIBLE
                binding.btnInvite.visibility = android.view.View.VISIBLE
            }
            else -> {
                binding.btnEditName.visibility = android.view.View.GONE
                binding.btnDisband.visibility = android.view.View.GONE
                binding.btnLeave.visibility = android.view.View.VISIBLE
                binding.btnInvite.visibility = android.view.View.GONE
            }
        }
    }

    private fun roleText(role: String): String = when (role) {
        "owner" -> "群主"
        "admin" -> "管理员"
        else -> "成员"
    }

    private fun showEditNameDialog() {
        val editText = EditText(this).apply {
            setText(groupDetail?.group?.name ?: "")
        }
        AlertDialog.Builder(this)
            .setTitle("修改群名称")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                updateGroupName(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun updateGroupName(name: String) {
        lifecycleScope.launch {
            val result = repository.updateGroupName(groupId, name)
            result.fold(
                onSuccess = {
                    binding.tvGroupName.text = name
                    Snackbar.make(binding.root, "群名称已更新", Snackbar.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "更新失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showDisbandConfirm() {
        AlertDialog.Builder(this)
            .setTitle("解散群聊")
            .setMessage("确定要解散此群聊吗？此操作不可撤销，所有聊天记录将被清除。")
            .setPositiveButton("解散") { _, _ -> disbandGroup() }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun disbandGroup() {
        lifecycleScope.launch {
            val result = repository.disbandGroup(groupId)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "群聊已解散", Snackbar.LENGTH_SHORT).show()
                    finish()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "解散失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun leaveGroup() {
        AlertDialog.Builder(this)
            .setTitle("退出群聊")
            .setMessage("确定要退出此群聊吗？")
            .setPositiveButton("退出") { _, _ ->
                lifecycleScope.launch {
                    val result = repository.leaveGroup(groupId)
                    result.fold(
                        onSuccess = {
                            Snackbar.make(binding.root, "已退出群聊", Snackbar.LENGTH_SHORT).show()
                            finish()
                        },
                        onFailure = { e ->
                            Snackbar.make(binding.root, e.message ?: "退出失败", Snackbar.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showInviteDialog() {
        val editText = EditText(this).apply {
            hint = "输入好友昵称搜索"
        }
        AlertDialog.Builder(this)
            .setTitle("邀请好友")
            .setView(editText)
            .setPositiveButton("搜索") { _, _ ->
                searchAndInvite(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun searchAndInvite(query: String) {
        lifecycleScope.launch {
            val result = repository.searchUsers(query)
            result.fold(
                onSuccess = { users ->
                    if (users.isEmpty()) {
                        Snackbar.make(binding.root, "未找到用户", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val names = users.map { it.nickname }.toTypedArray()
                        AlertDialog.Builder(this@GroupInfoActivity)
                            .setTitle("选择用户")
                            .setItems(names) { _, which ->
                                inviteUser(users[which].id, users[which].nickname)
                            }
                            .setNegativeButton("取消", null)
                            .show()
                    }
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "搜索失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun inviteUser(userId: String, nickname: String) {
        lifecycleScope.launch {
            val result = repository.inviteToGroup(groupId, userId)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "已邀请 $nickname", Snackbar.LENGTH_SHORT).show()
                    loadGroupDetail()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "邀请失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun onMemberClick(member: GroupMember) {
        if (member.id == currentUserId) return
        val items = mutableListOf<String>()
        if (myRole == "owner" || myRole == "admin") {
            items.add("踢出群聊")
            if (member.role != "admin") items.add("设为管理员")
            else items.add("取消管理员")
        }
        if (myRole == "owner") {
            items.add("转让群主")
        }

        if (items.isEmpty()) return

        AlertDialog.Builder(this)
            .setTitle(member.nickname)
            .setItems(items.toTypedArray()) { _, which ->
                when (items[which]) {
                    "踢出群聊" -> kickMember(member.id)
                    "设为管理员" -> toggleAdmin(member.id)
                    "取消管理员" -> toggleAdmin(member.id)
                    "转让群主" -> transferOwner(member.id, member.nickname)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun kickMember(userId: String) {
        lifecycleScope.launch {
            val result = repository.kickMember(groupId, userId)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "已踢出成员", Snackbar.LENGTH_SHORT).show()
                    loadGroupDetail()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "操作失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun toggleAdmin(userId: String) {
        lifecycleScope.launch {
            val result = repository.toggleAdmin(groupId, userId)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "操作成功", Snackbar.LENGTH_SHORT).show()
                    loadGroupDetail()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "操作失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun transferOwner(newOwnerId: String, nickname: String) {
        AlertDialog.Builder(this)
            .setTitle("转让群主")
            .setMessage("确定将群主转让给 $nickname 吗？")
            .setPositiveButton("确定") { _, _ ->
                lifecycleScope.launch {
                    val result = repository.transferOwner(groupId, newOwnerId)
                    result.fold(
                        onSuccess = {
                            Snackbar.make(binding.root, "群主已转让", Snackbar.LENGTH_SHORT).show()
                            loadGroupDetail()
                        },
                        onFailure = { e ->
                            Snackbar.make(binding.root, e.message ?: "转让失败", Snackbar.LENGTH_SHORT).show()
                        }
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