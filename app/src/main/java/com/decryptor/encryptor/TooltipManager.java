package com.decryptor.encryptor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class TooltipManager {

  private final Activity activity;
  private final List<TapTarget> targets = new ArrayList<>();
  private int currentIndex = 0;
  private TapTargetView currentTooltip;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private final Runnable autoDismissRunnable;

  private MaterialToolbar toolbar;
  private MaterialButton imageCopy1,
      imagePaste1,
      imageShare1,
      imageExport1,
      imageImport1,
      imageCopy2,
      imagePaste2,
      imageShare2,
      imageImport2,
      imageExport2,
      imageReload;
  private View InputEditText, OutputEditText;

  public TooltipManager(Activity activity) {
    this.activity = activity;
    this.autoDismissRunnable =
        () -> {
          if (currentTooltip != null && !activity.isFinishing()) {
            currentTooltip.dismiss(false);
          }
        };
    initViews();
  }

  private void initViews() {
    toolbar = activity.findViewById(R.id.topAppBar);
    InputEditText = activity.findViewById(R.id.converterEditText1);
    imageCopy1 = activity.findViewById(R.id.converterImageView1);
    imagePaste1 = activity.findViewById(R.id.converterImageView3);

    imageShare1 = activity.findViewById(R.id.shareImageView1);
    imageExport1 = activity.findViewById(R.id.exportImageView1);
    imageImport1 = activity.findViewById(R.id.importImageView1);

    OutputEditText = activity.findViewById(R.id.converterEditText2);
    imageCopy2 = activity.findViewById(R.id.converterImageView2);
    imagePaste2 = activity.findViewById(R.id.converterImageView4);

    imageShare2 = activity.findViewById(R.id.shareImageView2);
    imageExport2 = activity.findViewById(R.id.exportImageView2);
    imageImport2 = activity.findViewById(R.id.importImageView2);

    imageReload = activity.findViewById(R.id.converterImageView5);
  }

  public void maybeShowTooltips() {
    if (isGuideShown()) return;

    toolbar.post(
        () -> {
          Menu menu = toolbar.getMenu();

          // Pick color
          MenuItem pickColorItem = menu.findItem(R.id.action_pick_color);
          View pickColorView = pickColorItem.getActionView();
          View pickColorIcon = pickColorView.findViewById(R.id.icon_image);

          // Private key
          MenuItem privateKeyItem = menu.findItem(R.id.action_private_key);
          View privateKeyView = privateKeyItem.getActionView();
          View privateKeyIcon = privateKeyView.findViewById(R.id.icon_image);

          // Reset
          MenuItem resetItem = menu.findItem(R.id.reset);
          View resetView = resetItem.getActionView();
          View resetIcon = resetView.findViewById(R.id.icon_image);

          // Settings
          MenuItem settingsItem = menu.findItem(R.id.settings);
          View settingsView = settingsItem.getActionView();
          View settingsIcon = settingsView.findViewById(R.id.icon_image);

          // Conversation
          MenuItem conversionItem = menu.findItem(R.id.action_conversion);
          View conversionView = conversionItem.getActionView();
          View conversionIcon = conversionView.findViewById(R.id.icon_image);

          // Switch compat

          MenuItem hexItem = menu.findItem(R.id.menu_hex_switch);
          View switchLayout = hexItem.getActionView();
          View hexSwitch = switchLayout.findViewById(R.id.hex_switch);

          targets.clear();
          currentIndex = 0;

          addTooltip(
              InputEditText,
              "Input Field",
              "Enter the text you want to convert using your selected method.");
          addTooltip(imageCopy1, "Copy", "Copies the text from the input field.");
          addTooltip(imagePaste1, "Paste", "Pastes text from the clipboard into the input field.");
          addTooltip(
              imageShare1, "Share", "Shares the text to other apps installed on your device.");
          addTooltip(
              imageImport1,
              "Import",
              "Gets the text from a chosen text file and then converts it.");
          addTooltip(
              imageExport1, "Export", "Saves the text to a user-selected location using SAF.");

          addTooltip(
              OutputEditText,
              "Result Field",
              "Displays the converted result. You can also input converted text here to reverse it"
                  + " back to the original.");
          addTooltip(imageCopy2, "Copy", "Copies the text from the result field.");
          addTooltip(imagePaste2, "Paste", "Pastes text from the clipboard into the result field.");
          addTooltip(imageReload, "Reload", "Regenerates and refreshes the output.");
          addTooltip(
              imageShare2, "Share", "Shares the text to other apps installed on your device.");

          addTooltip(
              imageImport2,
              "Import",
              "Gets the text from a chosen text file and then converts it.");
          addTooltip(
              imageExport2, "Export", "Saves the text to a user-selected location using SAF.");
          addTooltip(
              conversionIcon,
              "Conversion selector",
              "Can change conversion within a second by using it.");
          addTooltip(
              pickColorIcon, "Color picker", "Pick a color easily with Color picker dialog.");
          addTooltip(
              privateKeyIcon,
              "Private Key",
              "Set a password and key, or view your saved private key.");
          addTooltip(hexSwitch, "Hex Mode", "Enable to generate results in hexadecimal format.");
          addTooltip(resetIcon, "Reset", "Clears both input and result fields.");
          addTooltip(settingsIcon, "Settings", "Customize your preferences here.");

          if (!targets.isEmpty()) {
            showNextTooltip();
          } else {
            setGuideShown();
          }
        });
  }

  private void addTooltip(View view, String title, String description) {
    if (view == null
        || view.getVisibility() != View.VISIBLE
        || !view.getGlobalVisibleRect(new Rect())) return;

    int colorPrimary = resolveThemeColor(com.google.android.material.R.attr.colorPrimary);
    int textColor = resolveThemeColor(com.google.android.material.R.attr.colorOnPrimary);

    TapTarget target =
        TapTarget.forView(view, title, description)
            .outerCircleColorInt(colorPrimary)
            .targetCircleColorInt(textColor)
            .textColorInt(textColor)
            .titleTextSize(20)
            .descriptionTextSize(14)
            .transparentTarget(true)
            .cancelable(true)
            .tintTarget(false);

    if (view == InputEditText || view == OutputEditText) {
      target.targetRadius(32);
    } else {
      target.targetRadius(21);
    }

    targets.add(target);
  }

  private void showNextTooltip() {
    handler.removeCallbacks(autoDismissRunnable);

    if (currentIndex >= targets.size() || activity.isFinishing()) {
      currentTooltip = null;
      setGuideShown();
      return;
    }

    TapTarget target = targets.get(currentIndex);
    currentTooltip =
        TapTargetView.showFor(
            activity,
            target,
            new TapTargetView.Listener() {
              @Override
              public void onTargetClick(TapTargetView view) {
                view.dismiss(false);
              }

              @Override
              public void onOuterCircleClick(TapTargetView view) {
                view.dismiss(false);
              }

              @Override
              public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                currentTooltip = null;
                currentIndex++;
                showNextTooltip();
              }
            });

    handler.postDelayed(autoDismissRunnable, 3000);
  }

  private int resolveThemeColor(int attrResId) {
    TypedValue typedValue = new TypedValue();
    activity.getTheme().resolveAttribute(attrResId, typedValue, true);
    return typedValue.data;
  }

  public boolean isGuideShown() {
    SharedPreferences prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    return prefs.getBoolean("full_guide_shown", false);
  }

  private void setGuideShown() {
    SharedPreferences prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    prefs.edit().putBoolean("full_guide_shown", true).apply();
  }

  public void resetGuide() {
    SharedPreferences prefs = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    prefs.edit().putBoolean("full_guide_shown", false).apply();
  }

  public void cleanup() {
    handler.removeCallbacks(autoDismissRunnable);
    if (currentTooltip != null) {
      currentTooltip.dismiss(false);
      currentTooltip = null;
    }
  }
}
