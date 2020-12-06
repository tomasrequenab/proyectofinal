package com.tomasrequenab.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;
import java.util.concurrent.Executor;

public class RegisterActivity extends AppCompatActivity {
    static String USER_ID_KEY = "USER_ID";

    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, biometricAuthCallback);

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateFingerPrint();
            }
        });
    }

    BiometricPrompt.AuthenticationCallback biometricAuthCallback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(
                    getApplicationContext(),
                    "Error de Autenticación: " + errString,
                    Toast.LENGTH_LONG
            ).show();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            String userID = UUID.randomUUID().toString();
            saveUserID(userID);
            loadLocation();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(
                    getApplicationContext(),
                    "La autenticación falló",
                    Toast.LENGTH_LONG
            ).show();
        }
    };

    /**
     * Request finger print authentication
     */
    private void authenticateFingerPrint() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Registro Biométrico ubicatex")
                .setSubtitle("Registrate utilizando tu huella digital")
                .setNegativeButtonText("Cancelar")
                .setDeviceCredentialAllowed(false)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * Save user ID
     * @param userID User ID
     */
    private void saveUserID(String userID) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("", Context.MODE_PRIVATE).edit();
        editor.putString(USER_ID_KEY, userID);
        editor.apply();
    }

    /**
     * Load Location view
     */
    private void loadLocation() {
        Intent locationIntent = new Intent(this, LocationActivity.class);
        startActivity(locationIntent);
    }
}