package local.business.discovery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Business implements Parcelable {

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

    // Parcelable implementation
    protected Business(Parcel in) {
        name = in.readString();
        category = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        reviews = new ArrayList<>();
        in.readList(reviews, Review.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };
}
