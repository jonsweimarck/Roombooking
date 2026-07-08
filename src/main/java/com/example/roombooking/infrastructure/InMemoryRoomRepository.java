package com.example.roombooking.infrastructure;

import com.example.roombooking.application.RoomRepository;
import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Används numera enbart av acceptanstesterna för affärsreglerna kring bokning
 * (bokning.feature) - inte Spring-hanterad. Produktionskonfigurationen använder
 * {@link JpaRoomRepository} mot Postgres, se rum-persistens.feature.
 */
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public InMemoryRoomRepository() {
        // Startdata så acceptanstesterna har något att boka mot.
        save(new Room(new RoomId("R204")));
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        return Optional.ofNullable(rooms.get(id.value()));
    }

    @Override
    public void save(Room room) {
        rooms.put(room.id().value(), room);
    }
}
