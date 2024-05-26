package com.vicenterincon.hive_proyectofinal.adapters;
import android.content.Context;
import android.content.SharedPreferences;
import com.vicenterincon.hive_proyectofinal.model.UserSession;

public class SessionManager {
    private static final String USER_SESSION_PREFS = "user_session";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SESSION_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(UserSession session) {
        editor.putString("auth_token", session.getAuthToken());
        editor.putString("user_id", session.getUserId());
        editor.apply();
    }

    public UserSession getUserSession() {
        String authToken = sharedPreferences.getString("auth_token", null);
        String userId = sharedPreferences.getString("user_id", null);
        return new UserSession(authToken, userId);
    }

    public void clearSession() {
        editor.clear().apply();
    }
}

