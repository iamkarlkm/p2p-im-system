import codecs

new_entry = """## 2026-03-19

### ✅ 功能 #15: AI聊天机器人API (AI Chatbot API)

**完成时间**: 2026-03-19 20:33
**开发时长**: 约 5 分钟
**代码量**: 约 8800 行

#### 功能特性
- 机器人CRUD管理（创建/获取/更新/删除）
- 多类型支持（AI对话/客服/通知/工具/资讯/娱乐）
- AI大模型集成配置（OpenAI/Claude/文心一言/讯飞星火）
- 自定义API端点支持
- 斜杠命令系统（/help, /status, /reset, /model）
- 群组白名单管理、@提及响应模式
- 私聊/群聊权限控制
- WebSocket实时消息推送
- 打字状态广播、事件订阅系统
- 会话上下文管理、消息统计

#### 新增文件

**Backend (Java)**:
- `im-backend/src/main/java/com/im/server/chatbot/Bot.java`
- `im-backend/src/main/java/com/im/server/chatbot/BotRepository.java`
- `im-backend/src/main/java/com/im/server/chatbot/BotService.java`
- `im-backend/src/main/java/com/im/server/chatbot/BotController.java`
- `im-backend/src/main/java/com/im/server/chatbot/BotWebSocketHandler.java`
- `im-backend/src/main/java/com/im/server/chatbot/BotException.java`

**Desktop (TypeScript)**:
- `im-desktop/src/services/bot_service.ts`
- `im-desktop/src/components/bot_card.ts`
- `im-desktop/src/services/bot.css`

**Mobile (Flutter)**:
- `im-mobile/lib/models/bot.dart`
- `im-mobile/lib/services/bot_service.dart`
- `im-mobile/lib/widgets/bot_widgets.dart`

#### API 设计
```
POST   /api/bot              - 创建机器人
GET    /api/bot/{botId}     - 获取机器人详情
PUT    /api/bot/{botId}     - 更新机器人
DELETE /api/bot/{botId}     - 删除机器人
GET    /api/bot             - 获取机器人列表
GET    /api/bot/user/{userId} - 获取用户机器人
GET    /api/bot/search      - 搜索机器人
POST   /api/bot/{botId}/command - 添加斜杠命令
DELETE /api/bot/{botId}/command - 移除斜杠命令
POST   /api/bot/{botId}/group  - 允许群组
DELETE /api/bot/{botId}/group  - 禁止群组
POST   /api/bot/message    - 发送消息给机器人
```

#### WebSocket 事件
```
bot.created   - 新机器人创建
bot.updated   - 机器人已更新
bot.deleted   - 机器人已删除
bot.message   - 机器人消息
bot.typing    - 打字状态
bot.connection - 连接状态变化
```

---

"""

filepath = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\feature_changelog.md"
with codecs.open(filepath, 'r', 'utf-8') as f:
    existing = f.read()

with codecs.open(filepath, 'w', 'utf-8') as f:
    f.write(new_entry)
    f.write(existing)

print("Changelog updated successfully")
