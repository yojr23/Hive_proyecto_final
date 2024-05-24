package com.vicenterincon.hive_proyectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicenterincon.hive_proyectofinal.R;
import com.vicenterincon.hive_proyectofinal.adapters.EventsAdapter;
import com.vicenterincon.hive_proyectofinal.model.Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HomePageFragment extends Fragment {

    private WeakReference<EventsAdapter> eventsAdapter = null;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page_fragment, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        Spinner spinnerFilterCategory = view.findViewById(R.id.spinnerFilterCategory);
        String[] categories = getResources().getStringArray(R.array.filterEventsByCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCategory.setAdapter(adapter);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        eventsAdapter = new WeakReference<>(new EventsAdapter(this, requireContext()));
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

        // Query Firestore for events from today onwards
        CollectionReference eventsCollection = db.collection("events");
        Query query = eventsCollection.whereGreaterThanOrEqualTo("date", today);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                swipeRefreshLayout.setEnabled(true);
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    events.add(event);
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (eventsAdapter.get() != null) {
                    eventsAdapter.get().submitList(events);
                }
                swipeRefreshLayout.setOnRefreshListener(() -> {
                    refreshFragment(swipeRefreshLayout);
                });
                spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filterEventsByCategory(categories[position], events, view, eventsAdapter.get());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        filterEventsByCategory("Todos", events, view, eventsAdapter.get());
                    }
                });
            } else {
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void refreshFragment(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setRefreshing(true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        CollectionReference eventsCollection = db.collection("events");
        Query query = eventsCollection.whereGreaterThanOrEqualTo("date", today);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    events.add(event);
                }
                if (eventsAdapter.get() != null) {
                    eventsAdapter.get().submitList(events);
                }
                swipeRefreshLayout.setRefreshing(false);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void filterEventsByCategory(@NonNull String category, @NonNull List<Event> listEvents, View view, EventsAdapter eventsAdapter) {
        String categoryChecked = category;

        switch (category) {
            case "Académico":
                categoryChecked = "ACADEMIC";
                break;
            case "Deportivo":
                categoryChecked = "SPORTS";
                break;
            case "Cultural":
                categoryChecked = "CULTURAL";
                break;
            case "Entretenimiento":
                categoryChecked = "ENTERTAINMENT";
                break;
            case "Otros":
                categoryChecked = "OTHER";
                break;
        }

        // Filter the list based on the selected category
        List<Event> filteredList;
        if ("Todos".equals(categoryChecked)) {
            filteredList = listEvents;
        } else {
            String finalCategoryChecked = categoryChecked;
            filteredList = listEvents.stream()
                    .filter(event -> finalCategoryChecked.equals(event.getCategory()))
                    .collect(Collectors.toList());
        }

        TextView noEventsTextView = view.findViewById(R.id.noEventsTextView);

        if (filteredList.isEmpty()) {
            eventsAdapter.submitList(filteredList);
            // If there are no events in that category, show a message
            if (noEventsTextView != null) {
                noEventsTextView.setVisibility(View.VISIBLE);
                noEventsTextView.setText(view.getContext().getString(R.string.no_events_category));
            }
        } else {
            if (noEventsTextView != null) {
                noEventsTextView.setVisibility(View.GONE);
            }
            eventsAdapter.submitList(filteredList);
        }
    }

}