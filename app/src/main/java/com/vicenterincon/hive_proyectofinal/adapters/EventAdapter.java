package com.vicenterincon.hive_proyectofinal.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.vicenterincon.hive_proyectofinal.R;
import java.util.HashMap;
import com.vicenterincon.hive_proyectofinal.model.Event;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MListHolder> {

    private final LifecycleOwner lifecycleOwner;
    private final Context context;

    public EventsAdapter(LifecycleOwner lifecycleOwner, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
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

        // Change date format to "dd/mm/yyyy"
        String[] date = event.getDate().toString();
        String newDate = date[2] + "/" + date[1] + "/" + date[0];

        holder.itemView.findViewById(R.id.creador).setText(event.getCreator());
        holder.itemView.findViewById(R.id.nombre).setText(event.getName());
        holder.itemView.findViewById(R.id.descripcion).setText(event.getDescription());
        holder.itemView.findViewById(R.id.fecha).setText(newDate);

        connectionLiveData = new ConnectionLiveData(context);
        Dialog detailDialog = new Dialog(holder.itemView.getContext());
        detailDialog.setContentView(R.layout.fragment_no_internet_connection);

        // Add a click listener to the card view
        cardView.setOnClickListener(v -> {
            connectionLiveData.observe(lifecycleOwner, isConnected -> {
                if (isConnected) {
                    // If the user is connected to the internet, show the event detail
                    // Track a custom event when a list item is clicked
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "item_" + event.getId()); // Use a unique item ID
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "List Item " + event.getName()); // Item name
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CardView Item " + event.getName());

                    FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    eventDetailViewModel = new EventDetailViewModel(event.getId(), context);

                    eventDetailViewModel.getEventByIdVM(event.getId());

                    detailDialog.setContentView(R.layout.fragment_event_detail);

                    LinearLayout dialogLinearLayout = detailDialog.findViewById(R.id.layoutCard);
                    ProgressBar dialogProgressBar = detailDialog.findViewById(R.id.loadingProgressBar);

                    dialogLinearLayout.setVisibility(View.GONE);
                    dialogProgressBar.setVisibility(View.VISIBLE);

                    TextView eventNameTextView = detailDialog.findViewById(R.id.title);
                    TextView eventEstadoTextView = detailDialog.findViewById(R.id.estado);
                    if (event.getState()) {
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
                    TextView eventLinksInteresesTextView = detailDialog.findViewById(R.id.linksInteres);
                    ImageView eventImageView = detailDialog.findViewById(R.id.imagen);

                    eventDetailViewModel.getEventById().observe(lifecycleOwner, resource -> {
                        if (resource instanceof Resource.Loading) {
                            // Show progress bar
                            // Hide all components except progress bar
                            dialogLinearLayout.setVisibility(View.GONE);
                            dialogProgressBar.setVisibility(View.VISIBLE);
                        } else if (resource instanceof Resource.Success) {
                            // Hide progress bar
                            // Show all components except progress bar
                            dialogProgressBar.setVisibility(View.GONE);
                            dialogLinearLayout.setVisibility(View.VISIBLE);

                            EventDetailResponse eventDetailResponse = (EventDetailResponse) resource.getData();

                            eventNameTextView.setText(eventDetailResponse.getName());

                            if (eventDetailResponse.getState()) {
                                eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_activo));
                            } else {
                                eventEstadoTextView.setText(holder.itemView.getContext().getString(R.string.event_state_inactivo));
                            }

                            eventIDTextView.setText(eventDetailResponse.getId());

                            eventCategoryTextView.setText(eventDetailResponse.getCategory());

                            eventCreatorTextView.setText(eventDetailResponse.getCreator());

                            eventDateTextView.setText(newDate);

                            eventDuracionTextView.setText(eventDetailResponse.getDuration() + " " + holder.itemView.getContext().getString(R.string.event_duration_minutos));

                            eventDescriptionTextView.setText(eventDetailResponse.getDescription());

                            eventLugarTextView.setText(eventDetailResponse.getPlace());

                            String stringParticipant = eventDetailResponse.getParticipants().size() + " / " + eventDetailResponse.getNumParticipants() + " " + holder.itemView.getContext().getString(R.string.event_participants_personas);
                            eventParticipantTextView.setText(stringParticipant);

                            String eventId = eventDetailResponse.getId();
                            Bitmap qrCodeBitmap = generateQRCode(eventId, 300, 300);

                            if (qrCodeBitmap != null) {
                                eventQRImageView.setImageBitmap(qrCodeBitmap);
                            }

                            UserSession userSession = sessionManager.getUserSession();

                            // Check if userSession.id is in event.participants
                            if (eventDetailResponse.getParticipants().contains(userSession.getUserId())) {
                                // If userSession.id is in event.participants, change joinEventButton text to "salir"
                                joinEventButton.setText(holder.itemView.getContext().getString(R.string.event_detail_no_asistir));
                            }

                            if (!eventDetailResponse.getLinks().isEmpty()) {
                                // If there are links, set the text to the links
                                eventLinksInteresesTextView.setText(String.join("\n", eventDetailResponse.getLinks()));
                            } else {
                                // If there are no links, set an empty text
                                eventLinksInteresesTextView.setText("");
                            }

                            String url = eventDetailResponse.getImage();

                            if (url != null && !url.isEmpty()) {
                                // In case it is not a loadable image just leave the eventImageView empty using a try catch
                                try {
                                    Picasso.get().load(url).into(eventImageView);
                                } catch (Exception e) {
                                    eventImageView.setImageResource(R.drawable.ic_baseline_calendar_day);
                                }
                            }

                            joinEventButton.setOnClickListener(view -> {
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
                        } else if (resource instanceof Resource.Error) {
                            // Handle error state (e.g., show an error message)
                            detailDialog.setContentView(R.layout.fragment_no_internet_connection);
                        }
                    });
                } else {
                    detailDialog.setContentView(R.layout.fragment_no_internet_connection);
                }
            });
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

    public void submitList(List<EventResponse> list) {
        differ.submitList(list);
    }
}

