package com.fdddf.zipencrypt;

public class ZipRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ZipRuntimeException(Throwable cause)
    {
        super(cause);
    }

    public ZipRuntimeException(String message)
    {
        super(message);
    }
}