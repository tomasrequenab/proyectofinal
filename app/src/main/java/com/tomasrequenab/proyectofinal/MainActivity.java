package com.tomasrequenab.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    static String USER_ID_KEY = "USER_ID";

    private BiometricManager biometricManager;
    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        biometricManager = BiometricManager.from(this);
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, biometricAuthCallback);

        checkBiometricAuthentication();
    }

    BiometricPrompt.AuthenticationCallback biometricAuthCallback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            loadRegister();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            loadLocation();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            loadRegister();
        }
    };

    private void checkBiometricAuthentication() {
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                if (isUserRegistered())
                    authenticateFingerPrint();
                else
                    loadRegister();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showUnavailableAuthenticationMethod();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                loadRegister();
                break;
        }
    }

    private boolean isUserRegistered() {
        return getApplicationContext()
                .getSharedPreferences("", Context.MODE_PRIVATE)
                .getString(USER_ID_KEY, null) != null;
    }

    private void authenticateFingerPrint() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Registro Biométrico UBICATEX")
                .setSubtitle("Inicia sesión utilizando tu huella digital")
                .setDeviceCredentialAllowed(false)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showUnavailableAuthenticationMethod() {
        Log.i("AUTH", "Authentication unavailable");
    }

    private void loadRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void loadLocation() {
        Intent locationIntent = new Intent(this, LocationActivity.class);
        startActivity(locationIntent);
    }
}