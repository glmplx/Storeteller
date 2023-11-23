package com.example.storeteller;

import android.Manifest;
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
        int test_bart_commit;
        super.onCreate(savedInstanceState);

        com.example.storeteller.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: "+isGranted);
                    if (!isGranted) {
                        Log.d(TAG, "onActivityResult: Permission denied");
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
