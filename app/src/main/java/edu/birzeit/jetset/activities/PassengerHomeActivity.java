package edu.birzeit.jetset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import edu.birzeit.jetset.Fragments.AllFlightsFragment;
import edu.birzeit.jetset.Fragments.EditProfilePassengerFragment;
import edu.birzeit.jetset.Fragments.HomeFragment;
import edu.birzeit.jetset.Fragments.MyReservationsFragment;
import edu.birzeit.jetset.R;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;

public class PassengerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    public TextView toolbarTitle;
    SharedPrefManager sharedPrefManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_passenger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPrefManager = SharedPrefManager.getInstance(this);


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        toolbarTitle = findViewById(R.id.toolbar_title);

        TextView textViewName = navHeader.findViewById(R.id.textViewName);
        String userName = getUserNameFromSharedPreferences();
        textViewName.setText(userName);

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        String title = "Home";
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
            title = "Home";
        } else if (itemId == R.id.nav_my_reservations) {
            selectedFragment = new MyReservationsFragment();
            title = "My Reservations";
        } else if (itemId == R.id.nav_view_all_flights) {
            selectedFragment = new AllFlightsFragment();
            title = "All Flights";
        } else if (itemId == R.id.nav_edit_profile) {
            selectedFragment = new EditProfilePassengerFragment();
            title = "Edit Profile";
        } else if (itemId == R.id.nav_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
            sharedPrefManager.writeBoolean(IS_LOGGED_IN, false);
            sharedPrefManager.apply();
            Intent intent = new Intent(PassengerHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
            sharedPrefManager.writeToolbarTitle(title);
            sharedPrefManager.apply();
            toolbarTitle.setText(title);
        }
        navigationView.setCheckedItem(item.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getUserNameFromSharedPreferences() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        return sharedPrefManager.getUserName();
    }


}