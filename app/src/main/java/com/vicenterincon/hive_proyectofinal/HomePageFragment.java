package com.vicenterincon.hive_proyectofinal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.vicenterincon.hive_proyectofinal.R;
import com.vicenterincon.hive_proyectofinal.adapters.EventsAdapter;

public class HomePageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        eventsAdapter = new WeakReference<>(new EventsAdapter(viewModelAddParticipant, this, userSession, requireContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(eventsAdapter.get());

        EventListOfflineViewModelProviderFactory viewModelFactoryOffline = new EventListOfflineViewModelProviderFactory(requireContext());
        viewModelEventListOffline = new ViewModelProvider(this, viewModelFactoryOffline).get(EventListOfflineViewModel.class);

        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        viewModelEventListOffline.getAllEvents().observe(getViewLifecycleOwner(), resource -> {
            loadingProgressBar.setVisibility(View.GONE);
            List<EventResponse> list = new ArrayList<>();
            if (resource != null) {
                Calendar today = Calendar.getInstance();
                List<Event> filteredList = new ArrayList<>();
                for (Event event : resource) {
                    Calendar eventDate = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date;
                    try {
                        date = formatter.parse(event.getDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (date != null) {
                        eventDate.setTime(date);
                        if (eventDate.get(Calendar.DAY_OF_YEAR) >= today.get(Calendar.DAY_OF_YEAR)) {
                            filteredList.add(event);
                        }
                    }
                }

                for (Event event : filteredList) {
                    EventResponse eventToAdd = new EventResponse(
                            event.getId(),
                            event.getImage() != null ? event.getImage() : "",
                            event.getName() != null ? event.getName() : "",
                            event.getDescription() != null ? event.getDescription() : "",
                            event.getDate() != null ? event.getDate() : "",
                            event.getPlace() != null ? event.getPlace() : "",
                            event.getNumParticipants() != null ? event.getNumParticipants() : 0,
                            event.getCategory() != null ? event.getCategory() : "",
                            event.getState() != null ? event.getState() : false,
                            event.getDuration() != null ? event.getDuration() : 0,
                            event.getCreatorId() != null ? event.getCreatorId() : "",
                            event.getCreator() != null ? event.getCreator() : "",
                            event.getParticipants() != null ? event.getParticipants() : Collections.emptyList(),
                            event.getLinks() != null ? event.getLinks() : Collections.emptyList()
                    );
                    list.add(eventToAdd);
                }

                if (eventsAdapter.get() != null) {
                    eventsAdapter.get().submitList(list);
                }

                spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filterEventsByCategory(categories[position], list);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        filterEventsByCategory("Todos", list);
                    }
                });
            }
        });
        return view;
    }

}