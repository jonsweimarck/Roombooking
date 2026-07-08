package com.example.roombooking.acceptance;

import com.example.roombooking.application.BookingResult;
import com.example.roombooking.application.BookingService;
import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import com.example.roombooking.infrastructure.JpaBookingRepository;
import com.example.roombooking.infrastructure.JpaRoomRepository;
import io.cucumber.java.Before;
import io.cucumber.java.sv.Givet;
import io.cucumber.java.sv.När;
import io.cucumber.java.sv.Så;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Till skillnad från BookingSteps körs dessa steg mot Spring-hanterade bönor
 * (BookingService/JpaBookingRepository/JpaRoomRepository) och en riktig
 * Postgres via Testcontainers - se CucumberSpringConfiguration.
 */
public class PersistenceSteps {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JpaBookingRepository bookingRepository;

    @Autowired
    private JpaRoomRepository roomRepository;

    @Autowired
    private EntityManager entityManager;

    private BookingResult senasteResultat;

    @Before
    public void nollställ() {
        bookingRepository.deleteAll();
    }

    @Givet("att ett rum {string} har en bokning")
    public void attEttRumHarEnBokning(String rumId) {
        roomRepository.save(new Room(new RoomId(rumId), rumId));
        var timeSlot = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        bookingService.book(new RoomId(rumId), timeSlot, "Cecilia");
    }

    @Givet("att rummet {string} finns")
    public void attRummetFinns(String rumId) {
        roomRepository.save(new Room(new RoomId(rumId), rumId));
    }

    @När("applikationen startas om")
    public void applikationenStartasOm() {
        entityManager.clear();
    }

    @Så("ska bokningen fortfarande finnas för {string}")
    public void skaBokningenFortfarandeFinnasFör(String rumId) {
        var bokningar = bookingRepository.findByRoom(new RoomId(rumId));
        assertThat(bokningar).isNotEmpty();
    }

    @Så("ska rummet {string} fortfarande gå att boka")
    public void skaRummetFortfarandeGåAttBoka(String rumId) {
        var timeSlot = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        senasteResultat = bookingService.book(new RoomId(rumId), timeSlot, "Cecilia");
        assertThat(senasteResultat).isInstanceOf(BookingResult.Confirmed.class);
    }
}