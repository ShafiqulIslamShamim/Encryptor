package com.decryptor.encryptor;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.color.MaterialColors;

public class EventHandler {
  private static final String TAG = "EventHandler";
  private final AppCompatActivity activity;
  private final UIInitializer uiInitializer;
  private final ClipboardHandler clipboardHandler;
  private final ConversionManager conversionManager;
  private final ErrorHandler errorHandler;
  private boolean isInputFocused = true;

  public EventHandler(
      AppCompatActivity activity,
      UIInitializer uiInitializer,
      ClipboardHandler clipboardHandler,
      ConversionManager conversionManager,
      ErrorHandler errorHandler) {
    this.activity = activity;
    this.uiInitializer = uiInitializer;
    this.clipboardHandler = clipboardHandler;
    this.conversionManager = conversionManager;
    this.errorHandler = errorHandler;
  }

  public void setupPreferences() {
    // Set hint for resultEditText based on SharedPreferences
    uiInitializer.setResultEditTextHint();
    // Perform initial conversion
    conversionManager.performConversion(isInputFocused);
  }

  public void setupListeners() {
    // Click listener for copy/paste ImageViews
    View.OnClickListener clickListener =
        new View.OnClickListener() {
          public void onClick(View v) {
            int viewId = v.getId();
            int converterImageView1Id =
                activity
                    .getResources()
                    .getIdentifier("converterImageView1", "id", activity.getPackageName());
            int converterImageView2Id =
                activity
                    .getResources()
                    .getIdentifier("converterImageView2", "id", activity.getPackageName());
            int converterImageView3Id =
                activity
                    .getResources()
                    .getIdentifier("converterImageView3", "id", activity.getPackageName());
            int converterImageView4Id =
                activity
                    .getResources()
                    .getIdentifier("converterImageView4", "id", activity.getPackageName());

            int converterImageView5Id =
                activity
                    .getResources()
                    .getIdentifier("converterImageView5", "id", activity.getPackageName());

            if (viewId == converterImageView1Id) {
              clipboardHandler.copyText(uiInitializer.getInputEditText(), "Input");
            } else if (viewId == converterImageView2Id) {
              clipboardHandler.copyText(uiInitializer.getResultEditText(), "Result");
            } else if (viewId == converterImageView3Id) {
              uiInitializer.getInputEditText().requestFocus();
              clipboardHandler.pasteText(uiInitializer.getInputEditText());
            } else if (viewId == converterImageView4Id) {
              uiInitializer.getResultEditText().requestFocus();
              clipboardHandler.pasteText(uiInitializer.getResultEditText());
            } else if (viewId == converterImageView5Id) {
              isInputFocused = true;
            }

            conversionManager.performConversion(isInputFocused);
          }
        };

    uiInitializer.getCopyInputImageView().setOnClickListener(clickListener);
    uiInitializer.getCopyResultImageView().setOnClickListener(clickListener);
    uiInitializer.getPasteInputImageView().setOnClickListener(clickListener);
    uiInitializer.getPasteResultImageView().setOnClickListener(clickListener);
    uiInitializer.getReloadImageView().setOnClickListener(clickListener);

    // Focus change listener
    View.OnFocusChangeListener focusChangeListener =
        new View.OnFocusChangeListener() {
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              int converterEditText1Id =
                  activity
                      .getResources()
                      .getIdentifier("converterEditText1", "id", activity.getPackageName());
              int converterEditText2Id =
                  activity
                      .getResources()
                      .getIdentifier("converterEditText2", "id", activity.getPackageName());
              if (v.getId() == converterEditText1Id) {
                isInputFocused = true;
              } else if (v.getId() == converterEditText2Id) {
                isInputFocused = false;
              }
            }
            conversionManager.performConversion(isInputFocused);
          }
        };
    uiInitializer.getInputEditText().setOnFocusChangeListener(focusChangeListener);
    uiInitializer.getResultEditText().setOnFocusChangeListener(focusChangeListener);

    // TextWatcher for inputEditText
    uiInitializer
        .getInputEditText()
        .addTextChangedListener(
            new TextWatcher() {
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

              public void onTextChanged(CharSequence s, int start, int before, int count) {}

              public void afterTextChanged(Editable s) {
                if (conversionManager.isProgrammaticTextChange()) return;

                try {
                  EditText inputEditText = uiInitializer.getInputEditText();

                  getRemovedFullSpan(s, ForegroundColorSpan.class);

                  if (inputEditText.isFocused() && s.length() > 0) {

                    int selectedIndex = SharedPrefValues.getValue("converter_select", 27);
                    boolean checkColorMode =
                        selectedIndex == 26 || selectedIndex == 28 || selectedIndex == 29;

                    if (checkColorMode) {
                      String input = s.toString();

                      String getHashInput = input.startsWith("#") ? input : "#" + input;

                      CharSequence coloredText =
                          setColorTextBackground(getHashInput, getHashInput, inputEditText);
                      conversionManager.setProgrammaticTextChange(true);
                      inputEditText.setText(coloredText);
                      inputEditText.setSelection(coloredText.length());
                      conversionManager.setProgrammaticTextChange(false);

                      conversionManager.performConversion(isInputFocused);

                      EditText resultEditText = uiInitializer.getResultEditText();

                      CharSequence coloredTextResult =
                          setColorTextBackground(
                              getHashInput, resultEditText.getText().toString(), resultEditText);
                      conversionManager.setProgrammaticTextChange(true);
                      resultEditText.setText(coloredTextResult);
                      resultEditText.setSelection(coloredTextResult.length());
                      conversionManager.setProgrammaticTextChange(false);

                    } else {
                      conversionManager.performConversion(isInputFocused);
                    }

                  } else if (s.length() == 0) {
                    conversionManager.setProgrammaticTextChange(true);
                    uiInitializer.getResultEditText().setText("");
                    conversionManager.setProgrammaticTextChange(false);
                  }

                } catch (Exception e) {
                  errorHandler.logError(TAG, "Error in input text watcher: " + e.getMessage());
                  errorHandler.highlightError(
                      uiInitializer.getInputEditText(), uiInitializer.getResultEditText());
                }
              }
            });

    // TextWatcher for resultEditText
    uiInitializer
        .getResultEditText()
        .addTextChangedListener(
            new TextWatcher() {
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

              public void onTextChanged(CharSequence s, int start, int before, int count) {}

              public void afterTextChanged(Editable s) {
                if (conversionManager.isProgrammaticTextChange()) {
                  return;
                }
                try {
                  getRemovedFullSpan(s, Class.forName("android.text.style.ForegroundColorSpan"));

                  EditText resultEditText = uiInitializer.getResultEditText();

                  // s.removeSpan(new
                  // android.text.style.ForegroundColorSpan(android.graphics.Color.RED));
                  if (resultEditText.isFocused() && s.length() > 0) {

                    int selectedIndex = SharedPrefValues.getValue("converter_select", 27);
                    boolean checkColorMode =
                        selectedIndex == 26 || selectedIndex == 28 || selectedIndex == 29;

                    if (checkColorMode) {
                      String result = s.toString();
                      String getHashColorFromResult = "";

                      if (selectedIndex == 26) {

                        getHashColorFromResult = ColorConverterTextStrings.c(result);

                      } else if (selectedIndex == 28) {

                        getHashColorFromResult =
                            String.format("#%08x", Integer.parseInt(result) & (-1));

                      } else if (selectedIndex == 29) {

                        getHashColorFromResult =
                            String.format("#%08x", ConvertTextStrings.k(result) & (-1));
                      }

                      CharSequence coloredText =
                          setColorTextBackground(getHashColorFromResult, result, resultEditText);
                      conversionManager.setProgrammaticTextChange(true);
                      resultEditText.setText(coloredText);
                      resultEditText.setSelection(coloredText.length());
                      conversionManager.setProgrammaticTextChange(false);

                      conversionManager.performConversion(isInputFocused);

                      EditText inputEditText = uiInitializer.getInputEditText();

                      CharSequence coloredTextInput =
                          setColorTextBackground(
                              getHashColorFromResult,
                              inputEditText.getText().toString(),
                              inputEditText);
                      conversionManager.setProgrammaticTextChange(true);
                      inputEditText.setText(coloredTextInput);
                      inputEditText.setSelection(coloredTextInput.length());
                      conversionManager.setProgrammaticTextChange(false);

                    } else {

                      conversionManager.performConversion(isInputFocused);
                    }
                  } else if (s.length() == 0) {
                    conversionManager.setProgrammaticTextChange(true);
                    uiInitializer.getInputEditText().setText("");
                    conversionManager.setProgrammaticTextChange(false);
                  }
                } catch (Exception e) {
                  errorHandler.logError(TAG, "Error in result text watcher: " + e.getMessage());
                  errorHandler.highlightError(
                      uiInitializer.getInputEditText(), uiInitializer.getResultEditText());
                }
              }
            });
  }

  public static CharSequence setColorTextBackground(
      String colorhash, String text, EditText editText) {
    Spannable spannable = new SpannableString(text);
    int color;

    try {
      color = Color.parseColor(colorhash);

      // Set background color
      spannable.setSpan(
          new BackgroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

      // Get context from EditText
      Context context = editText.getContext();

      // Determine readable text color using dynamic Material colors
      int textColor =
          isColorDark(color)
              ? MaterialColors.getColor(
                  context, com.google.android.material.R.attr.colorOnSurface, Color.WHITE)
              : MaterialColors.getColor(
                  context, com.google.android.material.R.attr.colorOnPrimary, Color.BLACK);

      // Set text color
      spannable.setSpan(
          new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    } catch (IllegalArgumentException e) {
      /*   // On invalid hex, use red text
      spannable.setSpan(
          new ForegroundColorSpan(Color.RED),
          0,
          text.length(),
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      );
      */
    }

    return spannable;
  }

  public static boolean isColorDark(int color) {
    double darkness =
        1
            - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                / 255;
    return darkness >= 0.5;
  }

  public void handleReset() {
    conversionManager.setProgrammaticTextChange(true);
    uiInitializer.getInputEditText().setText("");
    uiInitializer.getResultEditText().setText("");
    conversionManager.setProgrammaticTextChange(false);
    uiInitializer.getInputEditText().requestFocus();
  }

  public static void getRemovedFullSpan(Editable editable, Class cls) {
    for (CharacterStyle characterStyle :
        (CharacterStyle[]) editable.getSpans(0, editable.length(), cls)) {
      editable.removeSpan(characterStyle);
    }
  }
}
