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
        // Initialisation de l'activité
        super.onCreate(savedInstanceState);

        // Utilisation du View Binding pour lier le layout de l'activité
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lancement de la demande de permission (POST_NOTIFICATIONS)
        // permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

        // Initialisation de la barre de navigation inférieure
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configuration du NavController pour la navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    // Mise en place du gestionnaire de résultats pour la demande de permission
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: "+isGranted);
                    // Gestion du résultat de la demande de permission
                    if (!isGranted) {
                        Log.d(TAG, "onActivityResult: Permission denied");
                        Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Gestion du bouton "Retour" de la barre d'action
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
