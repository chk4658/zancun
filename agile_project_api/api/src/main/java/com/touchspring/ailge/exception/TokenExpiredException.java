package com.touchspring.ailge.exception;

/**
 * Created by feng- on 2017/5/29.
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        this(message, null);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
