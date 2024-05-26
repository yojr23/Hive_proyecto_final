package com.vicenterincon.hive_proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

import java.text.SimpleDateFormat;

public class UserProfileFragment extends Fragment {
    private SessionManager sessionManager;
    private UserSession user;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBarProfile);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProfile);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);


        sessionManager = new SessionManager(requireContext());
        user = sessionManager.getUserSession();

        FirebaseApp.initializeApp(requireContext());
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(user.getUserId());
        loadingProgressBar.setVisibility(View.VISIBLE);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    swipeRefreshLayout.setEnabled(true);
                    loadingProgressBar.setVisibility(View.GONE);
                    User user = document.toObject(User.class);
                    TextView nameTextView = view.findViewById(R.id.userName);
                    TextView emailTextView = view.findViewById(R.id.email);
                    TextView role = view.findViewById(R.id.rolText);
                    TextView career = view.findViewById(R.id.career);
                    TextView birthday = view.findViewById(R.id.birthdate);

                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    role.setText(user.getRole());
                    career.setText(formatString(user.getCareer()));
                    SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
                    birthday.setText(formattedDate.format(user.getBirthdate()));
                    swipeRefreshLayout.setOnRefreshListener(() -> {
                        refreshFragment(swipeRefreshLayout, loadingProgressBar, view);
                    });
                }
                else {
                    Log.d("TAG", "No such document");
                    loadingProgressBar.setVisibility(View.GONE);
                }
            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonSignOut = view.findViewById(R.id.signOutButton);
        buttonSignOut.setOnClickListener(v -> {
            UserSession userSession = new UserSession("", "");
            sessionManager.saveUserSession(userSession);
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void refreshFragment(SwipeRefreshLayout swipeRefreshLayout, ProgressBar loadingProgressBar, View view) {
        swipeRefreshLayout.setRefreshing(true);
        DocumentReference docRef = db.collection("users").document(user.getUserId());
        loadingProgressBar.setVisibility(View.VISIBLE);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(true);
                    loadingProgressBar.setVisibility(View.GONE);
                    User user = document.toObject(User.class);
                    TextView nameTextView = view.findViewById(R.id.userName);
                    TextView emailTextView = view.findViewById(R.id.email);
                    TextView role = view.findViewById(R.id.rolText);
                    TextView career = view.findViewById(R.id.career);
                    TextView birthday = view.findViewById(R.id.birthdate);

                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    role.setText(user.getRole());
                    career.setText(formatString(user.getCareer()));
                    SimpleDateFormat formattedDate = new SimpleDateFormat("dd:MM:yyyy");
                    birthday.setText(formattedDate.format(user.getBirthdate()));
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("TAG", "No such document");
                    loadingProgressBar.setVisibility(View.GONE);
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    private String formatString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Replace underscores with spaces
        String modifiedString = input.replace("_", " ");

        // Convert the whole string to lower case
        modifiedString = modifiedString.toLowerCase();

        // Capitalize the first letter
        modifiedString = modifiedString.substring(0, 1).toUpperCase() + modifiedString.substring(1);

        return modifiedString;
    }
}
