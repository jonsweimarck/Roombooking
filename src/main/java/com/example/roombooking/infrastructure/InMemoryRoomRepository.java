package com.example.roombooking.infrastructure;

import com.example.roombooking.application.RoomRepository;
import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tillfällig implementation. Ersätts av en Postgres-baserad implementation
 * (testdriven med Testcontainers) i nästa iteration - se README.
 */
@Repository
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public InMemoryRoomRepository() {
        // Startdata så acceptanstesterna har något att boka mot.
        addRoom(new Room(new RoomId("R204"), "R204"));
    }

    public void addRoom(Room room) {
        rooms.put(room.id().value(), room);
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        return Optional.ofNullable(rooms.get(id.value()));
    }
}
