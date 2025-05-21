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
        //se hace una query en firebase
            //se va hacia la carpeta usuarios
        db.collection("users")
                //se buca el username en firebase
                .whereEqualTo("login", username)
                //se obtiene la informacion de ese usuario con ese username
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //si es el resultado es diferente a vacio
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                //se saca la contraseña
                                String storedPassword = document.getString("password");

                                if (storedPassword != null && storedPassword.equals(password)) {
                                    //se guarda el authtoken
                                    String authToken = "";
                                    //se guarda el user id
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
