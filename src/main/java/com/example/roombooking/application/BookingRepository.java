package com.example.roombooking.application;

import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;

import java.util.List;

public interface BookingRepository {

    List<Booking> findByRoom(RoomId roomId);

    void save(Booking booking);
}
