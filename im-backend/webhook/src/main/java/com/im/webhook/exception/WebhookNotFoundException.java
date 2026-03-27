package com.im.webhook.exception;

/**
 * Webhook未找到异常
 */
public class WebhookNotFoundException extends RuntimeException {
    
    public WebhookNotFoundException(String message) {
        super(message);
    }
    
    public WebhookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
