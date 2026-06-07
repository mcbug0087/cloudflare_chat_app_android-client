package com.cloudflarechat.data.repository

import com.cloudflarechat.data.api.ApiClient
import com.cloudflarechat.data.model.*

class ChatRepository {

    private val api = ApiClient.apiService

    // Auth
    suspend fun register(nickname: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(AuthRequest(nickname, password))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val error = response.body()?.error?.message ?: "注册失败"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(nickname: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(AuthRequest(nickname, password))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val error = response.body()?.error?.message ?: "登录失败"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Users
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            val response = api.searchUsers(query)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "搜索失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Friends
    suspend fun getFriends(): Result<List<Friend>> {
        return try {
            val response = api.getFriends()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取好友列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFriend(userId: String, remark: String?): Result<Friend> {
        return try {
            val response = api.addFriend(AddFriendRequest(userId, remark))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "添加好友失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFriend(friendId: String): Result<Unit> {
        return try {
            val response = api.deleteFriend(friendId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "删除好友失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRemark(friendId: String, remark: String): Result<Friend> {
        return try {
            val response = api.updateRemark(friendId, RemarkRequest(remark))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "修改备注失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Private Chats
    suspend fun getPrivateChats(): Result<List<ChatInfo>> {
        return try {
            val response = api.getPrivateChats()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取私聊列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPrivateChat(targetUserId: String): Result<ChatInfo> {
        return try {
            val response = api.createPrivateChat(PrivateChatRequest(targetUserId))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "创建私聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPrivateMessages(chatId: String, before: String? = null, limit: Int = 50): Result<List<Message>> {
        return try {
            val response = api.getPrivateMessages(chatId, before, limit)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取消息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPrivateMessage(chatId: String, content: String): Result<Message> {
        return try {
            val response = api.sendPrivateMessage(chatId, SendMessageRequest(content))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "发送消息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Groups
    suspend fun createGroup(name: String): Result<Group> {
        return try {
            val response = api.createGroup(CreateGroupRequest(name))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "创建群聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<ChatInfo>> {
        return try {
            val response = api.getGroups()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取群聊列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchGroups(code: String): Result<Group> {
        return try {
            val response = api.searchGroups(code)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "搜索群聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupDetail(groupId: String): Result<GroupDetail> {
        return try {
            val response = api.getGroupDetail(groupId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取群聊信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGroupName(groupId: String, name: String): Result<Group> {
        return try {
            val response = api.updateGroupName(groupId, UpdateGroupNameRequest(name))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "修改群名称失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disbandGroup(groupId: String): Result<Unit> {
        return try {
            val response = api.disbandGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "解散群聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinGroup(groupId: String): Result<Unit> {
        return try {
            val response = api.joinGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "加入群聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveGroup(groupId: String): Result<Unit> {
        return try {
            val response = api.leaveGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "退出群聊失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun kickMember(groupId: String, userId: String): Result<Unit> {
        return try {
            val response = api.kickMember(groupId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "踢人失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun transferOwner(groupId: String, newOwnerId: String): Result<Unit> {
        return try {
            val response = api.transferOwner(groupId, TransferOwnerRequest(newOwnerId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "转让群主失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleAdmin(groupId: String, userId: String): Result<Unit> {
        return try {
            val response = api.toggleAdmin(groupId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "操作失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun inviteToGroup(groupId: String, targetUserId: String): Result<Unit> {
        return try {
            val response = api.inviteToGroup(groupId, InviteRequest(targetUserId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "邀请失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setGroupNickname(groupId: String, userId: String, nickname: String): Result<Unit> {
        return try {
            val response = api.setGroupNickname(groupId, userId, GroupNicknameRequest(nickname))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "设置群昵称失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupMessages(groupId: String, before: String? = null, limit: Int = 50): Result<List<Message>> {
        return try {
            val response = api.getGroupMessages(groupId, before, limit)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "获取群消息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendGroupMessage(groupId: String, content: String): Result<Message> {
        return try {
            val response = api.sendGroupMessage(groupId, SendMessageRequest(content))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "发送群消息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}