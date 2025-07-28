package com.workflex.controller;

import com.workflex.dto.TripRequestDTO;
import com.workflex.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflex")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping("/trip")
    public ResponseEntity<String> addTrip(@RequestBody TripRequestDTO request) {
        tripService.addTrip(request.getUserId(), request.getDestination());
        return ResponseEntity.ok("Trip added!");
    }

    @GetMapping("/destination/recommendations")
    public List<String> getRecommendations(@RequestParam String destination) {
        return tripService.getRecommendations(destination);
    }
}