package local.business.discovery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BusinessAdapter businessAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstLaunch()) {
            startIntroActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.businessRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        businessAdapter = new BusinessAdapter(new ArrayList<>());
        recyclerView.setAdapter(businessAdapter);

        // Load businesses dynamically
        loadBusinesses();
    }

    private void loadBusinesses() {
        // Assuming user credentials are saved in SharedPreferences after successful login
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString(getString(R.string.key_email), "");

        // Replace this with your logic to load businesses for the logged-in user
        // For testing, you can still add hardcoded businesses
        List<Business> businesses = new ArrayList<>();
        businesses.add(Business.newBuilder("Local Cafe", "Coffee & Pastries", "123 Main St", 37.7749, -122.4194)
                .addReview(new Review("User1", 4.5, "Great place"))
                .addReview(new Review("User2", 5.0, "Excellent service"))
                .build());

        // Add more businesses as needed

        // Update the adapter with the new list of businesses
        businessAdapter.updateData(businesses);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                businessAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}
