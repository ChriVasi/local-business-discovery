package local.business.discovery;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class DetailsActivity extends AppCompatActivity {

    private Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize osmdroid configuration
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences(getPackageName(), MODE_PRIVATE));

        // Retrieve business details from intent
        business = getIntent().getParcelableExtra("business");

        // Update UI with business details
        TextView nameTextView = findViewById(R.id.businessNameTextView);
        TextView categoryTextView = findViewById(R.id.businessCategoryTextView);

        nameTextView.setText(business.getName());
        categoryTextView.setText(business.getCategory());

        // Display reviews in a TextView or RecyclerView
        displayReviews(business);

        // Obtain the MapView and configure it
        MapView mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Set the map's center and zoom level based on the business location
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(new org.osmdroid.util.GeoPoint(business.getLatitude(), business.getLongitude()));
    }

    private void displayReviews(Business business) {
        // Assuming you have a TextView to display reviews
        TextView textViewReviews = findViewById(R.id.textViewReviews);

        if (business.getReviews() != null && !business.getReviews().isEmpty()) {
            // Iterate through the reviews and display them in the TextView
            for (Review review : business.getReviews()) {
                String reviewText = "User: " + review.getUserName() + ", Rating: " + review.getRating() + "\nComment: " + review.getComment() + "\n\n";
                textViewReviews.append(reviewText);
            }
        } else {
            // No reviews available for this business
            textViewReviews.setText("No reviews yet");
        }
    }
}
