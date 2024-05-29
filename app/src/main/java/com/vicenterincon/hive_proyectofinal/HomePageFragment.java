package com.vicenterincon.hive_proyectofinal;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vicenterincon.hive_proyectofinal.adapters.EventsAdapter;
import com.vicenterincon.hive_proyectofinal.model.Event;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.zxing.integration.android.IntentIntegrator;
import com.vicenterincon.hive_proyectofinal.model.User;

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

        AppCompatImageButton buttonScanQR = view.findViewById(R.id.scanQRButton);
        buttonScanQR.setOnClickListener(v -> {
            initScanner();
        });

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

    private void initScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt(requireContext().getString(R.string.event_promotion));
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Dialog detailDialog = new Dialog(requireContext());
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.evento_cancelado), Toast.LENGTH_LONG).show();
            } else {
                detailDialog.setContentView(R.layout.fragment_event_detail_less);
                String eventId = String.valueOf(result.getContents());
                LinearLayout dialogLinearLayout = detailDialog.findViewById(R.id.layoutCard);
                ProgressBar dialogProgressBar = detailDialog.findViewById(R.id.loadingProgressBar);

                dialogLinearLayout.setVisibility(View.GONE);
                dialogProgressBar.setVisibility(View.VISIBLE);

                TextView eventNameTextView = detailDialog.findViewById(R.id.title);
                TextView eventEstadoTextView = detailDialog.findViewById(R.id.estado);
                TextView eventIDTextView = detailDialog.findViewById(R.id.eventID);
                TextView eventCategoryTextView = detailDialog.findViewById(R.id.categoria);
                TextView eventCreatorTextView = detailDialog.findViewById(R.id.creador);
                TextView eventDateTextView = detailDialog.findViewById(R.id.fecha);
                TextView eventDuracionTextView = detailDialog.findViewById(R.id.duracion);
                TextView eventDescriptionTextView = detailDialog.findViewById(R.id.descripcion);
                TextView eventLugarTextView = detailDialog.findViewById(R.id.lugar);
                TextView eventParticipantTextView = detailDialog.findViewById(R.id.personas);
                ImageView eventQRImageView = detailDialog.findViewById(R.id.qr);
                Button joinEventButton = detailDialog.findViewById(R.id.submitButton);
                ImageView eventImageView = detailDialog.findViewById(R.id.imagen);

                // When loading
                dialogLinearLayout.setVisibility(View.GONE);
                dialogProgressBar.setVisibility(View.VISIBLE);

                // Reference to the events collection
                CollectionReference eventsRef = db.collection("events");

                // Create a query to search for the document with the matching id attribute
                Query query = eventsRef.whereEqualTo("id", eventId);

                // Execute the query
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            DocumentReference docRef = document.getReference();

                            // Now you can use docRef to get the document details
                            docRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document1 = task1.getResult();
                                    if (document1.exists()) {
                                        dialogProgressBar.setVisibility(View.GONE);
                                        dialogLinearLayout.setVisibility(View.VISIBLE);
                                        Event event1 = document1.toObject(Event.class);
                                        eventNameTextView.setText(event1.getName());

                                        if (event1.isState()) {
                                            eventEstadoTextView.setText(requireContext().getString(R.string.event_state_activo));
                                        } else {
                                            eventEstadoTextView.setText(requireContext().getString(R.string.event_state_inactivo));
                                        }

                                        eventIDTextView.setText(event1.getId());
                                        eventCategoryTextView.setText(event1.getCategory());

                                        DocumentReference creator1 = event1.getCreator();
                                        creator1.get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                DocumentSnapshot document2 = task2.getResult();
                                                if (document2.exists()) {
                                                    User user = document2.toObject(User.class);
                                                    eventCreatorTextView.setText(user.getName());
                                                } else {
                                                    Log.d(TAG, "No such user document");
                                                }
                                            } else {
                                                Log.d(TAG, "Failed with: ", task2.getException());
                                            }
                                        });
                                        String[] date = event1.getDate().toString().split(" ");
                                        String newDate = date[2] + "/" + date[1] + "/" + date[0];
                                        eventDateTextView.setText(newDate);
                                        eventDuracionTextView.setText(event1.getDuration() + " " + requireContext().getString(R.string.event_duration_minutos));
                                        eventDescriptionTextView.setText(event1.getDescription());
                                        eventLugarTextView.setText(event1.getPlace());
                                        Bitmap qrCodeBitmap = generateQRCode(eventId, 300, 300);

                                        if (qrCodeBitmap != null) {
                                            eventQRImageView.setImageBitmap(qrCodeBitmap);
                                        }

                                        try {
                                            String stringParticipant = event1.getParticipants().size() + " / " + event1.getNumParticipants() + " " + requireContext().getString(R.string.event_participants_personas);
                                            eventParticipantTextView.setText(stringParticipant);
                                        } catch (Exception e) {
                                            eventParticipantTextView.setText("0 / " + event1.getNumParticipants() + " " + requireContext().getString(R.string.event_participants_personas));
                                        }

                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task1.getException());
                                }
                            });
                        } else {
                            Log.d("TAG", "No document found with the given id attribute.");
                        }
                        detailDialog.show();
                    } else {
                        Log.d("TAG", "Query failed with ", task.getException());
                    }
                });

            }
        }
    }

    private Bitmap generateQRCode(String eventId, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

            QRCodeWriter writer = new QRCodeWriter();
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(eventId, BarcodeFormat.QR_CODE, width, height, hints);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}