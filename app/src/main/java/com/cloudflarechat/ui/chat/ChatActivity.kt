package com.cloudflarechat.ui.chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudflarechat.data.api.ApiClient
import com.cloudflarechat.data.model.Message
import com.cloudflarechat.data.model.WsMessage
import com.cloudflarechat.data.repository.ChatRepository
import com.cloudflarechat.databinding.ActivityChatBinding
import com.cloudflarechat.util.PreferencesManager
import com.cloudflarechat.util.WsMessageParser
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var prefs: PreferencesManager
    private val repository = ChatRepository()
    private val messagesAdapter = MessagesAdapter()
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    private var chatId: String = ""
    private var chatType: String = "private"
    private var chatName: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)

        chatId = intent.getStringExtra("chat_id") ?: ""
        chatType = intent.getStringExtra("chat_type") ?: "private"
        chatName = intent.getStringExtra("chat_name") ?: ""

        supportActionBar?.apply {
            title = chatName
            setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = messagesAdapter

        binding.btnSend.setOnClickListener { sendMessage() }

        lifecycleScope.launch {
            currentUserId = prefs.userId.first() ?: ""
            messagesAdapter.currentUserId = currentUserId

            // 构建发送者名称映射
            val nameMap = mutableMapOf<String, String>()

            if (chatType == "group") {
                // 群聊：加载群成员信息
                val groupDetail = repository.getGroupDetail(chatId).getOrNull()
                groupDetail?.members?.forEach { member ->
                    // 优先使用群昵称
                    nameMap[member.id] = member.groupNickname?.takeIf { it.isNotBlank() }
                        ?: member.nickname
                }
            }

            // 加载好友备注（私聊和群聊都适用）
            val friends = repository.getFriends().getOrDefault(emptyList())
            for (f in friends) {
                val display = f.remark?.takeIf { it.isNotBlank() } ?: f.nickname
                // 只在还没有群成员昵称时覆盖（好友备注可能更个性化）
                if (!nameMap.containsKey(f.id)) {
                    nameMap[f.id] = display
                }
            }

            messagesAdapter.senderNames = nameMap
            loadMessages()
            connectWebSocket()
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            val result = if (chatType == "private") {
                repository.getPrivateMessages(chatId)
            } else {
                repository.getGroupMessages(chatId)
            }
            result.fold(
                onSuccess = { messages ->
                    messagesAdapter.submitList(messages.reversed())
                    binding.recyclerView.scrollToPosition(messages.size - 1)
                },
                onFailure = {
                    Snackbar.make(binding.root, "加载消息失败", Snackbar.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun connectWebSocket() {
        lifecycleScope.launch {
            val token = prefs.token.first() ?: return@launch
            val wsUrl = "${ApiClient.WS_URL}?token=$token&chat_id=$chatId&chat_type=$chatType"

            val client = OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build()

            val request = Request.Builder()
                .url(wsUrl)
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) {
                    runOnUiThread {
                        handleWsMessage(text)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    runOnUiThread {
                        Snackbar.make(binding.root, "连接断开", Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun handleWsMessage(text: String) {
        val wsMessage = WsMessageParser.parse(text) ?: return
        when (wsMessage.type) {
            "new_message" -> {
                wsMessage.message?.let { msg ->
                    messagesAdapter.addMessage(msg)
                    binding.recyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
                }
            }
            "typing" -> {
                if (wsMessage.isTyping == true) {
                    binding.tvTyping.visibility = View.VISIBLE
                } else {
                    binding.tvTyping.visibility = View.GONE
                }
            }
        }
    }

    private fun sendMessage() {
        val content = binding.etMessage.text.toString().trim()
        if (content.isEmpty()) return

        binding.etMessage.text?.clear()
        binding.btnSend.isEnabled = false

        lifecycleScope.launch {
            val result = if (chatType == "private") {
                repository.sendPrivateMessage(chatId, content)
            } else {
                repository.sendGroupMessage(chatId, content)
            }
            result.fold(
                onSuccess = { msg ->
                    messagesAdapter.addMessage(msg)
                    binding.recyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
                },
                onFailure = { e ->
                    Snackbar.make(binding.root, e.message ?: "发送失败", Snackbar.LENGTH_SHORT).show()
                }
            )
            binding.btnSend.isEnabled = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Activity destroyed")
    }
}