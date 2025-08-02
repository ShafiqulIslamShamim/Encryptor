package com.decryptor.encryptor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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

  private Toolbar toolbar;
  private MaterialButton imageCopy1,
      imagePaste1,
      imageShare1,
      imageExport1,
      imageCopy2,
      imagePaste2,
      imageShare2,
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

    OutputEditText = activity.findViewById(R.id.converterEditText2);
    imageCopy2 = activity.findViewById(R.id.converterImageView2);
    imagePaste2 = activity.findViewById(R.id.converterImageView4);

    imageShare2 = activity.findViewById(R.id.shareImageView2);
    imageExport2 = activity.findViewById(R.id.exportImageView2);

    imageReload = activity.findViewById(R.id.converterImageView5);
  }

  public void maybeShowTooltips() {
    if (isGuideShown()) return;

    toolbar.post(
        () -> {
          View itemPrivateKey = toolbar.findViewById(R.id.action_private_key);
          View itemSwitch = toolbar.findViewById(R.id.menu_hex_switch);
          View itemReset = toolbar.findViewById(R.id.reset);
          View itemSettings = toolbar.findViewById(R.id.settings);

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
              imageExport2, "Export", "Saves the text to a user-selected location using SAF.");
          addTooltip(
              itemPrivateKey,
              "Private Key",
              "Set a password and key, or view your saved private key.");
          addTooltip(itemSwitch, "Hex Mode", "Enable to generate results in hexadecimal format.");
          addTooltip(itemReset, "Reset", "Clears both input and result fields.");
          addTooltip(itemSettings, "Settings", "Customize your preferences here.");

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
