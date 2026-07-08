package com.example.roombooking.application;

import com.example.roombooking.domain.Room;
import com.example.roombooking.domain.Room.RoomId;

import java.util.Optional;

public interface RoomRepository {

    Optional<Room> findById(RoomId id);
}
