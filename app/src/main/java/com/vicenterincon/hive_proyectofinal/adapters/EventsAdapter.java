package com.vicenterincon.hive_proyectofinal.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicenterincon.hive_proyectofinal.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vicenterincon.hive_proyectofinal.model.Event;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MListHolder> {

    private final LifecycleOwner lifecycleOwner;
    private final Context context;
    private FirebaseFirestore db;

    public EventsAdapter(LifecycleOwner lifecycleOwner, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
    }

    public class MListHolder extends RecyclerView.ViewHolder {
        public MListHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private final DiffUtil.ItemCallback<Event> differCallback = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final AsyncListDiffer<Event> differ = new AsyncListDiffer<>(this, differCallback);

    @NonNull
    @Override
    public MListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull MListHolder holder, int position) {
        Event event = differ.getCurrentList().get(position);
        CardView cardView = holder.itemView.findViewById(R.id.eventCardView);
        TextView creador = holder.itemView.findViewById(R.id.creador);
        TextView nombre = holder.itemView.findViewById(R.id.nombre);
        TextView description = holder.itemView.findViewById(R.id.descripcion);
        TextView fecha = holder.itemView.findViewById(R.id.fecha);

        // Change date format to "dd/mm/yyyy"
        String[] date = event.getDate().toString().split(" ");
        String newDate = date[2] + "/" + date[1] + "/" + date[0];

        DocumentReference creator = event.getCreator();
        creator.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    creador.setText(user.getName());
                } else {
                    Log.d(TAG, "No such user document");
                }
            } else {
                Log.d(TAG, "Failed with: ", task.getException());
            }
        });

        nombre.setText(event.getName());
        description.setText(event.getDescription());
        nombre.setText(event.getName());
        description.setText(event.getDescription());
        fecha.setText(newDate);
        Dialog detailDialog = new Dialog(holder.itemView.getContext());

        // Add a click listener to the card view
        cardView.setOnClickListener(v -> {
            detailDialog.setContentView(R.layout.fragment_event_detail);

            LinearLayout dialogLinearLayout = detailDialog.findViewById(R.id.layoutCard);
            ProgressBar dialogProgressBar = detailDialog.findViewById(R.id.loadingProgressBar);

            dialogLinearLayout.setVisibility(View.GONE);
            dialogProgressBar.setVisibility(View.VISIBLE);

            TextView eventNameTextView = detailDialog.findViewById(R.id.title);
            TextView eventEstadoTextView = detailDialog.findViewById(R.id.estado);
            if (event.isState()) {
                eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_activo));
            } else {
                eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_inactivo));
            }

            TextView eventIDTextView = detailDialog.findViewById(R.id.eventID);
            TextView eventCategoryTextView = detailDialog.findViewById(R.id.categoria);
            TextView eventCreatorTextView = detailDialog.findViewById(R.id.creador);
            TextView eventDateTextView = detailDialog.findViewById(R.id.fecha);
            TextView eventDuracionTextView = detailDialog.findViewById(R.id.duracion);
            eventDuracionTextView.setText(event.getDuration() + " " + holder.itemView.getContext().getString(R.string.event_duration_minutos));

            TextView eventDescriptionTextView = detailDialog.findViewById(R.id.descripcion);
            TextView eventLugarTextView = detailDialog.findViewById(R.id.lugar);
            TextView eventParticipantTextView = detailDialog.findViewById(R.id.personas);
            ImageView eventQRImageView = detailDialog.findViewById(R.id.qr);
            Button joinEventButton = detailDialog.findViewById(R.id.submitButton);
            ImageView eventImageView = detailDialog.findViewById(R.id.imagen);

            // When loading
            dialogLinearLayout.setVisibility(View.GONE);
            dialogProgressBar.setVisibility(View.VISIBLE);

            // When Success
            // Hide progress bar
            // Show all components except progress bar
            String eventId = event.getId();

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
                                        eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_activo));
                                    } else {
                                        eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_inactivo));
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

                                    eventDateTextView.setText(newDate);
                                    eventDuracionTextView.setText(event1.getDuration() + " " + holder.itemView.getContext().getString(R.string.event_duration_minutos));
                                    eventDescriptionTextView.setText(event1.getDescription());
                                    eventLugarTextView.setText(event1.getPlace());
                                    Bitmap qrCodeBitmap = generateQRCode(eventId, 300, 300);

                                    if (qrCodeBitmap != null) {
                                        eventQRImageView.setImageBitmap(qrCodeBitmap);
                                    }

                                    try {
                                        String stringParticipant = event1.getParticipants().size() + " / " + event1.getNumParticipants() + " " + holder.itemView.getContext().getString(R.string.event_participants_personas);
                                        eventParticipantTextView.setText(stringParticipant);
                                    } catch (Exception e) {
                                        eventParticipantTextView.setText("0 / " + event1.getNumParticipants() + " " + holder.itemView.getContext().getString(R.string.event_participants_personas));
                                    }

                                    // Uncomment and use this part if needed
                                    // String eventId = event.getId();
                                    // Bitmap qrCodeBitmap = generateQRCode(eventId, 300, 300);
                                    // if (qrCodeBitmap != null) {
                                    //     eventQRImageView.setImageBitmap(qrCodeBitmap);
                                    // }
                                    // String url = event.getImage();
                                    // eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day);
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
                } else {
                    Log.d("TAG", "Query failed with ", task.getException());
                }
            });

