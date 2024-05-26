package com.vicenterincon.hive_proyectofinal;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.databinding.ActivityMainBinding;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        if (!isUserLoggedIn()) {
            navigateToLogin();
        }

        //Block the screen rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomePageFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.ic_home:
                    replaceFragment(new HomePageFragment());
                    return true;
                case R.id.ic_create:
                    replaceFragment(new EventCreationFragment());
                    return true;
                case R.id.ic_calendar:
                    replaceFragment(new CalendarFragment());
                    return true;
                case R.id.ic_profile:
                    replaceFragment(new UserProfileFragment());
                    return true;
                default:
                    return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void navigateToLogin() {
        // Redirect to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isUserLoggedIn() {
        // Check if the user is logged in using SessionManager
        UserSession userSession = sessionManager.getUserSession();
        return userSession.getAuthToken() != null && userSession.getUserId() != null;
    }
}