package com.vicenterincon.hive_proyectofinal;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vicenterincon.hive_proyectofinal.adapters.MyEventsAdapter;
import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.model.Event;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

public class CalendarFragment extends Fragment {

    private WeakReference<MyEventsAdapter> eventsAdapter = null;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        SessionManager sessionManager = new SessionManager(requireContext());
        String[] categories = getResources().getStringArray(R.array.filterEventsByCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        eventsAdapter = new WeakReference<>(new MyEventsAdapter(this, requireContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(eventsAdapter.get());

        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        FirebaseApp.initializeApp(this.requireContext());
        db = FirebaseFirestore.getInstance();

        // Get User DocumentReference
        CollectionReference usersCollection = db.collection("users");
        UserSession userSession = sessionManager.getUserSession();
        String userId = userSession.getUserId();

        // Query the user document by the user ID
        Query userQuery = usersCollection.whereEqualTo("id", userId);
        userQuery.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                DocumentSnapshot userDocument = userTask.getResult().getDocuments().get(0);
                DocumentReference userRef = userDocument.getReference();
                Log.d("Firestore", "User Reference: " + userRef.getPath());

                // Query Firestore for events where the current user is a participant, from today onwards
                CollectionReference eventsCollection = db.collection("events");
                Query eventQuery = eventsCollection
                        .whereGreaterThanOrEqualTo("date", today)
                        .whereArrayContains("participants", userRef);

                eventQuery.get().addOnCompleteListener(eventTask -> {
                    if (eventTask.isSuccessful()) {
                        swipeRefreshLayout.setEnabled(true);
                        List<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : eventTask.getResult()) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        loadingProgressBar.setVisibility(View.GONE);
                        if (eventsAdapter.get() != null) {
                            eventsAdapter.get().submitList(events);
                        }
                        swipeRefreshLayout.setOnRefreshListener(() -> refreshFragment(swipeRefreshLayout, loadingProgressBar));
                    } else {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                // Handle errors or case where user document is not found
                loadingProgressBar.setVisibility(View.GONE);
                Log.d("FirestoreError", "Failed to fetch user document or user not found");
            }
        });

        return view;
    }

    private void refreshFragment(SwipeRefreshLayout swipeRefreshLayout, ProgressBar loadingProgress) {
        swipeRefreshLayout.setRefreshing(true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        // Get User DocumentReference
        SessionManager sessionManager = new SessionManager(requireContext());
        CollectionReference usersCollection = db.collection("users");
        UserSession userSession = sessionManager.getUserSession();
        String userId = userSession.getUserId();

        // Query the user document by the user ID
        Query userQuery = usersCollection.whereEqualTo("id", userId);
        userQuery.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                DocumentSnapshot userDocument = userTask.getResult().getDocuments().get(0);
                DocumentReference userRef = userDocument.getReference();
                Log.d("Firestore", "User Reference: " + userRef.getPath());

                // Query Firestore for events where the current user is a participant, from today onwards
                CollectionReference eventsCollection = db.collection("events");
                Query eventQuery = eventsCollection
                        .whereGreaterThanOrEqualTo("date", today)
                        .whereArrayContains("participants", userRef);

                eventQuery.get().addOnCompleteListener(eventTask -> {
                    if (eventTask.isSuccessful()) {
                        swipeRefreshLayout.setEnabled(true);
                        List<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : eventTask.getResult()) {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        loadingProgress.setVisibility(View.GONE);
                        if (eventsAdapter.get() != null) {
                            eventsAdapter.get().submitList(events);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        loadingProgress.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                // Handle errors or case where user document is not found
                loadingProgress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.d("FirestoreError", "Failed to fetch user document or user not found");
            }
        });
    }
}