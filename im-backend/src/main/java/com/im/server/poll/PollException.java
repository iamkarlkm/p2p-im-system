package com.im.server.poll;

/**
 * 投票异常
 */
public class PollException extends RuntimeException {

    public PollException(String message) {
        super(message);
    }

    public PollException(String message, Throwable cause) {
        super(message, cause);
    }
}
