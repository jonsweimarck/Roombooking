package com.example.roombooking.domain;

public record Room(RoomId id) {

    public record RoomId(String value) {
        public RoomId {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Rum-id kan inte vara tomt");
            }
        }
    }
}
