package com.vicenterincon.hive_proyectofinal.utils;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

import java.util.UUID;

public class SignUpHelper {
    private static final String TAG = "sign_up_helper";
    private FirebaseFirestore db;

    public SignUpHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface SignUpCallback {
        void onSignUpSuccess(UserSession userSession);
        void onSignUpFailure(String message);
    }

    public void signUp(final User user, final SignUpCallback callback) {
        db.collection("users")
                .whereEqualTo("login", user.getLogin())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            // Username is unique, create the user
                            user.setId(UUID.randomUUID().toString());
                            db.collection("users")
                                    .add(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // User creation successful
                                            UserSession userSession = new UserSession("", user.getId());
                                            callback.onSignUpSuccess(userSession);
                                        } else {
                                            Log.e(TAG, "Error adding document: ", task1.getException());
                                            callback.onSignUpFailure("Error: " + task1.getException().getMessage());
                                        }
                                    });
                        } else {
                            // Username already exists
                            callback.onSignUpFailure("El nombre de usuario ingresado ya existe. Por favor, elija otro.");
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        callback.onSignUpFailure("Error: " + task.getException().getMessage());
                    }
                });
    }
}
