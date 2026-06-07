# Cloudflare Chat Android

基于 [Cloudflare Chat App](https://github.com/mcbug0087/cloudflare_chat_app) 后端 API 开发的 Android 客户端。

## 功能

- 用户注册/登录
- 好友管理（添加、删除、搜索、备注）
- 私聊（实时消息、在线状态）
- 群聊（创建、加入、管理、实时消息）
- WebSocket 实时通信

## 技术栈

- Kotlin
- Retrofit + OkHttp (HTTP & WebSocket)
- Gson
- ViewModel + LiveData
- Material Design 3
- ViewBinding

## 构建

```bash
./gradlew assembleDebug    # 构建 Debug APK
./gradlew assembleRelease  # 构建 Release APK
```

## 最低版本要求

- Android 7.0 (API 24)
- 目标版本: Android 13.0 (API 33)