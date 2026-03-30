use tauri::command;
use crate::models::*;

#[command]
pub async fn login(request: LoginRequest) -> Result<ApiResponse<LoginResponse>, String> {
    log::info!("登录请求: {}", request.username);
    
    Ok(ApiResponse {
        code: 200,
        message: "登录成功".to_string(),
        data: Some(LoginResponse {
            token: "mock_token_12345".to_string(),
            user: User {
                id: "user_001".to_string(),
                username: request.username.clone(),
                nickname: Some("测试用户".to_string()),
                avatar: None,
                status: UserStatus::Online,
            },
            expires_in: 86400,
        }),
    })
}

#[command]
pub async fn logout() -> Result<ApiResponse<()>, String> {
    log::info!("用户登出");
    
    Ok(ApiResponse {
        code: 200,
        message: "登出成功".to_string(),
        data: Some(()),
    })
}

#[command]
pub async fn get_conversations() -> Result<ApiResponse<Vec<Conversation>>, String> {
    let conversations = vec![
        Conversation {
            id: "conv_001".to_string(),
            conversation_type: ConversationType::Private,
            title: "张三".to_string(),
            avatar: None,
            last_message: Some(Message {
                id: "msg_001".to_string(),
                sender_id: "user_002".to_string(),
                receiver_id: "user_001".to_string(),
                content: "你好，在吗？".to_string(),
                message_type: MessageType::Text,
                timestamp: chrono::Utc::now().timestamp_millis(),
                status: MessageStatus::Read,
            }),
            unread_count: 0,
            updated_at: chrono::Utc::now().timestamp_millis(),
        },
        Conversation {
            id: "conv_002".to_string(),
            conversation_type: ConversationType::Group,
            title: "工作群".to_string(),
            avatar: None,
            last_message: Some(Message {
                id: "msg_002".to_string(),
                sender_id: "user_003".to_string(),
                receiver_id: "group_001".to_string(),
                content: "下午开会".to_string(),
                message_type: MessageType::Text,
                timestamp: chrono::Utc::now().timestamp_millis(),
                status: MessageStatus::Delivered,
            }),
            unread_count: 5,
            updated_at: chrono::Utc::now().timestamp_millis(),
        },
    ];
    
    Ok(ApiResponse {
        code: 200,
        message: "获取成功".to_string(),
        data: Some(conversations),
    })
}

#[command]
pub async fn send_message(conversation_id: String, content: String) -> Result<ApiResponse<Message>, String> {
    log::info!("发送消息到会话: {}", conversation_id);
    
    Ok(ApiResponse {
        code: 200,
        message: "发送成功".to_string(),
        data: Some(Message {
            id: format!("msg_{}", uuid::Uuid::new_v4()),
            sender_id: "user_001".to_string(),
            receiver_id: conversation_id,
            content,
            message_type: MessageType::Text,
            timestamp: chrono::Utc::now().timestamp_millis(),
            status: MessageStatus::Sent,
        }),
    })
}
