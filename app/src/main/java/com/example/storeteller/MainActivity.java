package com.example.storeteller;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.storeteller.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PERMISSION_TAG";
    private final AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_settings, R.id.navigation_library, R.id.navigation_play)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity initialization
        super.onCreate(savedInstanceState);

        // Using View Binding to link the activity layout
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Launch permission request (POST_NOTIFICATIONS)
        // permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

        // Initializing the bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configuring the NavController for navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    // Setting up the Permission Request Results Manager
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: "+isGranted);
                    // Permission request result management
                    if (!isGranted) {
                        Log.d(TAG, "onActivityResult: Permission denied");
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Managing the "Back" action bar button
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
