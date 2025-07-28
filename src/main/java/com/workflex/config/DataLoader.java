package com.workflex.config;

import com.opencsv.CSVReader;
import com.workflex.model.Trip;
import com.workflex.model.User;
import com.workflex.repository.TripRepository;
import com.workflex.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;

@Component
public class DataLoader implements CommandLineRunner {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public DataLoader(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/trip-tracking.csv"))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line[0].equalsIgnoreCase("user_id")) continue;
                String userId = line[0].trim();
                String destination = line[1].trim();

                User user = userRepository.findById(userId).orElseGet(() -> userRepository.save(new User(userId)));
                tripRepository.save(new Trip(null, destination, user));
            }
        }
    }
}