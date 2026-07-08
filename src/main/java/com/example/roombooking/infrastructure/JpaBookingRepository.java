package com.example.roombooking.infrastructure;

import com.example.roombooking.application.BookingRepository;
import com.example.roombooking.domain.Booking;
import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.domain.TimeSlot;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaBookingRepository implements BookingRepository {

    private final BookingJpaRepository jpaRepository;

    public JpaBookingRepository(BookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Booking> findByRoom(RoomId roomId) {
        return jpaRepository.findByRoomId(roomId.value()).stream()
                .map(JpaBookingRepository::toDomain)
                .toList();
    }

    @Override
    public void save(Booking booking) {
        jpaRepository.save(toEntity(booking));
    }

    /** Används av acceptanstesterna för att nollställa tillstånd mellan scenarier. */
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    private static BookingEntity toEntity(Booking booking) {
        var timeSlot = booking.timeSlot();
        return new BookingEntity(
                booking.id().value(),
                booking.roomId().value(),
                timeSlot.day(),
                timeSlot.start(),
                timeSlot.end(),
                booking.bookedBy());
    }

    private static Booking toDomain(BookingEntity entity) {
        return new Booking(
                new Booking.BookingId(entity.getId()),
                new RoomId(entity.getRoomId()),
                new TimeSlot(entity.getDay(), entity.getStartTime(), entity.getEndTime()),
                entity.getBookedBy());
    }
}