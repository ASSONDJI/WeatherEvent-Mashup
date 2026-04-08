package com.mashup.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends BusinessException {

    private static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";
    private static final int HTTP_STATUS = 429;

    private final int retryAfterSeconds;

    public RateLimitException(String apiName, int retryAfterSeconds) {
        super(
                String.format("Rate limit exceeded for API '%s'. Retry after %d seconds.", apiName, retryAfterSeconds),
                ERROR_CODE,
                HTTP_STATUS
        );
        this.retryAfterSeconds = retryAfterSeconds;
    }
}