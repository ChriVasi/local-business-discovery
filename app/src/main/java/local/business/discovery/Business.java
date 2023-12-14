package local.business.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Business {

    private final String name;
    private final String category;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final List<Review> reviews;

    private Business(Builder builder) {
        this.name = builder.name;
        this.category = builder.category;
        this.address = builder.address;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        // Use an unmodifiable list to make reviews immutable
        this.reviews = Collections.unmodifiableList(new ArrayList<>(builder.reviews));
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static Builder newBuilder(String name, String category, String address, double latitude, double longitude) {
        return new Builder(name, category, address, latitude, longitude);
    }

    public static class Builder {
        private final String name;
        private final String category;
        private final String address;
        private final double latitude;
        private final double longitude;
        private final List<Review> reviews;

        private Builder(String name, String category, String address, double latitude, double longitude) {
            this.name = name;
            this.category = category;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.reviews = new ArrayList<>();
        }

        public Builder addReview(Review review) {
            this.reviews.add(review);
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}
