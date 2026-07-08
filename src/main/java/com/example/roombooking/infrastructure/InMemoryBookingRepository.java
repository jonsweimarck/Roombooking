package com.example.roombooking.infrastructure;

import com.example.roombooking.application.BookingRepository;
import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final List<Booking> bookings = new CopyOnWriteArrayList<>();

    @Override
    public List<Booking> findByRoom(RoomId roomId) {
        return bookings.stream()
                .filter(b -> b.roomId().equals(roomId))
                .toList();
    }

    @Override
    public Booking save(Booking booking) {
        bookings.add(booking);
        return booking;
    }

    /** Används av acceptanstesterna för att nollställa tillstånd mellan scenarier. */
    public void clear() {
        bookings.clear();
    }
}
