package com.example.roombooking.application;

import com.example.roombooking.domain.Room.RoomId;
import com.example.roombooking.infrastructure.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoomAdminService")
class RoomAdminServiceTest {

    private RoomAdminService roomAdminService;
    private InMemoryRoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository = new InMemoryRoomRepository();
        roomAdminService = new RoomAdminService(roomRepository);
    }

    @Test
    @DisplayName("ska göra rummet sökbart i repositoryt")
    void skaGöraRummetSökbartIRepositoryt() {
        roomAdminService.addRoom(new RoomId("R205"));

        assertThat(roomRepository.findById(new RoomId("R205"))).isPresent();
    }
}
