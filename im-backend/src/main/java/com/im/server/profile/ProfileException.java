package com.im.server.profile;

/**
 * 用户资料异常
 */
public class ProfileException extends RuntimeException {
    public ProfileException(String message) {
        super(message);
    }

    public ProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
