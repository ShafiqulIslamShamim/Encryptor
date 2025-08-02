package com.decryptor.encryptor;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UIInitializer {
  private static final String TAG = "UIInitializer";
  private final AppCompatActivity activity;

  private TextInputEditText inputEditText;
  private TextInputEditText resultEditText;

  private TextInputLayout inputEditTextLayout;
  private TextInputLayout resultEditTextLayout;

  private MaterialButton copyInputImageView;
  private MaterialButton copyResultImageView;
  private MaterialButton pasteInputImageView;
  private MaterialButton pasteResultImageView;
  private MaterialButton reloadImageView;

  private MaterialButton exportInputImageView;
  private MaterialButton exportResultImageView;
  private MaterialButton shareInputImageView;
  private MaterialButton shareResultImageView;

  private TextView labelEditText1;
  private TextView labelEditText2;

  public UIInitializer(AppCompatActivity activity) {
    this.activity = activity;
  }

  public boolean initializeLayout(String layoutName) {
    int layoutId =
        activity.getResources().getIdentifier(layoutName, "layout", activity.getPackageName());
    if (layoutId == 0) {
      Log.e(TAG, "Layout resource not found: " + layoutName);
      return false;
    }
    activity.setContentView(layoutId);

    inputEditText = initializeView("converterEditText1", "id", TextInputEditText.class);
    resultEditText = initializeView("converterEditText2", "id", TextInputEditText.class);

    inputEditTextLayout = initializeView("textInputLayout1", "id", TextInputLayout.class);
    resultEditTextLayout = initializeView("textInputLayout2", "id", TextInputLayout.class);

    copyInputImageView = initializeView("converterImageView1", "id", MaterialButton.class);
    copyResultImageView = initializeView("converterImageView2", "id", MaterialButton.class);
    pasteInputImageView = initializeView("converterImageView3", "id", MaterialButton.class);
    pasteResultImageView = initializeView("converterImageView4", "id", MaterialButton.class);

    exportInputImageView = initializeView("exportImageView1", "id", MaterialButton.class);
    exportResultImageView = initializeView("exportImageView2", "id", MaterialButton.class);
    shareInputImageView = initializeView("shareImageView1", "id", MaterialButton.class);
    shareResultImageView = initializeView("shareImageView2", "id", MaterialButton.class);

    reloadImageView = initializeView("converterImageView5", "id", MaterialButton.class);
    labelEditText1 = initializeView("labelEditText1", "id", TextView.class);
    labelEditText2 = initializeView("labelEditText2", "id", TextView.class);

    if (inputEditText == null
        || resultEditText == null
        || copyInputImageView == null
        || copyResultImageView == null
        || pasteInputImageView == null
        || pasteResultImageView == null
        || exportInputImageView == null
        || exportResultImageView == null
        || shareInputImageView == null
        || shareResultImageView == null
        || reloadImageView == null
        || labelEditText1 == null
        || labelEditText2 == null) {
      Log.e(TAG, "One or more UI components failed to initialize");
      new ErrorHandler(activity).showToast("Error: UI initialization failed");
      return false;
    }

    int selectedConvertion = SharedPrefValues.getValue("converter_select", 27);

    if (selectedConvertion == 27) {
      reloadImageView.setVisibility(View.VISIBLE); // দেখাবে
    } else {
      reloadImageView.setVisibility(View.GONE); // লুকাবে
    }

    copyInputImageView.setClickable(true);
    copyResultImageView.setClickable(true);
    pasteInputImageView.setClickable(true);
    pasteResultImageView.setClickable(true);
    exportInputImageView.setClickable(true);
    exportResultImageView.setClickable(true);
    shareInputImageView.setClickable(true);
    shareResultImageView.setClickable(true);
    reloadImageView.setClickable(true);

    return true;
  }

  public static String getEntryForValue(String value, String[] entryValues, String[] entries) {
    for (int i = 0; i < entryValues.length; i++) {
      if (entryValues[i].equals(value)) {
        return entries[i];
      }
    }
    return null;
  }

  public void setResultEditTextHint() {
    int selectedIndex = SharedPrefValues.getValue("converter_select", 27);
    int Entries =
        activity
            .getResources()
            .getIdentifier("conversion_options", "array", activity.getPackageName());

    int EntryValue =
        activity
            .getResources()
            .getIdentifier("conversion_options_entryvalue", "array", activity.getPackageName());

    CharSequence inputHintText = inputEditText.getHint();
    String resultHintText = resultEditText.getHint().toString();

    String[] conversionOptions = activity.getResources().getStringArray(Entries);

    String[] conversionOptionsEntryvalue = activity.getResources().getStringArray(EntryValue);

    resultHintText =
        getEntryForValue(
            String.valueOf(selectedIndex), conversionOptionsEntryvalue, conversionOptions);

    if (selectedIndex == 27 || selectedIndex == 30) {
      // শর্ত পূরণ হলে যা হবে তা এখানে লিখ
      resultHintText = "Encrypt & decrypt";
    }

    labelEditText1.setText(inputHintText != null ? inputHintText.toString() : "Input");
    labelEditText2.setText(resultHintText);
  }

  public boolean initializeMenu(Menu menu, EventHandler eventHandler) {
    int menuId =
        activity.getResources().getIdentifier("main_menu", "menu", activity.getPackageName());
    if (menuId == 0) {
      Log.e(TAG, "Menu resource 'main_menu' not found");
      return false;
    }

    activity.getMenuInflater().inflate(menuId, menu);

    // Pick Color
    MenuItem pickColorItem = menu.findItem(R.id.action_pick_color);
    View pickColorView = pickColorItem.getActionView();
    ImageView pickColorIcon = pickColorView.findViewById(R.id.icon_image);
    pickColorIcon.setOnClickListener(
        v -> {
          MainActivity mainActivity = (MainActivity) activity;
          mainActivity.showColorPickerDialog();
        });

    // Private Key
    MenuItem privateKeyItem = menu.findItem(R.id.action_private_key);
    View privateKeyView = privateKeyItem.getActionView();
    ImageView privateKeyIcon = privateKeyView.findViewById(R.id.icon_image);
    privateKeyIcon.setOnClickListener(
        v -> {
          PasswordManager.startBiometricAuth(activity, activity);
        });

    // Reset
    MenuItem resetItem = menu.findItem(R.id.reset);
    View resetView = resetItem.getActionView();
    ImageView resetIcon = resetView.findViewById(R.id.icon_image);
    resetIcon.setOnClickListener(
        v -> {
          eventHandler.handleReset();
        });

    // Settings
    MenuItem settingsItem = menu.findItem(R.id.settings);
    View settingsView = settingsItem.getActionView();
    ImageView settingsIcon = settingsView.findViewById(R.id.icon_image);
    settingsIcon.setOnClickListener(
        v -> {
          activity.startActivity(new Intent(activity, SettingsActivity.class));
        });

    // Visibility toggle based on converter_select
    int selectedItemPositionpass = SharedPrefValues.getValue("converter_select", 27);

    // password switch setup

    MenuItem passwordItem = menu.findItem(R.id.action_private_key);

    if (passwordItem != null) {

      boolean visibility = selectedItemPositionpass == 27 || selectedItemPositionpass == 30;
      //  Log.d(TAG, "selectedItemPosition: " + selectedItemPosition + ", hexItem visible: " +
      // visible);
      passwordItem.setVisible(visibility); // Temporarily set to true for testing
    }

    // Color picker setup

    MenuItem colorPickerItem = menu.findItem(R.id.action_pick_color);

    if (colorPickerItem != null) {

      boolean visibilityColorPicker =
          selectedItemPositionpass == 26
              || selectedItemPositionpass == 28
              || selectedItemPositionpass == 29;
      //  Log.d(TAG, "selectedItemPosition: " + selectedItemPosition + ", hexItem visible: " +
      // visible);
      colorPickerItem.setVisible(visibilityColorPicker); // Temporarily set to true for testing
    }

    // Hex switch setup
    MenuItem hexItem = menu.findItem(R.id.menu_hex_switch);
    if (hexItem != null) {
      View switchLayout = hexItem.getActionView();
      if (switchLayout != null) {
        SwitchCompat hexSwitch = switchLayout.findViewById(R.id.hex_switch);
        TextView switchTitle = switchLayout.findViewById(R.id.switch_title);

        if (hexSwitch != null) {
          //       hexSwitch.setOnCheckedChangeListener(null); // Clear any existing listener
          boolean hexModeEnabled = SharedPrefValues.getValue("converter_hex", false);
          Log.d(TAG, "Initial hexModeEnabled: " + hexModeEnabled);
          hexSwitch.setChecked(hexModeEnabled);
          //  hexSwitch.setEnabled(true); // Ensure switch is enabled

          hexSwitch.setOnCheckedChangeListener(
              (buttonView, isChecked) -> {
                //      Log.d(TAG, "Switch toggled, isChecked: " + isChecked);
                SharedPrefValues.putValue(
                    "converter_hex", String.valueOf(SharedPrefValues.booleanToInt(isChecked)));
                //     Log.d(TAG, "Saved converter_hex: " +
                // SharedPrefValues.getValue("converter_hex", false));
                eventHandler.setupPreferences();
                eventHandler.setupListeners();
                Log.d(TAG, "Hex mode is now " + (isChecked ? "enabled" : "disabled"));
              });
          //  Log.d(TAG, "Listener attached to hexSwitch");
        } else {
          Log.e(TAG, "Switch view not found in menu action layout.");
        }

        // Visibility toggle based on converter_select
        int selectedItemPosition = SharedPrefValues.getValue("converter_select", 27);
        boolean visible = selectedItemPosition >= 9 && selectedItemPosition <= 15;
        //  Log.d(TAG, "selectedItemPosition: " + selectedItemPosition + ", hexItem visible: " +
        // visible);
        hexItem.setVisible(visible); // Temporarily set to true for testing
      } else {
        Log.e(TAG, "Hex switch action view is null.");
      }
    } else {
      Log.e(TAG, "menu_hex_switch item not found");
    }

    TooltipManager tooltipManager = new TooltipManager(activity);

    tooltipManager.maybeShowTooltips();

    return true;
  }

  private <T extends View> T initializeView(String name, String defType, Class<T> clazz) {
    int id = activity.getResources().getIdentifier(name, defType, activity.getPackageName());
    if (id == 0) {
      Log.e(TAG, "Resource not found: " + name + " (" + defType + ")");
      return null;
    }
    View view = activity.findViewById(id);
    if (view == null) {
      Log.e(TAG, "View not found for ID: " + name);
      return null;
    }
    try {
      return clazz.cast(view);
    } catch (ClassCastException e) {
      Log.e(TAG, "View cast failed for ID: " + name, e);
      return null;
    }
  }

  // Getters
  public TextInputEditText getInputEditText() {
    return inputEditText;
  }

  public TextInputEditText getResultEditText() {
    return resultEditText;
  }

  public TextInputLayout getInputEditTextLayout() {
    return inputEditTextLayout;
  }

  public TextInputLayout getResultEditTextLayout() {
    return resultEditTextLayout;
  }

  public MaterialButton getCopyInputImageView() {
    return copyInputImageView;
  }

  public MaterialButton getCopyResultImageView() {
    return copyResultImageView;
  }

  public MaterialButton getPasteInputImageView() {
    return pasteInputImageView;
  }

  public MaterialButton getPasteResultImageView() {
    return pasteResultImageView;
  }

  public MaterialButton getReloadImageView() {
    return reloadImageView;
  }

  public MaterialButton getExportInputImageView() {
    return exportInputImageView;
  }

  public MaterialButton getExportResultImageView() {
    return exportResultImageView;
  }

  public MaterialButton getShareInputImageView() {
    return shareInputImageView;
  }

  public MaterialButton getShareResultImageView() {
    return shareResultImageView;
  }

  public TextView getLabelEditText1() {
    return labelEditText1;
  }

  public TextView getLabelEditText2() {
    return labelEditText2;
  }
}
