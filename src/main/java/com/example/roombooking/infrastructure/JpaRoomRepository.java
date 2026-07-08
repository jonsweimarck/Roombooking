package com.example.roombooking.infrastructure;

import com.example.roombooking.application.RoomRepository;
import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaRoomRepository implements RoomRepository {

    private final RoomJpaRepository jpaRepository;

    public JpaRoomRepository(RoomJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        return jpaRepository.findById(id.value()).map(JpaRoomRepository::toDomain);
    }

    /** Används av acceptanstesterna för att så ett rum inför persistens-scenarier. */
    public void save(Room room) {
        jpaRepository.save(toEntity(room));
    }

    private static RoomEntity toEntity(Room room) {
        return new RoomEntity(room.id().value());
    }

    private static Room toDomain(RoomEntity entity) {
        return new Room(new RoomId(entity.getId()));
    }
}
