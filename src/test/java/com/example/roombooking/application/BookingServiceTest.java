package com.example.roombooking.application;

import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingServiceTest {

    private BookingService bookingService;
    private InMemoryRoomRepository roomRepository;
    private InMemoryBookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        roomRepository = new InMemoryRoomRepository();
        bookingRepository = new InMemoryBookingRepository();
        bookingService = new BookingService(roomRepository, bookingRepository);
    }

    @Test
    void bekraftarBokningAvLedigtRum() {
        var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

        var result = bookingService.book(new RoomId("R204"), timeSlot, "Alva");

        assertThat(result).isInstanceOf(BookingResult.Confirmed.class);
    }

    @Test
    void avslarOverlappandeBokning() {
        var forsta = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        var overlappande = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));
        bookingService.book(new RoomId("R204"), forsta, "Alva");

        var result = bookingService.book(new RoomId("R204"), overlappande, "Björn");

        assertThat(result).isInstanceOf(BookingResult.Rejected.class);
        assertThat(((BookingResult.Rejected) result).reason()).isEqualTo("Överlappande bokning");
    }

    @Test
    void avslarBokningAvOkantRum() {
        var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

        var result = bookingService.book(new RoomId("OKÄNT"), timeSlot, "Alva");

        assertThat(result).isInstanceOf(BookingResult.Rejected.class);
    }
}
