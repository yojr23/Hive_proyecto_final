package com.vicenterincon.hive_proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.model.UserSession;
import com.vicenterincon.hive_proyectofinal.utils.LoginHelper;

public class LoginActivity extends AppCompatActivity {

    @Override
    //se llama al fragment login que es la pantalla que contiene el login
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        //se crea y se llama al SessionManager y al LoginHelper
        SessionManager session = new SessionManager(this);
        TextView loginButton = findViewById(R.id.buttonSignIn);
        LoginHelper loginHelper = new LoginHelper();

        loginButton.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

            //validacion del campo de texto de usuario
            if (username.isEmpty()) {
                ((EditText) findViewById(R.id.editTextEmail)).setError(getString(R.string.login_error_username));
                findViewById(R.id.editTextEmail).requestFocus();
                return;
            }

            //validacion del campo de texto de contraseña
            if (password.isEmpty()) {
                ((EditText) findViewById(R.id.editTextPassword)).setError(getString(R.string.login_error_password));
                findViewById(R.id.editTextPassword).requestFocus();
                return;
            }

           //se llama al login helper, se le pasa el user,password y callback que dira el estado final de la query
            loginHelper.login(username, password, new LoginHelper.LoginCallback() {
                @Override
                public void onLoginSuccess(UserSession userSession) {
                    // Navigate to main activity
                    session.saveUserSession(userSession);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onLoginFailure(String message) {
                    // Handle login failure
                    Log.d("Login", "Failure: " + message);
                    Toast.makeText(LoginActivity.this, "Revisa los datos ingresados", Toast.LENGTH_SHORT).show();
                }
            });

        });

        TextView signUpTextView = findViewById(R.id.textViewSignUpLink);
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
