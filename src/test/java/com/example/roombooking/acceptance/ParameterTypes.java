package com.example.roombooking.acceptance;

import io.cucumber.java.ParameterType;

import java.time.LocalTime;

public class ParameterTypes {

    @ParameterType("\\d{1,2}:\\d{2}")
    public LocalTime tid(String värde) {
        return LocalTime.parse(värde);
    }
}
