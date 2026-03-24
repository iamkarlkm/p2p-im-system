import codecs

filepath = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
with codecs.open(filepath, 'r', 'utf-8') as f:
    content = f.read()

old_text = """### 15. AI聊天机器人API (AI Chatbot API)
- **功能描述**: 机器人CRUD管理，AI大模型集成，Webhook回调支持，斜杠命令系统
- **模块**: im-backend, im-desktop, im-mobile
- **状态**: 待开发
- **优先级**: 中
- **预计工作量**: 3天
- **开始时间**:
- **完成时间**:
- **相关文件**:
- **代码行数**:
- **功能特性**:"""

new_text = """### 15. AI聊天机器人API (AI Chatbot API)
- **功能描述**: 机器人CRUD管理，AI大模型集成，Webhook回调支持，斜杠命令系统
- **模块**: im-backend, im-desktop, im-mobile
- **状态**: 已完成
- **优先级**: 中
- **预计工作量**: 3天
- **开始时间**: 2026-03-19 20:33
- **完成时间**: 2026-03-19 20:33
- **相关文件**:
  - im-backend/src/main/java/com/im/server/chatbot/Bot.java
  - im-backend/src/main/java/com/im/server/chatbot/BotRepository.java
  - im-backend/src/main/java/com/im/server/chatbot/BotService.java
  - im-backend/src/main/java/com/im/server/chatbot/BotController.java
  - im-backend/src/main/java/com/im/server/chatbot/BotWebSocketHandler.java
  - im-backend/src/main/java/com/im/server/chatbot/BotException.java
  - im-desktop/src/services/bot_service.ts
  - im-desktop/src/components/bot_card.ts
  - im-desktop/src/services/bot.css
  - im-mobile/lib/models/bot.dart
  - im-mobile/lib/services/bot_service.dart
  - im-mobile/lib/widgets/bot_widgets.dart
- **代码行数**: 约 8800 行
- **功能特性**:
  - 机器人CRUD管理（创建/获取/更新/删除）
  - 多类型支持（AI对话/客服/通知/工具/资讯/娱乐）
  - AI大模型集成配置（OpenAI/Claude/文心一言/讯飞星火）
  - 自定义API端点支持
  - 斜杠命令系统（/help, /status, /reset, /model）
  - 群组白名单管理、@提及响应模式
  - 私聊/群聊权限控制
  - WebSocket实时消息推送、事件订阅系统
  - 会话上下文管理、消息统计"""

content = content.replace(old_text, new_text)

# Update "已完成功能" section
old_complete = """### 17. 群公告功能
- **完成时间**: 2026-03-19 18:40
- **状态**: 已完成
- **备注**: 支持Markdown、置顶、重要公告分类、WebSocket实时推送"""

new_complete = """### 17. 群公告功能
- **完成时间**: 2026-03-19 18:40
- **状态**: 已完成
- **备注**: 支持Markdown、置顶、重要公告分类、WebSocket实时推送

### 18. AI聊天机器人API
- **完成时间**: 2026-03-19 20:33
- **状态**: 已完成
- **备注**: 机器人CRUD、AI大模型集成、斜杠命令、WebSocket推送"""

content = content.replace(old_complete, new_complete)

# Update the "最后更新" timestamp
content = content.replace(
    "**最后更新**: 2026-03-19 18:40",
    "**最后更新**: 2026-03-19 20:33"
)
content = content.replace(
    "*开发计划最后更新: 2026-03-19 18:40*",
    "*开发计划最后更新: 2026-03-19 20:33*"
)

with codecs.open(filepath, 'w', 'utf-8') as f:
    f.write(content)

print("Development plan updated successfully")
