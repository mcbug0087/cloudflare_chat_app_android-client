package com.cloudflarechat.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cloudflarechat.data.api.ApiClient
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.ActivitySettingsBinding
import com.cloudflarechat.ui.login.LoginActivity
import com.cloudflarechat.ui.main.MainActivity
import com.cloudflarechat.util.PreferencesManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: PreferencesManager
    private val repository = ChatRepository()
    private var currentNickname = ""
    private var isSuperAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "设置"
        }

        loadUserInfo()

        binding.btnChangeNickname.setOnClickListener { showChangeNicknameDialog() }
        binding.btnChangePassword.setOnClickListener { showChangePasswordDialog() }
        binding.btnDeleteAccount.setOnClickListener { showDeleteAccountDialog() }
        binding.btnAdminPanel.setOnClickListener {
            startActivity(Intent(this, AdminPanelActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch { prefs.clearAll() }
            ApiClient.token = null
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun loadUserInfo() {
        lifecycleScope.launch {
            val result = repository.getCurrentUser()
            result.fold(
                onSuccess = { user ->
                    currentNickname = user.nickname
                    isSuperAdmin = user.role == "super_admin"
                    binding.tvNickname.text = "当前昵称: ${currentNickname}"
                    binding.btnAdminPanel.visibility =
                        if (isSuperAdmin) android.view.View.VISIBLE else android.view.View.GONE
                },
                onFailure = {}
            )
        }
    }

    private fun showChangeNicknameDialog() {
        val editText = EditText(this).apply {
            hint = "新昵称"
            setText(currentNickname)
        }
        AlertDialog.Builder(this)
            .setTitle("修改昵称")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                val nickname = editText.text.toString().trim()
                if (nickname.isNotEmpty()) changeNickname(nickname)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun changeNickname(nickname: String) {
        lifecycleScope.launch {
            val result = repository.changeNickname(nickname)
            result.fold(
                onSuccess = { user ->
                    currentNickname = user.nickname
                    binding.tvNickname.text = "当前昵称: ${currentNickname}"
                    prefs.saveNickname(currentNickname)
                    Snackbar.make(binding.root, "昵称已更新", Snackbar.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "修改失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showChangePasswordDialog() {
        val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, null) // placeholder
        val oldPwdEdit = EditText(this).apply { hint = "旧密码" }
        val newPwdEdit = EditText(this).apply { hint = "新密码（至少6位）" }
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(oldPwdEdit)
            addView(newPwdEdit).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (16 * resources.displayMetrics.density).toInt() }
            }
        }
        AlertDialog.Builder(this)
            .setTitle("修改密码")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                val oldPwd = oldPwdEdit.text.toString().trim()
                val newPwd = newPwdEdit.text.toString().trim()
                if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                    Snackbar.make(binding.root, "请输入新旧密码", Snackbar.LENGTH_SHORT).show()
                } else if (newPwd.length < 6) {
                    Snackbar.make(binding.root, "新密码至少6位", Snackbar.LENGTH_SHORT).show()
                } else {
                    changePwd(oldPwd, newPwd)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun changePwd(oldPwd: String, newPwd: String) {
        lifecycleScope.launch {
            val result = repository.changePassword(oldPwd, newPwd)
            result.fold(
                onSuccess = {
                    Snackbar.make(binding.root, "密码已更新", Snackbar.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "修改失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showDeleteAccountDialog() {
        val editText = EditText(this).apply { hint = "请输入密码确认" }
        AlertDialog.Builder(this)
            .setTitle("注销账号")
            .setMessage("此操作不可撤销！确认后账号将被永久删除。")
            .setView(editText)
            .setPositiveButton("确认注销") { _, _ ->
                deleteAccount(editText.text.toString().trim())
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteAccount(password: String) {
        lifecycleScope.launch {
            val result = repository.deleteAccount(password)
            result.fold(
                onSuccess = {
                    prefs.clearAll()
                    ApiClient.token = null
                    startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                    finishAffinity()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "注销失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}