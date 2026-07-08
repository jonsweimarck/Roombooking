package com.example.roombooking.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RoomEntity {

    @Id
    private String id;

    private String name;

    protected RoomEntity() {
    }

    RoomEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
