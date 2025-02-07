package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Accommodation ID cannot be null or empty");
        }

        Criteria criteria = Criteria.where("_id").is(id);
        Query query = Query.query(criteria);

        try {
            Accommodation accommodation = template.findOne(query, Accommodation.class, "listings");
            return Optional.ofNullable(accommodation);
        } catch (Exception e) {
            throw new RuntimeException("Error finding accommodation with ID: " + id, e);
        }
    }

	// find all suburbs
      /* db.listings.aggregate([
        { $match: { "address.suburb": { $exists: true, $ne: "" } } },
        { $group: { _id: "$address.suburb" } },
        { $sort: { _id: 1 } }
      ]) */
    
	public List<String> findAllSuburbs() {
        try {
            TypedAggregation<Document> aggregation = Aggregation.newAggregation(Document.class,
                Aggregation.match(
                    Criteria.where("address.suburb").exists(true).ne("").ne(null)
                ),
                Aggregation.group("address.suburb"),
                Aggregation.match(
                    Criteria.where("_id").ne("").ne(null)
                ),
                Aggregation.sort(Sort.Direction.ASC, "_id")
            );

            AggregationResults<Document> results = template.aggregate(
                aggregation, 
                "listings",
                Document.class
            );

            if (results == null) {
                return Collections.emptyList();
            }

			// non lambda equivalent
			/* List<String> ids = new ArrayList<>();
			for (Document doc : results.getMappedResults()){
				String id = doc.get("_id", String.class);
				if (id != null){
					ids.add(id);
				}
			}
			return ids; */

            return results.getMappedResults().stream()
                .map(doc -> doc.get("_id", String.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch suburbs from database", e);
        }
    }


      /* db.listings.aggregate([
        { $match: {
            "address.suburb": { $regex: "^suburb$", $options: "i" },
            "price": { $lte: priceRange },
            "accommodates": { $gte: persons },
            "min_nights": { $gte: duration }
        }},
        { $project: {
            "_id": 1,
            "name": 1,
            "accommodates": 1,
            "price": 1
        }},
        { $sort: { "price": -1 }}
      ]) */
    
    public List<AccommodationSummary> searchAccommodations(String suburb, float priceRange, 
            int persons, int duration) {
        validateSearchParams(suburb, priceRange, persons, duration);

        try {
            TypedAggregation<Document> aggregation = Aggregation.newAggregation(Document.class,
                Aggregation.match(Criteria.where("address.suburb")
                    .regex("^" + suburb + "$", "i")
                    .and("price").lte(priceRange)
                    .and("accommodates").gte(persons)
                    .and("min_nights").gte(duration)),
                Aggregation.project("_id", "name", "accommodates", "price"),
                Aggregation.sort(Sort.Direction.DESC, "price")
            );

            AggregationResults<Document> results = template.aggregate(
                aggregation,
                "listings",
                Document.class
            );

			// non lambda equivalent
			/* List<AccommodationSummary> summaries = new ArrayList<>();
			for (Document doc : results.getMappedResults()){
				AccommodationSummary summary = convertToAccommodationSummary(doc);
				if (summary != null){
					summaries.add(summary);
				}
			} return summaries; */

            return results.getMappedResults().stream()
                .map(this::convertToAccommodationSummary)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to search accommodations", e);
        }
    }

    private void validateSearchParams(String suburb, float priceRange, int persons, int duration) {
        if (suburb == null || suburb.trim().isEmpty()) {
            throw new IllegalArgumentException("Suburb cannot be null or empty");
        }
        if (priceRange <= 0) {
            throw new IllegalArgumentException("Price range must be greater than 0");
        }
        if (persons <= 0) {
            throw new IllegalArgumentException("Number of persons must be greater than 0");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
    }

    private AccommodationSummary convertToAccommodationSummary(Document doc) {
        try {
            AccommodationSummary summary = new AccommodationSummary();
            summary.setId(doc.getString("_id"));
            summary.setName(doc.getString("name"));
            summary.setAccomodates(doc.getInteger("accommodates"));
            summary.setPrice(doc.get("price", Number.class).floatValue());
            return summary;
        } catch (Exception e) {
            return null;
        }
    }

      /* db.listings.find(
        { "_id": id },
        {
          "name": 1,
          "images.picture_url": 1,
          "amenities": 1,
          "min_nights": 1,
          "max_nights": 1,
          "price": 1,
          "address.suburb": 1,
          "address.country": 1
        }
      ) */
    
    public Optional<Accommodation> findAccommodationById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        try {
            Query query = new Query(Criteria.where("_id").is(id));
            query.fields()
                .include("name")
                .include("summary")
                .include("images.picture_url")
                .include("amenities")
                .include("min_nights")
                .include("max_nights")
                .include("price")
                .include("address.suburb")
                .include("address.country")
                .include("address.street");

            Document doc = template.findOne(query, Document.class, "listings");
            
            if (doc == null) {
                return Optional.empty();
            }

            Accommodation accommodation = new Accommodation();
            accommodation.setId(doc.getString("_id"));
            accommodation.setName(doc.get("name", "")); // Default empty string if null
            accommodation.setSummary(doc.get("summary", "")); // Default empty string if null

            Document images = (Document) doc.get("images");
            accommodation.setImage(images != null ? images.get("picture_url", "") : "");

            List<String> amenities = doc.getList("amenities", String.class);
            accommodation.setAmenities(amenities != null ? amenities : new ArrayList<>());

            accommodation.setMinNights(doc.getInteger("min_nights", 1)); // Default 1 if null
            accommodation.setMaxNights(doc.getInteger("max_nights", 1)); // Default 1 if null
            
            Number price = doc.get("price", Number.class);
            accommodation.setPrice(price != null ? price.floatValue() : 0.0f);

            Document address = (Document) doc.get("address");
            if (address != null) {
                accommodation.setStreet(address.get("street", ""));
                accommodation.setSuburb(address.get("suburb", ""));
                accommodation.setCountry(address.get("country", ""));
            } else {
                accommodation.setStreet("");
                accommodation.setSuburb("");
                accommodation.setCountry("");
            }

            return Optional.of(accommodation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch accommodation: " + id, e);
        }
    }
}
