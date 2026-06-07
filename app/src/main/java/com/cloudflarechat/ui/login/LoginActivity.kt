package com.cloudflarechat.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cloudflarechat.databinding.ActivityLoginBinding
import com.cloudflarechat.data.api.ApiClient
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.ui.main.MainActivity
import com.cloudflarechat.util.PreferencesManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: PreferencesManager
    private val repository = ChatRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        ApiClient.token = null

        // 检查是否已有登录信息
        lifecycleScope.launch {
            val token = prefs.token.first()
            if (!token.isNullOrEmpty()) {
                ApiClient.token = token
                startMainActivity()
            }
        }

        binding.btnLogin.setOnClickListener {
            val nickname = binding.etNickname.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (nickname.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "请填写昵称和密码", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Snackbar.make(binding.root, "密码至少6位", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(nickname, password)
        }

        binding.btnRegister.setOnClickListener {
            val nickname = binding.etNickname.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (nickname.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "请填写昵称和密码", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Snackbar.make(binding.root, "密码至少6位", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(nickname, password)
        }
    }

    private fun login(nickname: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            val result = repository.login(nickname, password)
            result.fold(
                onSuccess = { auth ->
                    prefs.saveAuth(auth.token, auth.user.id, auth.user.nickname)
                    ApiClient.token = auth.token
                    startMainActivity()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "登录失败", Snackbar.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                    binding.btnRegister.isEnabled = true
                }
            )
        }
    }

    private fun register(nickname: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            val result = repository.register(nickname, password)
            result.fold(
                onSuccess = { auth ->
                    prefs.saveAuth(auth.token, auth.user.id, auth.user.nickname)
                    ApiClient.token = auth.token
                    startMainActivity()
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "注册失败", Snackbar.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                    binding.btnRegister.isEnabled = true
                }
            )
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}