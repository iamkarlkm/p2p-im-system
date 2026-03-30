use serde::{Deserialize, Serialize};

pub mod commands;
pub mod models;
pub mod services;
pub mod utils;

#[derive(Debug, Serialize, Deserialize)]
pub struct AppConfig {
    pub api_base_url: String,
    pub ws_url: String,
    pub app_name: String,
    pub version: String,
}

impl Default for AppConfig {
    fn default() -> Self {
        Self {
            api_base_url: "http://localhost:8080".to_string(),
            ws_url: "ws://localhost:8080/ws".to_string(),
            app_name: "IM Desktop".to_string(),
            version: env!("CARGO_PKG_VERSION").to_string(),
        }
    }
}

pub fn add(left: u64, right: u64) -> u64 {
    left + right
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
