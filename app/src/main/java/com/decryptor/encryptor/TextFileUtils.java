package com.decryptor.encryptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextFileUtils {

  private static String tempTextToSave = "";
  private static String tempFileName = "";

  /** Launches a Material dialog for entering filename, then triggers SAF create document flow. */
  public static void saveTextToFile(
      Activity activity, ActivityResultLauncher<Intent> createFileLauncher, String textToSave) {
    tempTextToSave = textToSave;

    // Default auto-generated file name
    String defaultName =
        "Encryptor_Export_"
            + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date())
            + ".txt";

    // Input field
    final EditText input = new EditText(activity);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    input.setText(defaultName);
    input.setSelection(defaultName.length());

    // Optional styling for better layout
    LinearLayout container = new LinearLayout(activity);
    int padding = (int) (16 * activity.getResources().getDisplayMetrics().density);
    container.setPadding(padding, padding, padding, padding);
    container.addView(input);

    new MaterialAlertDialogBuilder(activity)
        //  .setTitle("Save As")
        .setCustomTitle(DialogUtils.createStyledDialogTitle(activity, "Save As"))
        .setMessage("Rename the file if needed:")
        .setView(container)
        .setPositiveButton(
            "Save",
            (dialog, which) -> {
              tempFileName = input.getText().toString().trim();
              if (!tempFileName.endsWith(".txt")) {
                tempFileName += ".txt";
              }
              launchSafCreateFile(createFileLauncher, tempFileName);
            })
        .setNegativeButton("Cancel", null)
        .show();
  }

  private static void launchSafCreateFile(
      ActivityResultLauncher<Intent> launcher, String fileName) {
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_TITLE, fileName);
    launcher.launch(intent);
  }

  public static void handleFileSaveResult(Activity activity, Intent data) {
    if (data != null) {
      Uri uri = data.getData();
      try (OutputStream out = activity.getContentResolver().openOutputStream(uri)) {
        out.write(tempTextToSave.getBytes());
        Toast.makeText(activity, "Saved: " + tempFileName, Toast.LENGTH_LONG).show();
      } catch (Exception e) {
        Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
      }
    } else {
      Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
    }
  }

  public static void shareText(Context context, String title, String message) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_SUBJECT, title);
    intent.putExtra(Intent.EXTRA_TEXT, message);

    context.startActivity(Intent.createChooser(intent, "Share via"));
  }

  public static void readTextFromUri(
      @Nullable Uri uri, TextInputEditText editTextContent, Activity activity) {
    if (uri == null) return;
    try (InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

      StringBuilder textBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        textBuilder.append(line).append('\n');
      }

      editTextContent.requestFocus();
      //  conversionManager.setProgrammaticTextChange(true);
      editTextContent.setText(textBuilder.toString());
      //  conversionManager.setProgrammaticTextChange(false);

    } catch (Exception e) {
      Toast.makeText(activity, "Failed to read file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
      e.printStackTrace();
    }
  }
}
