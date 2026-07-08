package com.example.roombooking.application;

import com.example.roombooking.domain.Booking;

public sealed interface BookingResult {

    record Confirmed(Booking booking) implements BookingResult {
    }

    record Rejected(String reason) implements BookingResult {
    }
}
