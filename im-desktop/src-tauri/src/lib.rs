use tauri::Manager;
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct LoginRequest {
    pub username: String,
    pub password: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct LoginResponse {
    pub token: String,
    pub user_id: i64,
    pub username: String,
    pub nickname: String,
    pub avatar_url: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Message {
    pub id: i64,
    pub msg_id: String,
    pub from_user_id: i64,
    pub to_user_id: i64,
    pub chat_type: i32,
    pub chat_id: i64,
    pub msg_type: i32,
    pub content: String,
    pub status: i32,
    pub create_time: String,
}

// Tauri Commands
#[tauri::command]
fn get_app_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

#[tauri::command]
fn get_platform() -> String {
    #[cfg(target_os = "windows")]
    return "windows".to_string();
    #[cfg(target_os = "macos")]
    return "macos".to_string();
    #[cfg(target_os = "linux")]
    return "linux".to_string();
    #[cfg(not(any(target_os = "windows", target_os = "macos", target_os = "linux")))]
    return "unknown".to_string();
}

#[tauri::command]
fn minimize_window(window: tauri::Window) {
    window.minimize().unwrap();
}

#[tauri::command]
fn maximize_window(window: tauri::Window) {
    if window.is_maximized().unwrap_or(false) {
        window.unmaximize().unwrap();
    } else {
        window.maximize().unwrap();
    }
}

#[tauri::command]
fn close_window(window: tauri::Window) {
    window.close().unwrap();
}

#[tauri::command]
fn set_always_on_top(window: tauri::Window, always_on_top: bool) {
    window.set_always_on_top(always_on_top).unwrap();
}

#[tauri::command]
fn get_local_storage(key: String) -> Option<String> {
    // This would typically use a browser API or local storage
    // For now, return None as it's handled in JS
    None
}

#[tauri::command]
fn set_local_storage(key: String, value: String) {
    // This would typically use a browser API or local storage
    // For now, do nothing as it's handled in JS
}

#[tauri::command]
fn clear_local_storage() {
    // This would typically use a browser API or local storage
    // For now, do nothing as it's handled in JS
}

#[tauri::command]
async fn login(request: LoginRequest) -> Result<LoginResponse, String> {
    // This is a placeholder - in production, this would call the actual API
    // For now, return a mock response
    Ok(LoginResponse {
        token: "mock_token_12345".to_string(),
        user_id: 1,
        username: request.username,
        nickname: request.username.clone(),
        avatar_url: None,
    })
}

#[tauri::command]
async fn logout() -> Result<(), String> {
    Ok(())
}

#[tauri::command]
async fn send_message(to_user_id: i64, content: String) -> Result<Message, String> {
    // This is a placeholder - in production, this would call the actual API
    let now = chrono::Utc::now();
    Ok(Message {
        id: 1,
        msg_id: format!("msg_{}", now.timestamp_millis()),
        from_user_id: 1,
        to_user_id,
        chat_type: 1,
        chat_id: to_user_id,
        msg_type: 1,
        content,
        status: 1,
        create_time: now.to_rfc3339(),
    })
}

#[tauri::command]
async fn get_messages(user_id: i64, page: i32, size: i32) -> Result<Vec<Message>, String> {
    // This is a placeholder - in production, this would call the actual API
    Ok(vec![])
}

#[tauri::command]
async fn get_friends() -> Result<Vec<LoginResponse>, String> {
    // This is a placeholder - in production, this would call the actual API
    Ok(vec![])
}

#[tauri::command]
async fn add_friend(friend_id: i64) -> Result<(), String> {
    Ok(())
}

#[tauri::command]
async fn delete_friend(friend_id: i64) -> Result<(), String> {
    Ok(())
}

#[tauri::command]
async fn get_groups() -> Result<Vec<LoginResponse>, String> {
    // This is a placeholder - in production, this would call the actual API
    Ok(vec![])
}

#[tauri::command]
async fn create_group(name: String) -> Result<LoginResponse, String> {
    Ok(LoginResponse {
        token: String::new(),
        user_id: 0,
        username: name,
        nickname: name,
        avatar_url: None,
    })
}

#[tauri::command]
async fn join_group(group_id: i64) -> Result<(), String> {
    Ok(())
}

#[tauri::command]
async fn leave_group(group_id: i64) -> Result<(), String> {
    Ok(())
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .setup(|app| {
            // 打印应用信息
            println!("IM Desktop starting...");
            println!("Version: {}", env!("CARGO_PKG_VERSION"));
            
            // 获取主窗口
            let window = app.get_window("main").unwrap();
            
            // 设置窗口标题
            window.set_title("IM Desktop - 即时通讯").unwrap();
            
            // 设置窗口大小
            window.set_size(tauri::PhysicalSize::new(1200, 800)).unwrap();
            
            // 最小窗口大小
            window.set_min_size(Some(tauri::PhysicalSize::new(800, 600))).unwrap();
            
            println!("IM Desktop started successfully");
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            get_app_version,
            get_platform,
            minimize_window,
            maximize_window,
            close_window,
            set_always_on_top,
            get_local_storage,
            set_local_storage,
            clear_local_storage,
            login,
            logout,
            send_message,
            get_messages,
            get_friends,
            add_friend,
            delete_friend,
            get_groups,
            create_group,
            join_group,
            leave_group,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
