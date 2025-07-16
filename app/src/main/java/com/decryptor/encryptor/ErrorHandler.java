package com.decryptor.encryptor;

import android.content.Context;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

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

  public void highlightError(EditText inputEditText, EditText resultEditText) {
    Editable inputText = inputEditText.getText();
    if (inputText.length() > 0) {
      inputText.setSpan(
          new ForegroundColorSpan(android.graphics.Color.RED), 0, inputText.length(), 33);
    }
    Editable resultText = resultEditText.getText();
    if (resultText.length() > 0) {
      resultText.setSpan(
          new ForegroundColorSpan(android.graphics.Color.RED), 0, resultText.length(), 33);
    }
  }

  public void logError(String tag, String message) {
    Log.e(tag, message);
  }
}
