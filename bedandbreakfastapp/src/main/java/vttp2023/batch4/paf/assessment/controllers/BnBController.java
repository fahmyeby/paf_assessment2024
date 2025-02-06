package vttp2023.batch4.paf.assessment.controllers;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.services.ListingsService;

@Controller
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class BnBController {

	// You may add additional dependency injections

	@Autowired
	private ListingsService listingsSvc;

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	@GetMapping("/suburbs")
	@ResponseBody
	public ResponseEntity<String> getSuburbs() {
		List<String> suburbs = listingsSvc.getAustralianSuburbs();
		JsonArray result = Json.createArrayBuilder(suburbs).build();
		return ResponseEntity.ok(result.toString());
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked

	@GetMapping("/search")
	@ResponseBody
	public ResponseEntity<String> search(@RequestParam MultiValueMap<String, String> params) {
		String suburb = params.getFirst("suburb");
		int persons = Integer.parseInt(params.getFirst("persons"));
		int duration = Integer.parseInt(params.getFirst("duration"));
		float priceRange = Float.parseFloat(params.getFirst("price_range"));

		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		listingsSvc.findAccommodatations(suburb, persons, duration, priceRange)
				.stream()
				.forEach(acc -> arrBuilder.add(
						Json.createObjectBuilder()
								.add("id", acc.getId())
								.add("name", acc.getName())
								.add("price", acc.getPrice())
								.add("accommodates", acc.getAccomodates())
								.build()));

		return ResponseEntity.ok(arrBuilder.build().toString());
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked

	@GetMapping("/accommodation/{id}")

	@ResponseBody
	public ResponseEntity<String> getAccommodationById(@PathVariable String id) {
		Optional<Accommodation> accommodation = listingsSvc.findAccommodatationById(id);
		if (accommodation.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().body(accommodation.get().toJson().toString());
	}

	// TODO: Task 6

	/*
	 * @PostMapping(path = {
	 * "/review" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces
	 * = MediaType.APPLICATION_JSON_VALUE)
	 * public ResponseEntity<String> updateReview(@RequestBody MultiValueMap<String,
	 * String> formData) {
	 * try {
	 * String listingId = formData.getFirst("_id");
	 * String reviewerName = formData.getFirst("name");
	 * String comments = formData.getFirst("comments");
	 * 
	 * 
	 * 
	 * return ResponseEntity.ok("{}");
	 * } catch (Exception e) {
	 * 
	 * String errorMessage = "An error occurred during booking.";
	 * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	 * .body("{\"message\":\"" + errorMessage + "\"}");
	 * }
	 * }
	 */

	 @PostMapping(path = "/accommodation", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> createBooking(@RequestBody String payload) {
        try {
            // Parse and validate JSON
            JsonObject json = Json.createReader(new StringReader(payload)).readObject();

            // Extract and validate fields
            String listingId = getRequiredString(json, "id");
            String name = getRequiredString(json, "name");
            String email = getRequiredString(json, "email");
            int nights = getRequiredInt(json, "nights");

            // Process booking
            listingsSvc.createBooking(listingId, name, email, nights);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();

        } catch (IllegalArgumentException | JsonException e) {
            return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(String.format("{\"message\": \"%s\"}", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(String.format("{\"message\": \"%s\"}", e.getMessage()));
        }
    }

    private String getRequiredString(JsonObject json, String key) {
        if (!json.containsKey(key)) {
            throw new IllegalArgumentException(key + " is required");
        }
        String value = json.getString(key).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(key + " cannot be empty");
        }
        return value;
    }

    private int getRequiredInt(JsonObject json, String key) {
        if (!json.containsKey(key)) {
            throw new IllegalArgumentException(key + " is required");
        }
        int value = json.getInt(key);
        if (value <= 0) {
            throw new IllegalArgumentException(key + " must be greater than 0");
        }
        return value;
    }
}
