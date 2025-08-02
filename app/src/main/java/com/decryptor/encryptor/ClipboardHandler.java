package com.decryptor.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;

public class ClipboardHandler {
  private static final String TAG = "ClipboardHandler";
  private final Context context;

  public ClipboardHandler(Context context) {
    this.context = context;
  }

  public void copyText(TextInputEditText editText, String label) {
    ClipboardManager clipboard =
        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    if (clipboard != null) {
      ClipData clip = ClipData.newPlainText(label, editText.getText().toString());
      clipboard.setPrimaryClip(clip);
      int copiedStringId =
          context.getResources().getIdentifier("copied", "string", context.getPackageName());
      new ErrorHandler(context).showToast(copiedStringId);
    }
  }

  public void pasteText(TextInputEditText editText) {
    ClipboardManager clipboard =
        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    if (clipboard != null && clipboard.hasPrimaryClip()) {
      ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
      if (item.getText() != null) {
        editText.setText(item.getText().toString());
      } else {
        new ErrorHandler(context).showToast("Clipboard is empty");
      }
    } else {
      new ErrorHandler(context).showToast("Clipboard is empty");
    }
  }
}
