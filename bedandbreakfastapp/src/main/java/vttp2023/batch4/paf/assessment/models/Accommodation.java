package vttp2023.batch4.paf.assessment.models;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

import java.util.LinkedList;

// IMPORTANT: DO NOT MODIFY THIS CLASS
// If this class is changed, any assessment task relying on this class will
// not be marked

public class Accommodation {
	private String id; // _id
	private String name; // name
	private String summary; // summary
	private int minNights; // minimum_nights
	private int maxNights; // maximum_nights
	private int accomodates; // accomates
	private float price; // price
	private String street; // address.street
	private String suburb; // address.suburb
	private String country; // address.country
	private List<String> amenities = new LinkedList<>(); // amenities
	private String image; // images.picture.url

	public void setId(String id) { this.id = id; }
	public String getId() { return this.id; }

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }

	public void setSummary(String summary) { this.summary = summary; }
	public String getSummary() { return this.summary; }

	public void setStreet(String street) { this.street = street; }
	public String getStreet() { return this.street; }

	public void setSuburb(String suburb) { this.suburb = suburb; }
	public String getSuburb() { return this.suburb; }

	public void setCountry(String country) { this.country = country; }
	public String getCountry() { return this.country; }

	public void setImage(String image) { this.image = image; }
	public String getImage() { return this.image; }

	public void setMinNights(int minNights) { this.minNights = minNights; }
	public int getMinNights() { return this.minNights; }

	public void setMaxNights(int maxNights) { this.maxNights = maxNights; }
	public int getMaxNights() { return this.maxNights; }

	public void setAccomodates(int accomodates) { this.accomodates = accomodates; }
	public int getAccomodates() { return this.accomodates; }

	public void setPrice(float price) { this.price = price; }
	public float getPrice() { return this.price; }

	public void setAmenities(List<String> amenities) { this.amenities = amenities; }
	public List<String> getAmenities() { return this.amenities; }

	@Override
	public String toString() {
		return "Accommodation [id=" + id + ", name=" + name + ", summary=" + summary + ", minNights=" + minNights
				+ ", maxNights=" + maxNights + ", accomodates=" + accomodates + ", price=" + price + ", street="
				+ street + ", suburb=" + suburb + ", country=" + country + ", amenities=" + amenities + ", image="
				+ image + "]";
	}
	public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("id", getId())
            .add("name", getName())
            .add("summary", getSummary())
            .add("minNights", getMinNights())
            .add("maxNights", getMaxNights())
            .add("accomodates", getAccomodates())
            .add("price", getPrice())
            .add("street", getStreet())
            .add("suburb", getSuburb())
            .add("country", getCountry())
            .add("image", getImage());

    JsonArrayBuilder amenitiesArrayBuilder = Json.createArrayBuilder();
    for (String amenity : getAmenities()) {
        amenitiesArrayBuilder.add(amenity);
    }
    builder.add("amenities", amenitiesArrayBuilder);

    return builder.build();
	}
	

	public static Accommodation fromJson(JsonObject jsonObject) {
    Accommodation accommodation = new Accommodation();
    accommodation.setId(jsonObject.getString("id"));
    accommodation.setName(jsonObject.getString("name"));
    accommodation.setSummary(jsonObject.getString("summary"));
    accommodation.setMinNights(jsonObject.getInt("minNights"));
    accommodation.setMaxNights(jsonObject.getInt("maxNights"));
    accommodation.setAccomodates(jsonObject.getInt("accomodates"));
    accommodation.setPrice((float) jsonObject.getJsonNumber("price").doubleValue());
    accommodation.setStreet(jsonObject.getString("street"));
    accommodation.setSuburb(jsonObject.getString("suburb"));
    accommodation.setCountry(jsonObject.getString("country"));
    accommodation.setImage(jsonObject.getString("image"));

    JsonArray amenitiesArray = jsonObject.getJsonArray("amenities");
    List<String> amenities = new LinkedList<>();
    for (JsonValue amenity : amenitiesArray) {
        amenities.add(amenity.toString());
    }
    accommodation.setAmenities(amenities);

    return accommodation;
}

}
