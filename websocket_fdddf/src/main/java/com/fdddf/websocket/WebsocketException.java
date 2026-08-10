package com.fdddf.websocket;

public class WebsocketException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public WebsocketException(String message) {
        super(message);
    }
    public WebsocketException(String message, Throwable cause) {
        super(message, cause);
    }
    public WebsocketException(Throwable cause) {
        super(cause);
    }
}
