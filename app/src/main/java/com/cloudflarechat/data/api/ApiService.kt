package com.cloudflarechat.data.api

import com.cloudflarechat.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse<AuthResponse>>

    // Users
    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<ApiResponse<User>>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<ApiResponse<List<User>>>

    // Friends
    @GET("api/friends")
    suspend fun getFriends(): Response<ApiResponse<List<Friend>>>

    @POST("api/friends")
    suspend fun addFriend(@Body request: AddFriendRequest): Response<ApiResponse<Friend>>

    @DELETE("api/friends/{friendId}")
    suspend fun deleteFriend(@Path("friendId") friendId: String): Response<ApiResponse<Any>>

    @PUT("api/friends/{friendId}/remark")
    suspend fun updateRemark(
        @Path("friendId") friendId: String,
        @Body request: RemarkRequest
    ): Response<ApiResponse<Friend>>

    // Private Chats
    @GET("api/chats/private")
    suspend fun getPrivateChats(): Response<ApiResponse<List<ChatInfo>>>

    @POST("api/chats/private")
    suspend fun createPrivateChat(@Body request: PrivateChatRequest): Response<ApiResponse<ChatInfo>>

    @GET("api/chats/private/{chatId}/messages")
    suspend fun getPrivateMessages(
        @Path("chatId") chatId: String,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Message>>>

    @POST("api/chats/private/{chatId}/messages")
    suspend fun sendPrivateMessage(
        @Path("chatId") chatId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<Message>>

    // Groups
    @POST("api/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<ApiResponse<Group>>

    @GET("api/groups")
    suspend fun getGroups(): Response<ApiResponse<List<ChatInfo>>>

    @GET("api/groups/search")
    suspend fun searchGroups(@Query("code") code: String): Response<ApiResponse<Group>>

    @GET("api/groups/{groupId}")
    suspend fun getGroupDetail(@Path("groupId") groupId: String): Response<ApiResponse<GroupDetail>>

    @PUT("api/groups/{groupId}")
    suspend fun updateGroupName(
        @Path("groupId") groupId: String,
        @Body request: UpdateGroupNameRequest
    ): Response<ApiResponse<Group>>

    @DELETE("api/groups/{groupId}")
    suspend fun disbandGroup(@Path("groupId") groupId: String): Response<ApiResponse<Any>>

    @POST("api/groups/{groupId}/join")
    suspend fun joinGroup(@Path("groupId") groupId: String): Response<ApiResponse<Any>>

    @POST("api/groups/{groupId}/leave")
    suspend fun leaveGroup(@Path("groupId") groupId: String): Response<ApiResponse<Any>>

    @DELETE("api/groups/{groupId}/members/{userId}")
    suspend fun kickMember(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<Any>>

    @PUT("api/groups/{groupId}/owner")
    suspend fun transferOwner(
        @Path("groupId") groupId: String,
        @Body request: TransferOwnerRequest
    ): Response<ApiResponse<Any>>

    @PUT("api/groups/{groupId}/admins/{userId}")
    suspend fun toggleAdmin(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<Any>>

    @POST("api/groups/{groupId}/invite")
    suspend fun inviteToGroup(
        @Path("groupId") groupId: String,
        @Body request: InviteRequest
    ): Response<ApiResponse<Any>>

    @PUT("api/groups/{groupId}/nickname/{userId}")
    suspend fun setGroupNickname(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String,
        @Body request: GroupNicknameRequest
    ): Response<ApiResponse<Any>>

    @GET("api/groups/{groupId}/messages")
    suspend fun getGroupMessages(
        @Path("groupId") groupId: String,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Message>>>

    @POST("api/groups/{groupId}/messages")
    suspend fun sendGroupMessage(
        @Path("groupId") groupId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<Message>>
}