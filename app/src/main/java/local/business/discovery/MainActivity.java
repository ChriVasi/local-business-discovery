package local.business.discovery;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.caverock.androidsvg.BuildConfig;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private BusinessAdapter businessAdapter;
    private MapView mapView;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1; // You can use any integer value
    // Declare the MyLocationNewOverlay as a class-level variable
    private MyLocationNewOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, prompt the user to enable it
            buildAlertMessageNoGps();
            return;
        }

// Check if internet connection is available
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            // Internet is not available, prompt the user to enable it
            buildAlertMessageNoInternet();
            return;
        }

// Initialize osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_main);

// Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Initialize MapView
        mapView = findViewById(R.id.mapView);

// Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // Permission already granted, initialize and enable MyLocationNewOverlay
            initializeMyLocationOverlay();
        }

        if (mapView != null) {
            // Continue with initialization and usage
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.getController().setZoom(5.0);
            mapView.setMultiTouchControls(true);

            // Initialize MyLocationNewOverlay
            initializeMyLocationOverlay();

            // Other operations involving mapView
            loadBusinesses();
        } else {
            // Handle the case where mapView is not found in the layout
            Log.e("MainActivity", "MapView not found in the layout");
        }

// Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.businessRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

// Initialize the adapter with an empty list
        businessAdapter = new BusinessAdapter(new ArrayList<>());
        recyclerView.setAdapter(businessAdapter);

// Load businesses dynamically
        loadBusinesses();
    }

        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize and enable MyLocationNewOverlay
                initializeMyLocationOverlay();
            } else {
                // Permission denied, handle accordingly
                Log.e("LocationDebug", "Location permission denied");
            }
        }
    }

    private void initializeMyLocationOverlay() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // Permission already granted, initialize and enable MyLocationNewOverlay
            myLocationOverlay = new MyLocationNewOverlay(mapView);
            mapView.getOverlays().add(myLocationOverlay);
            myLocationOverlay.enableMyLocation();

            // Zoom to your current location
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                mapView.getController().animateTo(myLocation);
                mapView.getController().setZoom(15.0); // Adjust the zoom level as needed
            }
        }
    }


    public void zoomToMyLocation(View view) {
        if (myLocationOverlay != null) {
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                mapView.getController().animateTo(myLocation);
                mapView.getController().setZoom(15.0); // Adjust the zoom level as needed
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your internet connection seems to be disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadBusinesses() {
        // Assuming user credentials are saved in SharedPreferences after a successful login
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString(getString(R.string.key_email), "");

        // Replace this with your logic to load businesses for the logged-in user
        // For testing, you can still add hardcoded businesses
        List<Business> businesses = new ArrayList<>();
        businesses.add(Business.newBuilder("Local Souvlaki", "Souvlaki & Pastries", "Agias Paraskevis 17", 39.37913, 22.95989)
                .addReview(new Review("User1", 4.5, "Great place"))
                .addReview(new Review("User2", 5.0, "Excellent service"))
                .build());

        // Add more businesses as needed

        // Check if businessAdapter is null, initialize it if needed
        if (businessAdapter == null) {
            // Initialize RecyclerView
            RecyclerView recyclerView = findViewById(R.id.businessRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize the adapter with an empty list
            businessAdapter = new BusinessAdapter(new ArrayList<>());
            recyclerView.setAdapter(businessAdapter);
        }

        // Update the adapter with the new list of businesses
        businessAdapter.updateData(businesses);

        // Display markers on the map
        displayMarkersOnMap(businesses);
    }



    private void displayMarkersOnMap(List<Business> businesses) {
        List<IGeoPoint> geoPoints = businesses.stream()
                .map(business -> new GeoPoint(business.getLatitude(), business.getLongitude()))
                .collect(Collectors.toList());

        BoundingBox boundingBox = BoundingBox.fromGeoPoints(geoPoints);


        for (Business business : businesses) {
            GeoPoint geoPoint = new GeoPoint(business.getLatitude(), business.getLongitude());
            CustomMarker marker = new CustomMarker(mapView);
            marker.setPosition(geoPoint);
            marker.setTitle(business.getName());
            mapView.getOverlays().add(marker);
        }

        mapView.invalidate(); // Refresh the map
    }

    private int calculateZoomLevel(BoundingBox boundingBox, int width, int height) {
        final int border = 20;
        double diffLat = boundingBox.getLatNorth() - boundingBox.getLatSouth();
        double diffLon = boundingBox.getLonEast() - boundingBox.getLonWest();

        double zoomLat = Math.log((height - 2 * border) / 256.0 / diffLat) / Math.log(2);
        double zoomLon = Math.log((width - 2 * border) / 256.0 / diffLon) / Math.log(2);

        return (int) Math.min(zoomLat, zoomLon);
    }

    private boolean isFirstLaunch() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.first_launch_key), true);
    }

    private void startIntroActivity() {
        startActivity(new Intent(this, IntroActivity.class));

        // Update the shared preferences to indicate that the intro has been shown
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(getString(R.string.first_launch_key), false);
        editor.apply();

        // Finish the main activity to prevent the user from going back to it
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the submission of the search query if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the businesses based on the search query
                List<Business> filteredBusinesses = filterBusinesses(businessAdapter.getData(), newText);

                // Update the adapter with the filtered businesses
                businessAdapter.updateData(filteredBusinesses);

                // Display the markers for filtered businesses on the map
                displayMarkersOnMap(filteredBusinesses);

                return false;
            }
        });
    }
    private List<Business> filterBusinesses(List<Business> businesses, String query) {
        String lowerCaseQuery = query.toLowerCase();

        return businesses.stream()
                .filter(business ->
                        business.getName().toLowerCase().contains(lowerCaseQuery) ||
                                business.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                                business.getAddress().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save your state variables here
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore your state variables here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release any resources here
    }


    // Custom Marker class for marker customization
    private static class CustomMarker extends Marker {
        public CustomMarker(MapView mapView) {
            super(mapView);
            // Customize the marker appearance here, e.g., setIcon, setAnchor, etc.
        }
    }
}
