package com.example.roombooking.application;

import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public BookingResult book(RoomId roomId, TimeSlot timeSlot, String bookedBy) {
        if (roomRepository.findById(roomId).isEmpty()) {
            return new BookingResult.Rejected("Rummet finns inte");
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
}
