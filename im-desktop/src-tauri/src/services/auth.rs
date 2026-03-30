use crate::models::*;
use std::sync::Mutex;
use once_cell::sync::Lazy;

static CURRENT_USER: Lazy<Mutex<Option<User>>> = Lazy::new(|| Mutex::new(None));
static AUTH_TOKEN: Lazy<Mutex<Option<String>>> = Lazy::new(|| Mutex::new(None));

pub struct AuthService;

impl AuthService {
    pub fn set_current_user(user: User) {
        if let Ok(mut guard) = CURRENT_USER.lock() {
            *guard = Some(user);
        }
    }
    
    pub fn get_current_user() -> Option<User> {
        CURRENT_USER.lock().ok()?.clone()
    }
    
    pub fn set_token(token: String) {
        if let Ok(mut guard) = AUTH_TOKEN.lock() {
            *guard = Some(token);
        }
    }
    
    pub fn get_token() -> Option<String> {
        AUTH_TOKEN.lock().ok()?.clone()
    }
    
    pub fn clear() {
        if let Ok(mut user_guard) = CURRENT_USER.lock() {
            *user_guard = None;
        }
        if let Ok(mut token_guard) = AUTH_TOKEN.lock() {
            *token_guard = None;
        }
    }
    
    pub fn is_authenticated() -> bool {
        Self::get_token().is_some()
    }
}
