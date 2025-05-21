package com.vicenterincon.hive_proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.vicenterincon.hive_proyectofinal.adapters.SessionManager;
import com.vicenterincon.hive_proyectofinal.model.User;
import com.vicenterincon.hive_proyectofinal.model.UserSession;
import com.vicenterincon.hive_proyectofinal.utils.LoginHelper;
import com.vicenterincon.hive_proyectofinal.utils.SignUpHelper;

import java.sql.Date;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //se llama al fragmento de registro que tiene el formulario
        setContentView(R.layout.fragment_sign_up);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        SignUpHelper signUpHelper = new SignUpHelper();
        SessionManager session = new SessionManager(this);


        //Inicio de getters de la info al hacer click en el boton

        buttonSignUp.setOnClickListener(v -> {
            String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
            String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
            String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
            String confirmPassword = ((EditText) findViewById(R.id.editTextVerifyPassword)).getText().toString();
            String selectedCareer = ((Spinner) findViewById(R.id.spinnerCareer)).getSelectedItem().toString();
            DatePicker birthdate = findViewById(R.id.datePickerBirthdate);


        //fin de getters de la info al hacer click en el boton


            int year = birthdate.getYear();
            int month = birthdate.getMonth() + 1;
            int dayOfMonth = birthdate.getDayOfMonth();
            String birthdateStr = String.format("%d-%02d-%02d", year, month, dayOfMonth);
            Date birthdateDate = Date.valueOf(birthdateStr);


            //Inicio de validaroes

            if (name.isEmpty()) {
                ((EditText) findViewById(R.id.editTextName)).setError(getString(R.string.sign_up_error_name));
                findViewById(R.id.editTextName).requestFocus();
                return;
            }
            if (username.isEmpty()) {
                ((EditText) findViewById(R.id.editTextUsername)).setError(getString(R.string.sign_up_error_user));
                findViewById(R.id.editTextUsername).requestFocus();
                return;
            }
            if (email.isEmpty()) {
                ((EditText) findViewById(R.id.editTextEmail)).setError(getString(R.string.sign_up_error_mail));
                findViewById(R.id.editTextEmail).requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ((EditText) findViewById(R.id.editTextEmail)).setError(getString(R.string.sign_up_error_mail_invalid));
                findViewById(R.id.editTextEmail).requestFocus();
                return;
            }
            if (password.isEmpty()) {
                ((EditText) findViewById(R.id.editTextPassword)).setError(getString(R.string.sign_up_error_password));
                findViewById(R.id.editTextPassword).requestFocus();
                return;
            }
            if (confirmPassword.isEmpty()) {
                ((EditText) findViewById(R.id.editTextVerifyPassword)).setError(getString(R.string.sign_up_error_password_confirm));
                findViewById(R.id.editTextVerifyPassword).requestFocus();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, getString(R.string.sign_up_error_password_match), Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.length() < 4) {
                ((EditText) findViewById(R.id.editTextUsername)).setError(getString(R.string.sign_up_error_user_length));
                findViewById(R.id.editTextUsername).requestFocus();
                return;
            }


            //fin de validadores


            //verificacion de que la contraseña tenga una mayuscula, un caracter especial y un nuemero
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%.*?&]{8,}$";
            if (!password.matches(regex)) {
                ((EditText) findViewById(R.id.editTextPassword)).setError(getString(R.string.sign_up_error_password_validation));
                findViewById(R.id.editTextPassword).requestFocus();
                return;
            }

            User userToAdd = new User("", name, username, email, password, "", true, "ADMIN", selectedCareer, birthdateDate);

            //se llama el signuphelper
            signUpHelper.signUp(userToAdd, new SignUpHelper.SignUpCallback() {
                @Override
                public void onSignUpSuccess(UserSession userSession) {
                    // Navigate to main activity
                    Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                    session.saveUserSession(userSession);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onSignUpFailure(String message) {
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        TextView signUpTextView = findViewById(R.id.textViewLoginLink);
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
