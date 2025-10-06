package com.decryptor.encryptor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.*;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import androidx.activity.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;

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

    super.onCreate(savedInstanceState);

    applySystemBarIconColors();

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
    String activityLayout = "activity_main";

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
    MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);

    // Initialize app components
    clipboardHandler = new ClipboardHandler(this);
    errorHandler = new ErrorHandler(this);
    conversionManager =
        new ConversionManager(
            this,
            uiInitializer.getInputEditText(),
            uiInitializer.getResultEditText(),
            uiInitializer.getInputEditTextLayout(),
            uiInitializer.getResultEditTextLayout(),
            errorHandler);
    eventHandler =
        new EventHandler(this, uiInitializer, clipboardHandler, conversionManager, errorHandler);

    eventHandler.setupPreferences();
    eventHandler.setupListeners();

    //  logDynamicColors(this);
  }

  private void logDynamicColors(Context context) {
    int[] attrs =
        new int[] {
          com.google.android.material.R.attr.colorPrimary,
          com.google.android.material.R.attr.colorOnPrimary,
          com.google.android.material.R.attr.colorPrimaryContainer,
          com.google.android.material.R.attr.colorOnPrimaryContainer,
          com.google.android.material.R.attr.colorPrimaryInverse,
          com.google.android.material.R.attr.colorPrimaryFixed,
          com.google.android.material.R.attr.colorPrimaryFixedDim,
          com.google.android.material.R.attr.colorOnPrimaryFixed,
          com.google.android.material.R.attr.colorOnPrimaryFixedVariant,
          com.google.android.material.R.attr.colorSecondary,
          com.google.android.material.R.attr.colorOnSecondary,
          com.google.android.material.R.attr.colorSecondaryContainer,
          com.google.android.material.R.attr.colorOnSecondaryContainer,
          com.google.android.material.R.attr.colorSecondaryFixed,
          com.google.android.material.R.attr.colorSecondaryFixedDim,
          com.google.android.material.R.attr.colorOnSecondaryFixed,
          com.google.android.material.R.attr.colorOnSecondaryFixedVariant,
          com.google.android.material.R.attr.colorTertiary,
          com.google.android.material.R.attr.colorOnTertiary,
          com.google.android.material.R.attr.colorTertiaryContainer,
          com.google.android.material.R.attr.colorOnTertiaryContainer,
          com.google.android.material.R.attr.colorTertiaryFixed,
          com.google.android.material.R.attr.colorTertiaryFixedDim,
          com.google.android.material.R.attr.colorOnTertiaryFixed,
          com.google.android.material.R.attr.colorOnTertiaryFixedVariant,
          com.google.android.material.R.attr.colorError,
          com.google.android.material.R.attr.colorOnError,
          com.google.android.material.R.attr.colorErrorContainer,
          com.google.android.material.R.attr.colorOnErrorContainer,
          com.google.android.material.R.attr.colorOutline,
          com.google.android.material.R.attr.colorOutlineVariant,
          android.R.attr.colorBackground, // Use android namespace for background
          com.google.android.material.R.attr.colorOnBackground,
          com.google.android.material.R.attr.colorSurface,
          com.google.android.material.R.attr.colorOnSurface,
          com.google.android.material.R.attr.colorSurfaceVariant,
          com.google.android.material.R.attr.colorOnSurfaceVariant,
          com.google.android.material.R.attr.colorSurfaceInverse,
          com.google.android.material.R.attr.colorOnSurfaceInverse,
          com.google.android.material.R.attr.colorSurfaceBright,
          com.google.android.material.R.attr.colorSurfaceDim,
          com.google.android.material.R.attr.colorSurfaceContainer,
          com.google.android.material.R.attr.colorSurfaceContainerLow,
          com.google.android.material.R.attr.colorSurfaceContainerLowest,
          com.google.android.material.R.attr.colorSurfaceContainerHigh,
          com.google.android.material.R.attr.colorSurfaceContainerHighest
        };

    String[] names =
        new String[] {
          "colorPrimary",
          "colorOnPrimary",
          "colorPrimaryContainer",
          "colorOnPrimaryContainer",
          "colorPrimaryInverse",
          "colorPrimaryFixed",
          "colorPrimaryFixedDim",
          "colorOnPrimaryFixed",
          "colorOnPrimaryFixedVariant",
          "colorSecondary",
          "colorOnSecondary",
          "colorSecondaryContainer",
          "colorOnSecondaryContainer",
          "colorSecondaryFixed",
          "colorSecondaryFixedDim",
          "colorOnSecondaryFixed",
          "colorOnSecondaryFixedVariant",
          "colorTertiary",
          "colorOnTertiary",
          "colorTertiaryContainer",
          "colorOnTertiaryContainer",
          "colorTertiaryFixed",
          "colorTertiaryFixedDim",
          "colorOnTertiaryFixed",
          "colorOnTertiaryFixedVariant",
          "colorError",
          "colorOnError",
          "colorErrorContainer",
          "colorOnErrorContainer",
          "colorOutline",
          "colorOutlineVariant",
          "colorBackground",
          "colorOnBackground",
          "colorSurface",
          "colorOnSurface",
          "colorSurfaceVariant",
          "colorOnSurfaceVariant",
          "colorSurfaceInverse",
          "colorOnSurfaceInverse",
          "colorSurfaceBright",
          "colorSurfaceDim",
          "colorSurfaceContainer",
          "colorSurfaceContainerLow",
          "colorSurfaceContainerLowest",
          "colorSurfaceContainerHigh",
          "colorSurfaceContainerHighest"
        };

    for (int i = 0; i < attrs.length; i++) {
      int color = getThemeColor(context, attrs[i]);
      String hex = String.format("#%08X", color);
      Log.d("ThemeColors", names[i] + ": " + hex);
    }
  }

  private int getThemeColor(Context context, int attrResId) {
    TypedValue typedValue = new TypedValue();
    Resources.Theme theme = context.getTheme();
    if (theme.resolveAttribute(attrResId, typedValue, true)) {
      return typedValue.data;
    } else {
      Log.e("ThemeColors", "Attribute not found: " + attrResId);
      return 0xFF00FF; // fallback magenta
    }
  }

  public void showColorPickerDialog() {
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

                uiInitializer.getInputEditTextLayout().setError(null);

                uiInitializer.getResultEditTextLayout().setError(null);

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

                TextInputEditText resultEditText = uiInitializer.getResultEditText();

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
                    uiInitializer.getInputEditText(),
                    uiInitializer.getResultEditText(),
                    uiInitializer.getInputEditTextLayout(),
                    uiInitializer.getResultEditTextLayout());
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

  private void applySystemBarIconColors() {
    String themePref = SharedPrefValues.getValue("theme_preference", "0");

    boolean isLightTheme;

    switch (themePref) {
      case "2": // Dark theme
        isLightTheme = false;
        break;
      case "3": // Light theme
        isLightTheme = true;
        break;
      default:
        // Follow system theme
        int nightModeFlags =
            getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isLightTheme = (nightModeFlags != Configuration.UI_MODE_NIGHT_YES);
        break;
    }

    // Enable edge-to-edge (backward compatible)
    EdgeToEdge.enable(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return uiInitializer.initializeMenu(menu, eventHandler);
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
