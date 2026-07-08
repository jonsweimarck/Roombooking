package com.example.roombooking.infrastructure;

import com.example.roombooking.application.BookingRepository;
import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Används numera enbart av acceptanstesterna för affärsreglerna kring bokning
 * (bokning.feature) - inte Spring-hanterad. Produktionskonfigurationen använder
 * {@link JpaBookingRepository} mot Postgres, se persistens.feature.
 */
public class InMemoryBookingRepository implements BookingRepository {

    private final List<Booking> bookings = new CopyOnWriteArrayList<>();

    @Override
    public List<Booking> findByRoom(RoomId roomId) {
        return bookings.stream()
                .filter(b -> b.roomId().equals(roomId))
                .toList();
    }

    @Override
    public void save(Booking booking) {
        bookings.add(booking);
    }

    /** Används av acceptanstesterna för att nollställa tillstånd mellan scenarier. */
    public void clear() {
        bookings.clear();
    }
}
