package com.example.roombooking.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface RoomJpaRepository extends JpaRepository<RoomEntity, String> {
}
