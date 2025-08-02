package com.decryptor.encryptor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ErrorHandler {
  private static final String TAG = "ErrorHandler";
  private final Context context;

  public ErrorHandler(Context context) {
    this.context = context;
  }

  public void showToast(int stringId) {
    if (stringId == 0) {
      Toast.makeText(context, "Resource not found", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }
  }

  public void showToast(String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public void highlightError(
      TextInputEditText inputEditText,
      TextInputEditText resultEditText,
      TextInputLayout inputLayout,
      TextInputLayout resultLayout) {

    // Error messages
    String inputErrorText = "Invalid input";
    String resultErrorText = "Incorrect result";

    // Get error color from Material default
    int errorColor =
        ContextCompat.getColor(
            inputEditText.getContext(),
            com.google.android.material.R.color.design_default_color_error);
    ColorStateList errorColorStateList = ColorStateList.valueOf(errorColor);

    // --- Input Field Error Handling ---
    Editable inputText = inputEditText.getText();
    if (inputText != null && inputText.length() > 0) {
      inputLayout.setError(inputErrorText);
      inputLayout.setBoxStrokeErrorColor(errorColorStateList);
      inputLayout.setErrorTextColor(errorColorStateList);
      inputLayout.setErrorIconTintList(errorColorStateList);

      inputText.setSpan(
          new ForegroundColorSpan(errorColor),
          0,
          inputText.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // --- Result Field Error Handling ---
    Editable resultText = resultEditText.getText();
    if (resultText != null && resultText.length() > 0) {
      resultLayout.setError(resultErrorText);
      resultLayout.setBoxStrokeErrorColor(errorColorStateList);
      resultLayout.setErrorTextColor(errorColorStateList);
      resultLayout.setErrorIconTintList(errorColorStateList);

      resultText.setSpan(
          new ForegroundColorSpan(errorColor),
          0,
          resultText.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  public void logError(String tag, String message) {
    Log.e(tag, message);
  }
}
