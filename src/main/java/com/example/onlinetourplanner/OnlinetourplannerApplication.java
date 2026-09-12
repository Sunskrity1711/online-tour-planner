package com.example.onlinetourplanner;

import com.example.onlinetourplanner.model.Trip;
import com.example.onlinetourplanner.repository.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlinetourplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlinetourplannerApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(TripRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.save(new Trip(null, "Goa Luxury Beach Vacation", "Goa", 24999.00, 4,
						"✈️ Indigo Direct Round-Trip Flight",
						"🚌 Private AC SUV Airport Pickup",
						"🏨 5-Star Taj Resort Stay",
						"Day 1: Airport Pick & Beachside Relax | Day 2: Cruise & Watersports | Day 3: Heritage Tour | Day 4: Return Flight",
						"https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=600"));

				repository.save(new Trip(null, "Himalayan Manali Trek", "Manali", 18500.00, 5,
						"✈️ Delhi Flight + Volvo Connect",
						"🚌 Luxury AC Volvo Bus Transfer",
						"🏨 Mountain View Cottage Stay",
						"Day 1: Arrival & Mall Road Walk | Day 2: Solang Valley Snow Point | Day 3: Atal Tunnel & Sissu | Day 4: Departure",
						"https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=600"));
			}
		};
	}
}