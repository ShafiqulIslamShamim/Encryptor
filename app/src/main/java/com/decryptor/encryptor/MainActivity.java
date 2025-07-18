package com.decryptor.encryptor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.*;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.color.MaterialColors;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private Context context;
  private UIInitializer uiInitializer;
  private ClipboardHandler clipboardHandler;
  private ConversionManager conversionManager;
  public static EventHandler eventHandler;
  private ErrorHandler errorHandler;
  private long lastBackPressTime = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    applyLocalTheme(); // 👈 Apply local theme

    // ✅ Enable edge-to-edge
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    super.onCreate(savedInstanceState);

    // Inside your Activity (e.g., in onCreate)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= 34) {
      Window window = getWindow();

      // Set status bar color
      TypedValue typedValue = new TypedValue();
      getTheme()
          .resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
      int colorSurface = typedValue.data;
      window.setStatusBarColor(colorSurface);

      // Set navigation bar color
      window.setNavigationBarColor(colorSurface);
    }

    context = this;

    OTAUpdateHelper.checkForUpdatesIfDue(context);

    boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
    if (logcat) {
      StoragePermissionHelper.checkAndRequestStoragePermission(this);
      if (StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this); // Pass context since LogcatSaver now uses SAF
      }
    }

    PasswordManager.init(this);

    // Initialize layout
    uiInitializer = new UIInitializer(this);
    String activityLayout =
        SharedPrefValues.getValue("enable_scrollable_layout", false)
            ? "activity_main_scroll"
            : "activity_main";

    if (!uiInitializer.initializeLayout(activityLayout)) {
      finish();
      return;
    }

    // ✅ Apply WindowInsets padding
    View rootView = findViewById(android.R.id.content);
    ViewCompat.setOnApplyWindowInsetsListener(
        rootView,
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    // Set toolbar
    Toolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);

    // Initialize app components
    clipboardHandler = new ClipboardHandler(this);
    errorHandler = new ErrorHandler(this);
    conversionManager =
        new ConversionManager(
            this,
            uiInitializer.getInputEditText(),
            uiInitializer.getResultEditText(),
            errorHandler);
    eventHandler =
        new EventHandler(this, uiInitializer, clipboardHandler, conversionManager, errorHandler);

    eventHandler.setupPreferences();
    eventHandler.setupListeners();
  }

  private void showColorPickerDialog() {
    int initialColor = Color.RED;

    String text = uiInitializer.getInputEditText().getText().toString().trim();
    if (!text.isEmpty()) {
      try {
        initialColor = Color.parseColor(text);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }

    ColorPickerDialogFragment dialog =
        new ColorPickerDialogFragment(
            initialColor,
            color -> {
              try {

                eventHandler.getRemovedFullSpanEditText(
                    uiInitializer.getInputEditText(), ForegroundColorSpan.class);

                eventHandler.getRemovedFullSpanEditText(
                    uiInitializer.getResultEditText(), ForegroundColorSpan.class);

                String hex = String.format("#%08X", color);
                Spannable spannable = new SpannableString(hex);

                // Background color
                spannable.setSpan(
                    new BackgroundColorSpan(color),
                    0,
                    hex.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Dynamic text color using Material theme (fallback to black/white)
                Context context = uiInitializer.getInputEditText().getContext();
                int textColor =
                    isColorDark(color)
                        ? MaterialColors.getColor(
                            context, com.google.android.material.R.attr.colorOnSurface, Color.WHITE)
                        : MaterialColors.getColor(
                            context,
                            com.google.android.material.R.attr.colorOnPrimary,
                            Color.BLACK);

                // Foreground color
                spannable.setSpan(
                    new ForegroundColorSpan(textColor),
                    0,
                    hex.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                conversionManager.setProgrammaticTextChange(true);

                uiInitializer.getInputEditText().setText(spannable);
                uiInitializer.getInputEditText().setSelection(spannable.length());

                conversionManager.setProgrammaticTextChange(false);

                conversionManager.performConversion(true);

                EditText resultEditText = uiInitializer.getResultEditText();

                CharSequence coloredText =
                    eventHandler.setColorTextBackground(
                        hex, resultEditText.getText().toString(), resultEditText);
                conversionManager.setProgrammaticTextChange(true);
                resultEditText.setText(coloredText);
                resultEditText.setSelection(coloredText.length());
                conversionManager.setProgrammaticTextChange(false);

              } catch (Exception e) {
                errorHandler.logError(TAG, "Error in input text watcher: " + e.getMessage());
                errorHandler.highlightError(
                    uiInitializer.getInputEditText(), uiInitializer.getResultEditText());
              }
            });
    dialog.show(getSupportFragmentManager(), "ColorPickerDialog");
  }

  private boolean isColorDark(int color) {
    double darkness =
        1
            - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                / 255;
    return darkness >= 0.5;
  }

  private void applyLocalTheme() {
    String themePref = SharedPrefValues.getValue("theme_preference", "0");
    switch (themePref) {
      case "2": // Dark
        setTheme(R.style.AppThemeDark);
        break;
      case "3": // Light
        setTheme(R.style.AppThemeLight);
        break;
      default: // System/default
        setTheme(R.style.AppTheme);
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return uiInitializer.initializeMenu(menu, eventHandler);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int resetId = getResources().getIdentifier("reset", "id", getPackageName());
    int settingsId = getResources().getIdentifier("settings", "id", getPackageName());
    int hexSwitchId = getResources().getIdentifier("menu_hex_switch", "id", getPackageName());

    if (item.getItemId() == resetId) {
      eventHandler.handleReset();
      return true;
    } else if (item.getItemId() == settingsId) {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    } else if (item.getItemId() == R.id.action_private_key) {

      if (!PasswordManager.isPrivateKeySet()) {
        PasswordManager.showSetPasswordDialog(this);
      } else {
        PasswordManager.showPasswordPrompt(this);
      }

      return true;
    } else if (item.getItemId() == R.id.action_pick_color) {
      showColorPickerDialog();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    long currentTime = System.currentTimeMillis();
    int pressBackStringId =
        getResources().getIdentifier("press_back_again", "string", getPackageName());
    if (currentTime - lastBackPressTime > 2000) {
      errorHandler.showToast(pressBackStringId);
      lastBackPressTime = currentTime;
    } else {
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Handle folder picker result
    if (requestCode == StoragePermissionHelper.REQUEST_CODE_OLD_STORAGE) {
      StoragePermissionHelper.handleFolderPickerResult(this, requestCode, resultCode, data);

      boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
      if (logcat && StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == StoragePermissionHelper.REQUEST_CODE_OLD_STORAGE) {
      boolean granted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          granted = false;
          break;
        }
      }

      boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
      if (granted && logcat && StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }
    }
  }
}
