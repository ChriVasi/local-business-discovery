package local.business.discovery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Sample business data
        Business[] businesses = {
                new Business("Local Cafe", "Coffee & Pastries", "123 Main St", 37.7749, -122.4194),
                new Business("Artisanal Bakery", "Bakery", "456 Oak St", 37.7831, -122.4039),
                new Business("Bookstore", "Books & Magazines", "789 Maple St", 37.7914, -122.3915)
                // Add more businesses as needed
        };

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.businessRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        businessAdapter = new BusinessAdapter(businesses);
        recyclerView.setAdapter(businessAdapter);
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

        return true;
    }
}
