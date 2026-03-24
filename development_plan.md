# 开发计划

## 功能开发状态

| # | 功能名称 | 模块 | 状态 | 开始时间 | 完成时间 | 优先级 |
|---|---------|------|------|---------|---------|-------|
| 1 | 项目结构初始化 | all | 已完成 | 2026-03-18 | 2026-03-18 | P0 |
| 2 | 用户注册与登录API | backend | 已完成 | 2026-03-18 | 2026-03-18 | P0 |
| 3 | WebSocket长连接服务 | backend | 已完成 | 2026-03-19 | 2026-03-19 | P0 |
| 4 | 基础文本消息收发 | backend | 已完成 | 2026-03-19 | 2026-03-19 | P0 |
| 5 | 好友关系管理 | backend | 已完成 | 2026-03-24 10:50 | 2026-03-24 11:15 | P1 |
| 6 | 单聊功能 | backend | 待开发 | - | - | P1 |
| 7 | Tauri桌面端项目搭建 | desktop | 待开发 | - | - | P0 |
| 8 | 登录界面 | desktop | 待开发 | - | - | P0 |
| 9 | 联系人列表界面 | desktop | 待开发 | - | - | P0 |
| 10 | 聊天窗口界面 | desktop | 待开发 | - | - | P0 |
| 11 | Flutter移动端项目搭建 | mobile | 待开发 | - | - | P0 |
| 12 | 登录界面 | mobile | 待开发 | - | - | P0 |
| 13 | 联系人列表界面 | mobile | 待开发 | - | - | P0 |
| 14 | 聊天窗口界面 | mobile | 待开发 | - | - | P0 |

## 开发顺序
1. ~~项目结构初始化~~ ✅
2. ~~用户注册与登录API~~ ✅
3. WebSocket长连接服务
4. 基础文本消息收发
5. Tauri桌面端项目搭建 + 登录界面
6. 联系人列表 + 聊天窗口
7. Flutter移动端项目搭建 + 登录界面
8. 联系人列表 + 聊天窗口

## 最近开发记录
- 2026-03-24: 完成好友关系管理功能
  - 创建了FriendController.java，好友关系控制器（9个接口）
  - 更新了FriendService.java，新增searchFriends、isFriend、getSentRequests、cancelFriendRequest方法
  - 创建了SendFriendRequest.java，发送好友申请请求DTO
  - 创建了friend_api_docs.md，API文档
  - 支持：发送/撤回好友申请、处理好友申请、搜索好友、检查好友状态
- 2026-03-19: 完成基础文本消息收发
  - 创建了MessageController.java，REST API消息控制器（8个接口）
  - 更新了WebSocketController.java，集成MessageService实现消息持久化
  - 更新了WebSocketService.java，新增getUserIdByUsername方法和用户ID缓存
  - 更新了MessageService.java，新增getRecentConversations方法
  - 支持REST API和WebSocket双通道发送消息，消息持久化到MySQL
- 2026-03-19: 完成WebSocket长连接服务
  - 创建了WebSocketController.java，处理STOMP消息路由
  - 创建了WebSocketService.java，管理在线用户会话（支持多设备）
  - 创建了WebSocketEventListener.java，监听连接/断开事件
  - 创建了WsMessageDTO.java，WebSocket消息格式定义
  - 支持：私聊消息、群聊消息、心跳检测、在线用户列表
- 2026-03-18: 完成项目结构初始化和用户注册与登录API
  - 创建了 controller 目录
  - 实现了 AuthController.java
  - API接口包括：/api/auth/register、/api/auth/login、/api/auth/me、/api/auth/refresh、/api/auth/validate
