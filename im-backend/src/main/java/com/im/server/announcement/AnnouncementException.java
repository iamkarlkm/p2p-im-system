package com.im.server.announcement;

/**
 * 群公告异常
 */
public class AnnouncementException extends RuntimeException {
    
    public AnnouncementException(String message) {
        super(message);
    }
    
    public AnnouncementException(String message, Throwable cause) {
        super(message, cause);
    }
}
