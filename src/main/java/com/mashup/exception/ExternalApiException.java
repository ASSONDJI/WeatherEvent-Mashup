package com.mashup.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends BusinessException {

    private static final String ERROR_CODE = "EXTERNAL_API_ERROR";
    private static final int HTTP_STATUS = 503;

    private final String apiName;
    private final String endpoint;
    private final int statusCode;

    public ExternalApiException(String apiName, String endpoint, int statusCode) {
        super(
                String.format("External API '%s' returned error %d for endpoint '%s'", apiName, statusCode, endpoint),
                ERROR_CODE,
                HTTP_STATUS
        );
        this.apiName = apiName;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
    }

    public ExternalApiException(String apiName, String endpoint, Throwable cause) {
        super(
                String.format("External API '%s' is unavailable for endpoint '%s'", apiName, endpoint),
                cause,
                ERROR_CODE,
                HTTP_STATUS
        );
        this.apiName = apiName;
        this.endpoint = endpoint;
        this.statusCode = 0;
    }
}