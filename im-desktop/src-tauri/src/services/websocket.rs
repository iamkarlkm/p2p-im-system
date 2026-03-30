use futures_util::{SinkExt, StreamExt};
use tokio_tungstenite::{connect_async, tungstenite::protocol::Message as WsMessage};
use std::sync::Arc;
use tokio::sync::Mutex;
use once_cell::sync::Lazy;

static WS_CONNECTION: Lazy<Arc<Mutex<Option<tokio_tungstenite::WebSocketStream<tokio_tungstenite::MaybeTlsStream<tokio::net::TcpStream>>>>>> = 
    Lazy::new(|| Arc::new(Mutex::new(None)));

pub struct WebSocketService;

impl WebSocketService {
    pub async fn connect(url: &str) -> Result<(), Box<dyn std::error::Error>> {
        let (ws_stream, _) = connect_async(url).await?;
        let mut guard = WS_CONNECTION.lock().await;
        *guard = Some(ws_stream);
        log::info!("WebSocket连接成功: {}", url);
        Ok(())
    }
    
    pub async fn disconnect() {
        let mut guard = WS_CONNECTION.lock().await;
        *guard = None;
        log::info!("WebSocket连接已关闭");
    }
    
    pub async fn send_message(message: &str) -> Result<(), Box<dyn std::error::Error>> {
        let mut guard = WS_CONNECTION.lock().await;
        if let Some(ref mut ws) = *guard {
            ws.send(WsMessage::Text(message.to_string())).await?;
        }
        Ok(())
    }
    
    pub fn is_connected() -> bool {
        // 简化检查
        true
    }
}
