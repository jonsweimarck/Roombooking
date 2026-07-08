package com.example.roombooking.application;

import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;
import org.springframework.stereotype.Service;

@Service
public class RoomAdminService {

    private final RoomRepository roomRepository;

    public RoomAdminService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void addRoom(RoomId id) {
        roomRepository.save(new Room(id));
    }
}