/*            if (url != null && !url.isEmpty()) {
                // In case it is not a loadable image just leave the eventImageView empty using a try catch
                try {
                    Picasso.get().load(url).into(eventImageView);
                } catch (Exception e) {
                    eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day);
                }
            }*/

            /*joinEventButton.setOnClickListener(view -> {
                String eventID = eventIDTextView.getText().toString();
                String userID = userSession.getUserId();
                if (userID != null && joinEventButton.getText().toString().equals(holder.itemView.getContext().getString(R.string.event_detail_unirse))) {
                    viewModelAddParticipant.addParticipatEventVM(eventID, userID);
                    viewModelAddParticipant.getAddParticipatEvent().observe(lifecycleOwner, resource1 -> {
                        if (resource1 instanceof Resource.Success) {
                            if (!eventDetailResponse.getParticipants().contains(userID)) {
                                eventDetailResponse.getParticipants().add(userID);
                            }

                            // Change joinEventButton text to "salir"
                            joinEventButton.setText(holder.itemView.getContext().getString(R.string.event_detail_no_asistir));

                            // Update the number of participants
                            String stringParticipant1 = eventDetailResponse.getParticipants().size() + " / " + eventDetailResponse.getNumParticipants() + " " + holder.itemView.getContext().getString(R.string.event_participants_personas);
                            eventParticipantTextView.setText(stringParticipant1);
                        }
                    });
                }
                if (userID != null && joinEventButton.getText().toString().equals(holder.itemView.getContext().getString(R.string.event_detail_no_asistir))) {
                    viewModelAddParticipant.deleteParticipatEventVM(eventID, userID);
                    // Remove the ID from the event.participants
                    viewModelAddParticipant.getDeleteParticipatEvent().observe(lifecycleOwner, resource12 -> {
                        if (resource12 instanceof Resource.Success) {
                            if (eventDetailResponse.getParticipants().contains(userID)) {
                                eventDetailResponse.getParticipants().remove(userID);
                            }
                            // Change joinEventButton text to "unirse"
                            joinEventButton.setText(holder.itemView.getContext().getString(R.string.event_detail_unirse));

                            // Update the number of participants
                            String stringParticipant1 = eventDetailResponse.getParticipants().size() + " / " + eventDetailResponse.getNumParticipants() + " " + holder.itemView.getContext().getString(R.string.event_participants_personas);
                            eventParticipantTextView.setText(stringParticipant1);
                            }
                        });
                    }
                });
            });*/
            // Show the detail dialog
            detailDialog.show();
        });
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

    public void submitList(List<Event> list) {
        differ.submitList(list);
    }
}

