#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use tauri::Manager;

fn main() {
    env_logger::init();
    
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .setup(|app| {
            log::info!("IM Desktop 启动成功");
            
            #[cfg(debug_assertions)]
            {
                let window = app.get_webview_window("main").unwrap();
                window.open_devtools();
            }
            
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            greet,
            get_app_version,
            get_config
        ])
        .run(tauri::generate_context!())
        .expect("运行Tauri应用时出错");
}

#[tauri::command]
fn greet(name: &str) -> String {
    format!("你好, {}! 欢迎使用IM Desktop", name)
}

#[tauri::command]
fn get_app_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}

#[tauri::command]
fn get_config() -> serde_json::Value {
    serde_json::json!({
        "apiBaseUrl": "http://localhost:8080",
        "wsUrl": "ws://localhost:8080/ws",
        "appName": "IM Desktop",
        "version": env!("CARGO_PKG_VERSION")
    })
}
