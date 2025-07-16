package com.decryptor.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class PasswordManager {
  private static final String PREFS_NAME = "PrivatePrefs";
  private static final String PASSWORD_KEY = "password_key";
  private static final String PRIVATE_KEY = "private_key";

  public final SharedPreferences prefs;
  public boolean passwordSet;
  public final Context context;
  public String alias;
  public boolean privateKeySet;
  private static AlertDialog currentDialog = null;

  public PasswordManager(Context context) {
    this.context = context;
    this.prefs = initializeEncryptedSharedPreferences();
    this.passwordSet = prefs.contains(PASSWORD_KEY);
    this.privateKeySet = prefs.contains(PRIVATE_KEY);
    this.alias = prefs.getString(PRIVATE_KEY, "");
    Log.d("PasswordManager", "passwordSet: " + passwordSet + ", privateKeySet: " + privateKeySet);
  }

  private SharedPreferences initializeEncryptedSharedPreferences() {
    Log.d("PasswordManager", "Initializing EncryptedSharedPreferences");
    try {
      String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
      Log.d("PasswordManager", "Master key alias: " + masterKeyAlias);
      return EncryptedSharedPreferences.create(
          PREFS_NAME,
          masterKeyAlias,
          context,
          EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
          EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    } catch (GeneralSecurityException | IOException e) {
      Log.e("PasswordManager", "Error initializing EncryptedSharedPreferences", e);
      if (e instanceof javax.crypto.AEADBadTagException) {
        Log.w("PasswordManager", "Clearing corrupted SharedPreferences due to AEADBadTagException");
        // Clear corrupted SharedPreferences file
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        try {
          String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
          return EncryptedSharedPreferences.create(
              PREFS_NAME,
              masterKeyAlias,
              context,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException ex) {
          Log.e(
              "PasswordManager",
              "Failed to reinitialize EncryptedSharedPreferences after clearing",
              ex);
          throw new RuntimeException(
              "Failed to reinitialize EncryptedSharedPreferences after clearing", ex);
        }
      } else {
        throw new RuntimeException("Failed to initialize EncryptedSharedPreferences", e);
      }
    }
  }

  public boolean isPasswordSet() {
    return passwordSet;
  }

  public String getPrivateKey() {
    return alias;
  }

  public static void dismissCurrentDialog() {
    if (currentDialog != null && currentDialog.isShowing()) {
      currentDialog.dismiss();
      currentDialog = null;
    }
  }

  public void showSetPasswordDialog() {
    dismissCurrentDialog();

    TextInputLayout textInputLayout = new TextInputLayout(context);
    textInputLayout.setHint("Minimum 8 characters");
    TextInputEditText input = new TextInputEditText(context);
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    textInputLayout.addView(input);

    final MaterialCheckBox showPassword = new MaterialCheckBox(context);
    showPassword.setText("Show Password");

    LinearLayout layout = new LinearLayout(context);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(50, 40, 50, 10);
    layout.addView(textInputLayout);
    layout.addView(showPassword);

    showPassword.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            input.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
          } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
          }
          input.setSelection(input.getText().length());
        });

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            .setCustomTitle(DialogUtils.createStyledDialogTitle(context, "Set Password"))
            .setMessage("Set a password to protect your private key.")
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create();

    dialog.show();
    currentDialog = dialog;

    dialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            v -> {
              String password = input.getText().toString();
              String errorMessage = null;

              if (password.length() < 8) {
                errorMessage = "Password must be at least 8 characters long.";
              } else if (!password.matches(".*[A-Za-z].*")) {
                errorMessage = "Password must contain at least one letter.";
              } else if (!password.matches(".*\\d.*")) {
                errorMessage = "Password must contain at least one number.";
              }

              if (errorMessage != null) {
                textInputLayout.setError(errorMessage);
              } else {
                textInputLayout.setError(null);
                prefs.edit().putString(PASSWORD_KEY, password).apply();
                passwordSet = true;
                dialog.dismiss();
                currentDialog = null;
                showPrivateKeyDialog();
              }
            });

    dialog
        .getButton(AlertDialog.BUTTON_NEGATIVE)
        .setOnClickListener(
            v -> {
              if (input.getText().toString().trim().isEmpty()) {
                textInputLayout.setError("You can't leave it empty");
              } else {
                dialog.dismiss();
                currentDialog = null;
              }
            });
  }

  public void showPasswordPrompt() {
    TextInputLayout textInputLayout = new TextInputLayout(context);
    textInputLayout.setHint("Enter password");
    TextInputEditText input = new TextInputEditText(context);
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    textInputLayout.addView(input);

    final MaterialCheckBox showPassword = new MaterialCheckBox(context);
    showPassword.setText("Show Password");

    LinearLayout layout = new LinearLayout(context);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(50, 40, 50, 10);
    layout.addView(textInputLayout);
    layout.addView(showPassword);

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            //  .setTitle("Enter Password")
            .setCustomTitle(DialogUtils.createStyledDialogTitle(context, "Enter Password"))
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Reset", null)
            .create();

    dialog.show();

    showPassword.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            input.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
          } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
          }
          input.setSelection(input.getText().length());
        });

    dialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            v -> {
              String entered = input.getText().toString();
              String savedPassword = prefs.getString(PASSWORD_KEY, "");
              if (entered.equals(savedPassword)) {
                showPrivateKeyDialog();
                dialog.dismiss();
              } else {
                textInputLayout.setError("Incorrect password");
              }
            });

    dialog
        .getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener(
            v -> {
              // Clear all SharedPreferences data
              prefs.edit().clear().apply();
              // Delete the master key
              try {
                java.security.KeyStore keyStore =
                    java.security.KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                keyStore.deleteEntry(masterKeyAlias);
              } catch (Exception e) {
                Log.e("PasswordManager", "Failed to delete master key", e);
              }
              passwordSet = false;
              privateKeySet = false;
              alias = "";
              Toast.makeText(context, "Password and private key reset", Toast.LENGTH_SHORT).show();
              dialog.dismiss();
            });

    dialog
        .getButton(AlertDialog.BUTTON_NEGATIVE)
        .setOnClickListener(
            v -> {
              dialog.dismiss();
            });
  }

  public void showPrivateKeyDialog() {
    dismissCurrentDialog();

    TextInputLayout textInputLayout = new TextInputLayout(context);
    textInputLayout.setHint("Enter private key");
    TextInputEditText input = new TextInputEditText(context);
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    input.setText(prefs.getString(PRIVATE_KEY, ""));
    textInputLayout.addView(input);

    final MaterialCheckBox showPassword = new MaterialCheckBox(context);
    showPassword.setText("Show Password");

    LinearLayout layout = new LinearLayout(context);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(50, 40, 50, 10);
    layout.addView(textInputLayout);
    layout.addView(showPassword);

    showPassword.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            input.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
          } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
          }
          input.setSelection(input.getText().length());
        });

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            .setCustomTitle(DialogUtils.createStyledDialogTitle(context, "Private Key"))
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("Save", null)
            .setNeutralButton("Copy", null)
            .setNegativeButton("Cancel", null)
            .create();

    dialog.show();
    currentDialog = dialog;

    dialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            v -> {
              String key = input.getText().toString();
              if (key.length() < 1) {
                textInputLayout.setError("You must input at least a character.");
              } else {
                prefs.edit().putString(PRIVATE_KEY, key).apply();
                Toast.makeText(context, "Private key saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                currentDialog = null;
                if (MainActivity.eventHandler != null) {
                  MainActivity.eventHandler.setupPreferences();
                }
              }
            });

    dialog
        .getButton(AlertDialog.BUTTON_NEUTRAL)
        .setOnClickListener(
            v -> {
              String key = input.getText().toString();
              if (!key.isEmpty()) {
                ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Private Key", key);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Private key copied to clipboard", Toast.LENGTH_SHORT)
                    .show();
              }
            });

    dialog
        .getButton(AlertDialog.BUTTON_NEGATIVE)
        .setOnClickListener(
            v -> {
              if (input.getText().toString().trim().isEmpty()) {
                textInputLayout.setError("You can't leave it empty");
              } else {
                dialog.dismiss();
                currentDialog = null;
              }
            });

    new Handler(Looper.getMainLooper())
        .postDelayed(
            () -> {
              if (dialog.isShowing()) {
                dialog.dismiss();
                currentDialog = null;
              }
            },
            100_000);
  }
}
