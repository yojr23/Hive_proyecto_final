package com.vicenterincon.hive_proyectofinal;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vicenterincon.hive_proyectofinal.databinding.ActivityMainBinding;
import com.vicenterincon.hive_proyectofinal.model.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user
        User newUser = new User(
                "unique-id",
                "John Doe",
                "johndoe",
                "johndoe@example.com",
                "securepassword",
                "123 Main St",
                true,
                "STUDENT",
                "INGENIERIA_DE_SISTEMAS_Y_COMPUTACION",
                "2000-01-01"
        );

        // Add a new document with a generated ID
        db.collection("users")
                .add(newUser)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                });

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
}