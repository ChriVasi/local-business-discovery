package local.business.discovery;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

        private final String userName;
        private final double rating;
        private final String comment;

        public Review(String userName, double rating, String comment) {
                this.userName = userName;
                this.rating = rating;
                this.comment = comment;
        }

        public String getUserName() {
                return userName;
        }

        public double getRating() {
                return rating;
        }

        public String getComment() {
                return comment;
        }

        // Parcelable implementation
        protected Review(Parcel in) {
                userName = in.readString();
                rating = in.readDouble();
                comment = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(userName);
                dest.writeDouble(rating);
                dest.writeString(comment);
        }

        @Override
        public int describeContents() {
                return 0;
        }

        public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
                @Override
                public Review createFromParcel(Parcel in) {
                        return new Review(in);
                }

                @Override
                public Review[] newArray(int size) {
                        return new Review[size];
                }
        };
}
