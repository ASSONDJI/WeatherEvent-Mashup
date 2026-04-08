package com.mashup.exception;

public class CacheException extends BusinessException {

    private static final String ERROR_CODE = "CACHE_ERROR";
    private static final int HTTP_STATUS = 500;

    public CacheException(String operation, String key) {
        super(
                String.format("Cache operation '%s' failed for key '%s'", operation, key),
                ERROR_CODE,
                HTTP_STATUS
        );
    }

    public CacheException(String operation, String key, Throwable cause) {
        super(
                String.format("Cache operation '%s' failed for key '%s'", operation, key),
                cause,
                ERROR_CODE,
                HTTP_STATUS
        );
    }
}