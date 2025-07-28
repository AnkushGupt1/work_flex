package com.workflex.repository;

import com.workflex.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByDestination(String destination);
    List<Trip> findByUser_UserId(String userId);
}