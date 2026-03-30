use crate::models::*;
use std::collections::HashMap;
use std::sync::Mutex;
use once_cell::sync::Lazy;

static MESSAGES: Lazy<Mutex<HashMap<String, Vec<Message>>>> = Lazy::new(|| {
    Mutex::new(HashMap::new())
});

pub struct ChatService;

impl ChatService {
    pub fn save_message(conversation_id: String, message: Message) {
        if let Ok(mut guard) = MESSAGES.lock() {
            guard.entry(conversation_id).or_insert_with(Vec::new).push(message);
        }
    }
    
    pub fn get_messages(conversation_id: &str) -> Vec<Message> {
        MESSAGES.lock()
            .ok()
            .and_then(|guard| guard.get(conversation_id).cloned())
            .unwrap_or_default()
    }
    
    pub fn get_all_conversations() -> Vec<String> {
        MESSAGES.lock()
            .ok()
            .map(|guard| guard.keys().cloned().collect())
            .unwrap_or_default()
    }
}
