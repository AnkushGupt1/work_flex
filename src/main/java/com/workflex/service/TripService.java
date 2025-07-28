package com.workflex.service;

import com.workflex.model.Trip;
import com.workflex.model.User;
import com.workflex.repository.TripRepository;
import com.workflex.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public void addTrip(String userId, String destination) {
        User user = userRepository.findById(userId)
                .orElseGet(() -> userRepository.save(new User(userId)));
        tripRepository.save(new Trip(null, destination, user));
    }

    public List<String> getRecommendations(String inputDestination) {
        List<Trip> allTrips = tripRepository.findAll();
        Map<String, Set<String>> destinationToUsers = new HashMap<>();
        Map<String, Set<String>> userToDestinations = new HashMap<>();

        for (Trip trip : allTrips) {
            destinationToUsers.computeIfAbsent(trip.getDestination(), k -> new HashSet<>()).add(trip.getUser().getUserId());
            userToDestinations.computeIfAbsent(trip.getUser().getUserId(), k -> new HashSet<>()).add(trip.getDestination());
        }

        if (!destinationToUsers.containsKey(inputDestination)) return List.of();

        Set<String> usersForInput = destinationToUsers.get(inputDestination);
        Map<String, Integer> coOccurrence = new HashMap<>();
        Map<String, Integer> destinationCounts = new HashMap<>();

        for (String user : usersForInput) {
            for (String dest : userToDestinations.get(user)) {
                if (!dest.equals(inputDestination)) {
                    coOccurrence.put(dest, coOccurrence.getOrDefault(dest, 0) + 1);
                }
            }
        }

        for (String dest : destinationToUsers.keySet()) {
            if (!dest.equals(inputDestination)) {
                destinationCounts.put(dest, destinationToUsers.get(dest).size());
            }
        }

        Map<String, Double> similarity = new HashMap<>();
        for (String dest : coOccurrence.keySet()) {
            int commonUsers = coOccurrence.get(dest);
            int a = usersForInput.size();
            int b = destinationCounts.getOrDefault(dest, 1);
            double sim = commonUsers / (Math.sqrt(a) * Math.sqrt(b));
            similarity.put(dest, sim);
        }

        return similarity.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}