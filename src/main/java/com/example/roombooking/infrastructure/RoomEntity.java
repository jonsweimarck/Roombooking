package com.example.roombooking.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RoomEntity {

    @Id
    private String id;

    protected RoomEntity() {
    }

    RoomEntity(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }
}
