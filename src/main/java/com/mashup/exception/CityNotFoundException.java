package com.mashup.exception;

public class CityNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "CITY_NOT_FOUND";
    private static final int HTTP_STATUS = 404;

    public CityNotFoundException(String city) {
        super(
                String.format("City '%s' not found. Please check the city name and try again.", city),
                ERROR_CODE,
                HTTP_STATUS
        );
    }

    public CityNotFoundException(String city, Throwable cause) {
        super(
                String.format("City '%s' not found. Please check the city name and try again.", city),
                cause,
                ERROR_CODE,
                HTTP_STATUS
        );
    }
}