package com.example.roombooking.acceptance;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.application.RoomAdminService;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.InMemoryBookingRepository;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import io.cucumber.java.Before;
import io.cucumber.java.sv.Givet;
import io.cucumber.java.sv.När;
import io.cucumber.java.sv.Så;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Följer samma mönster som BookingSteps: stegen kopplar Gherkin direkt mot
 * applikationslagret (RoomAdminService/BookingService), inte mot webblagret.
 */
public class RoomAdminSteps {

    private InMemoryRoomRepository roomRepository;
    private RoomAdminService roomAdminService;
    private BookingService bookingService;

    @Before
    public void setUp() {
        roomRepository = new InMemoryRoomRepository();
        roomAdminService = new RoomAdminService(roomRepository);
        bookingService = new BookingService(roomRepository, new InMemoryBookingRepository());
    }

    @Givet("att rummet {string} inte finns sedan tidigare")
    public void attRummetInteFinnsSedanTidigare(String rumId) {
        assertThat(roomRepository.findById(new RoomId(rumId))).isEmpty();
    }

    @När("administratören lägger till rummet {string}")
    public void administratörenLäggerTillRummet(String rumId) {
        roomAdminService.addRoom(new RoomId(rumId));
    }

    @Så("ska rummet {string} gå att boka")
    public void skaRummetGåAttBoka(String rumId) {
        var timeSlot = new TimeSlot(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));

        var resultat = bookingService.book(new RoomId(rumId), timeSlot, "Cecilia");

        assertThat(resultat).isInstanceOf(BookingResult.Confirmed.class);
    }
}
