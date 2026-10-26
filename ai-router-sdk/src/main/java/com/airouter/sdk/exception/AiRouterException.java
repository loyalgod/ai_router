/**
 * AI Router SDK 基础异常
 *
 * @author ai-router
 */
package com.airouter.sdk.exception;

/**
 * AI Router SDK 基础异常类
 */
public class AiRouterException extends RuntimeException {

    private final int code;

    public AiRouterException(String message) {
        super(message);
        this.code = -1;
    }

    public AiRouterException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AiRouterException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
    }

    public AiRouterException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
