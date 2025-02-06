package vttp2023.batch4.paf.assessment.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;
import vttp2023.batch4.paf.assessment.models.Bookings;
import vttp2023.batch4.paf.assessment.models.User;
import vttp2023.batch4.paf.assessment.repositories.BookingsRepository;
import vttp2023.batch4.paf.assessment.repositories.ListingsRepository;

@Service
@Transactional
public class ListingsService {

	// You may add additional dependency injections

	@Autowired
	private ListingsRepository listingsRepo;

	@Autowired
	private BookingsRepository repo;

	@Autowired
	private ForexService forexSvc;

	// IMPORTANT: DO NOT MODIFY THIS METHOD.
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public List<String> getAustralianSuburbs() {
		try {
			return listingsRepo.findAllSuburbs();
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve suburbs", e);
		}
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public List<AccommodationSummary> findAccommodatations(String suburb, int persons, int duration, float priceRange) {
		try {
			return listingsRepo.searchAccommodations(suburb, priceRange, persons, duration);
		} catch (Exception e) {
			throw new RuntimeException("Search failed", e);
		}
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		try {
			return listingsRepo.findAccommodationById(id);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve accommodation", e);
		}
	}

	// TODO: Task 6
	// IMPORTANT: DO NOT MODIFY THE SIGNATURE OF THIS METHOD.
	// You may only add annotations and throw exceptions to this method
	public void createBooking(String listingId, String name, String email, int nights) {
        // Input validation
        if (listingId == null || listingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Listing ID is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than 0");
        }

        try {
            // Create or verify user
            User user = new User(email.trim(), name.trim());
            repo.newUser(user);

            // Create booking
            Bookings booking = new Bookings();
            booking.setListingId(listingId.trim());
            booking.setEmail(email.trim());
            booking.setName(name.trim());
            booking.setDuration(nights);
            repo.newBookings(booking);
            
        } catch (Exception e) {
            throw new RuntimeException("Booking creation failed: " + e.getMessage());
        }
    }
}
