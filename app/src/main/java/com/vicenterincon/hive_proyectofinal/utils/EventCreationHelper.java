package com.vicenterincon.hive_proyectofinal.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicenterincon.hive_proyectofinal.model.Event;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

import java.util.UUID;

public class EventCreationHelper {
    private static final String TAG = "sign_up_helper";
    private FirebaseFirestore db;

    public EventCreationHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface CreateEventCallback {
        void onCreateEventSuccess();
        void onCreateEventFailure();
    }

    public void createEvent(final Event event, final UserSession userSession, final CreateEventCallback callback) {
        event.setId(UUID.randomUUID().toString());
        // Query to get the DocumentReference for the creator
        db.collection("users")
                .whereEqualTo("id", userSession.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming there's only one document with the given ID
                        QuerySnapshot querySnapshot = task.getResult();
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        DocumentReference creatorRef = document.getReference();
                        event.setCreator(creatorRef);

                        // Add the event to the Firestore collection
                        db.collection("events")
                                .add(event)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Event creation successful
                                        callback.onCreateEventSuccess();
                                    } else {
                                        // Log error and invoke failure callback
                                        Log.e(TAG, "Error adding document: ", task1.getException());
                                        callback.onCreateEventFailure();
                                    }
                                });
                    } else {
                        // Log error and invoke failure callback if user document is not found
                        Log.e(TAG, "Error finding user document: ", task.getException());
                        callback.onCreateEventFailure();
                    }
                });
    }
}

