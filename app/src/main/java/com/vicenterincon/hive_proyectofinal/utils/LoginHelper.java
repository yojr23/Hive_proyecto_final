package com.vicenterincon.hive_proyectofinal.utils;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

public class LoginHelper {
    private static final String TAG = "login_helper";
    private FirebaseFirestore db;

    public LoginHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface LoginCallback {
        void onLoginSuccess(UserSession userSession);
        void onLoginFailure(String message);
    }

    public void login(String username, String password, final LoginCallback callback) {
        db.collection("users")
                .whereEqualTo("login", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String storedPassword = document.getString("password");

                                if (storedPassword != null && storedPassword.equals(password)) {
                                    String authToken = "";
                                    String userId = document.getString("id");

                                    UserSession userSession = new UserSession(authToken, userId);
                                    callback.onLoginSuccess(userSession);
                                } else {
                                    callback.onLoginFailure("Incorrect password");
                                }
                            } else {
                                callback.onLoginFailure("User not found");
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            callback.onLoginFailure("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }
}
