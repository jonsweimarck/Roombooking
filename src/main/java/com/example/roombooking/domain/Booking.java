package com.example.roombooking.domain;

import com.example.roombooking.domain.Room.RoomId;

import java.util.UUID;

public record Booking(BookingId id, RoomId roomId, TimeSlot timeSlot, String bookedBy) {

    public static Booking create(RoomId roomId, TimeSlot timeSlot, String bookedBy) {
        return new Booking(new BookingId(UUID.randomUUID()), roomId, timeSlot, bookedBy);
    }

    public record BookingId(UUID value) {
    }
}
