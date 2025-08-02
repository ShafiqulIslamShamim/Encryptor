package com.decryptor.encryptor;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class BiometricAuthHelper {

  public interface AuthCallback {
    void onAuthSuccess();

    void onAuthFailed();

    void onAuthError(String error);
  }

  public static void authenticate(
      Context context, @NonNull AppCompatActivity activity, @NonNull AuthCallback callback) {

    Executor executor = ContextCompat.getMainExecutor(context);

    // PromptInfo তৈরি
    BiometricPrompt.PromptInfo promptInfo =
        new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify your identity")
            .setSubtitle("Authenticate to continue")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                    | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build();

    // ✅ declare biometricPrompt[] as single-element array (Java trick for "effectively final")
    final BiometricPrompt[] biometricPrompt = new BiometricPrompt[1];

    biometricPrompt[0] =
        new BiometricPrompt(
            activity,
            executor,
            new BiometricPrompt.AuthenticationCallback() {
              @Override
              public void onAuthenticationSucceeded(
                  @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthSuccess();
              }

              @Override
              public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthFailed();
              }

              @Override
              public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED
                    || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    || errorCode == BiometricPrompt.ERROR_CANCELED) {

                  // ✅ Now this works — because biometricPrompt is final-equivalent
                  biometricPrompt[0].authenticate(promptInfo);
                } else {
                  callback.onAuthError(errString.toString());
                }
              }
            });

    biometricPrompt[0].authenticate(promptInfo);
  }
}
