package local.business.discovery;

public class Review {

        private final String userName;
        private final double rating;
        private final String comment;

        // Make the constructor public
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

        // Builder pattern for constructing reviews
        public static class ReviewBuilder {
                private final String userName;
                private final double rating;
                private String comment;

                public ReviewBuilder(String userName, double rating) {
                        this.userName = userName;
                        this.rating = rating;
                }

                public ReviewBuilder comment(String comment) {
                        this.comment = comment;
                        return this;
                }

                public Review build() {
                        return new Review(userName, rating, comment);
                }
        }
}
