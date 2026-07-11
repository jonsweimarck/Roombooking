package com.example.roombooking.application;

import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Booking.BookingId;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository, Clock clock) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    public BookingResult book(RoomId roomId, TimeSlot timeSlot, String bookedBy) {
        if (roomRepository.findById(roomId).isEmpty()) {
            return new BookingResult.Rejected("Rummet finns inte");
        }

        var nu = LocalDateTime.now(clock);
        if (timeSlot.hasPassed(nu.getDayOfWeek(), nu.toLocalTime())) {
            return new BookingResult.Rejected("Kan inte boka bakåt i tiden");
        }

        boolean overlapsExisting = bookingRepository.findByRoom(roomId).stream()
                .anyMatch(existing -> existing.timeSlot().overlaps(timeSlot));

        if (overlapsExisting) {
            return new BookingResult.Rejected("Överlappande bokning");
        }

        Booking booking = Booking.create(roomId, timeSlot, bookedBy);
        bookingRepository.save(booking);
        return new BookingResult.Confirmed(booking);
    }

    public void cancel(BookingId id) {
        bookingRepository.deleteById(id);
    }
}
