package com.cloudflarechat.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val data: T?,
    val error: ApiError?
)

data class ApiError(
    val code: String,
    val message: String
)

data class AuthRequest(
    val nickname: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val nickname: String,
    val role: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class Friend(
    val id: String,
    val nickname: String,
    val remark: String?,
    @SerializedName("created_at") val createdAt: String? = null
)

data class AddFriendRequest(
    @SerializedName("user_id") val userId: String,
    val remark: String? = null
)

data class RemarkRequest(
    val remark: String
)

data class ChatInfo(
    val id: String,
    val name: String? = null,
    @SerializedName("last_message") val lastMessage: Message? = null,
    @SerializedName("created_at") val createdAt: Any? = null,
    @SerializedName("chat_type") val chatType: String? = null
)

data class PrivateChatRequest(
    @SerializedName("target_user_id") val targetUserId: String
)

data class Message(
    val id: String,
    val content: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_nickname") val senderNickname: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("chat_id") val chatId: String? = null,
    @SerializedName("chat_type") val chatType: String? = null,
    @SerializedName("created_at") val createdAt: Any? = null
)

data class SendMessageRequest(
    val content: String
)

data class MessagesResponse(
    val messages: List<Message>
)

data class Group(
    val id: String,
    val name: String,
    @SerializedName("group_code") val groupCode: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class CreateGroupRequest(
    val name: String
)

data class GroupDetail(
    val group: Group,
    val members: List<GroupMember>,
    @SerializedName("my_role") val myRole: String
)

data class GroupMember(
    val id: String,
    val nickname: String,
    val role: String,
    @SerializedName("group_nickname") val groupNickname: String? = null
)

data class TransferOwnerRequest(
    @SerializedName("new_owner_id") val newOwnerId: String
)

data class InviteRequest(
    @SerializedName("target_user_id") val targetUserId: String
)

data class GroupNicknameRequest(
    @SerializedName("group_nickname") val groupNickname: String
)

data class UpdateGroupNameRequest(
    val name: String
)

// New models for user profile management
data class ChangePasswordRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class DeleteAccountRequest(
    val password: String
)

data class ChangeNicknameRequest(
    val nickname: String
)

// Admin models
data class AdminUser(
    val id: String,
    val nickname: String,
    val role: String? = null,
    @SerializedName("is_banned") val isBanned: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class AdminGroup(
    val id: String,
    val name: String,
    @SerializedName("group_code") val groupCode: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class AdminChangePasswordRequest(
    @SerializedName("new_password") val newPassword: String
)

data class AdminUpdateSettingsRequest(
    val nickname: String? = null,
    @SerializedName("new_password") val newPassword: String? = null
)

// WebSocket messages
data class WsMessage(
    val type: String,
    @SerializedName("chat_id") val chatId: String? = null,
    @SerializedName("chat_type") val chatType: String? = null,
    @SerializedName("is_typing") val isTyping: Boolean? = null,
    val message: Message? = null,
    @SerializedName("user_id") val userId: String? = null,
    val nickname: String? = null,
    val action: String? = null,
    val data: Any? = null
)