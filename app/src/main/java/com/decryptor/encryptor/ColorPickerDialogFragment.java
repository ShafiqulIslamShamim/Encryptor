package com.decryptor.encryptor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ColorPickerDialogFragment extends DialogFragment {

  private int initialColor;
  private int selectedColor;
  private OnColorSelectedListener listener;

  private CustomColorPickerView colorPickerView;
  private View oldColorPreview, newColorPreview;
  private EditText hexEditText;
  private EditText smaliEditText;

  private TextWatcher hexWatcher;
  private TextWatcher smaliWatcher;

  public interface OnColorSelectedListener {
    void onColorSelected(int color);
  }

  public ColorPickerDialogFragment(int initialColor, OnColorSelectedListener listener) {
    this.initialColor = initialColor;
    this.listener = listener;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_color_picker, null);

    colorPickerView = dialogView.findViewById(R.id.colorPickerView);
    oldColorPreview = dialogView.findViewById(R.id.oldColorPreview);
    newColorPreview = dialogView.findViewById(R.id.newColorPreview);
    hexEditText = dialogView.findViewById(R.id.hexEditText);
    smaliEditText = dialogView.findViewById(R.id.smaliEditText);

    selectedColor = initialColor;
    colorPickerView.setColor(initialColor);
    applyColorPreviewStyle(requireContext(), oldColorPreview, initialColor);
    applyColorPreviewStyle(requireContext(), newColorPreview, initialColor);

    String initialHex = String.format("#%08X", initialColor);
    hexEditText.setText(initialHex);
    smaliEditText.setText(ColorConverterTextStrings.d(initialHex));

    colorPickerView.setOnColorChangedListener(
        color -> {
          selectedColor = color;
          applyColorPreviewStyle(requireContext(), newColorPreview, selectedColor);

          String hex = String.format("#%08X", selectedColor);
          String smali = ColorConverterTextStrings.d(hex);

          if (!hex.equalsIgnoreCase(hexEditText.getText().toString())) {
            hexEditText.removeTextChangedListener(hexWatcher);
            hexEditText.setText(hex);
            hexEditText.addTextChangedListener(hexWatcher);
          }

          if (!smali.equalsIgnoreCase(smaliEditText.getText().toString())) {
            smaliEditText.removeTextChangedListener(smaliWatcher);
            smaliEditText.setText(smali);
            smaliEditText.addTextChangedListener(smaliWatcher);
          }
        });

    hexWatcher =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            try {
              String hex = s.toString();
              int color = Color.parseColor(hex);

              selectedColor = color;
              colorPickerView.setColor(color);
              applyColorPreviewStyle(requireContext(), newColorPreview, color);

              String smali = ColorConverterTextStrings.d(hex);
              if (!smali.equalsIgnoreCase(smaliEditText.getText().toString())) {
                smaliEditText.removeTextChangedListener(smaliWatcher);
                smaliEditText.setText(smali);
                smaliEditText.addTextChangedListener(smaliWatcher);
              }

            } catch (IllegalArgumentException ignored) {
            }
          }
        };
    hexEditText.addTextChangedListener(hexWatcher);

    smaliWatcher =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            String hex = ColorConverterTextStrings.c(s.toString());
            if (hex != null) {
              try {
                int color = Color.parseColor(hex);

                selectedColor = color;
                colorPickerView.setColor(color);
                applyColorPreviewStyle(requireContext(), newColorPreview, color);

                if (!hex.equalsIgnoreCase(hexEditText.getText().toString())) {
                  hexEditText.removeTextChangedListener(hexWatcher);
                  hexEditText.setText(hex);
                  hexEditText.addTextChangedListener(hexWatcher);
                }

              } catch (IllegalArgumentException ignored) {
              }
            }
          }
        };
    smaliEditText.addTextChangedListener(smaliWatcher);

    builder
        .setView(dialogView)
        .setCustomTitle(DialogUtils.createStyledDialogTitle(requireContext(), "Select a color"))
        .setPositiveButton(
            "OK",
            (dialog, which) -> {
              if (listener != null) {
                listener.onColorSelected(selectedColor);
              }
            })
        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

    return builder.create();
  }

  // ✅ Apply checkerboard background + overlay + border (programmatically)
  public static void applyColorPreviewStyle(Context context, View view, int foregroundColor) {

    int borderWidthPx = dpToPx(1, context);

    // Get dynamic Material colors
    int lightColor =
        MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorSurfaceVariant, Color.LTGRAY);
    int darkColor =
        MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorOutline, Color.DKGRAY);
    int tileSize = dpToPx(6, context);

    // Create checkerboard bitmap
    Bitmap checkerBitmap = Bitmap.createBitmap(tileSize * 2, tileSize * 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(checkerBitmap);
    Paint light = new Paint();
    Paint dark = new Paint();
    light.setColor(lightColor);
    dark.setColor(darkColor);

    canvas.drawRect(0, 0, tileSize, tileSize, light);
    canvas.drawRect(tileSize, tileSize, tileSize * 2, tileSize * 2, light);
    canvas.drawRect(tileSize, 0, tileSize * 2, tileSize, dark);
    canvas.drawRect(0, tileSize, tileSize, tileSize * 2, dark);

    BitmapShader shader =
        new BitmapShader(checkerBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

    Drawable composedDrawable =
        new Drawable() {
          final Paint checkerPaint = new Paint();
          final Paint overlayPaint = new Paint();
          final Paint borderPaint = new Paint();

          {
            checkerPaint.setShader(shader);
            overlayPaint.setColor(foregroundColor);

            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(darkColor);
            borderPaint.setStrokeWidth(borderWidthPx);
            borderPaint.setAntiAlias(true);
          }

          @Override
          public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();

            // Checkerboard background
            canvas.drawRect(bounds, checkerPaint);

            // Overlay color with alpha
            canvas.drawRect(bounds, overlayPaint);

            // Solid border (no corner radius)
            RectF borderRect =
                new RectF(
                    bounds.left + borderWidthPx / 2f,
                    bounds.top + borderWidthPx / 2f,
                    bounds.right - borderWidthPx / 2f,
                    bounds.bottom - borderWidthPx / 2f);
            canvas.drawRect(borderRect, borderPaint);
          }

          @Override
          public void setAlpha(int alpha) {}

          @Override
          public void setColorFilter(@Nullable ColorFilter colorFilter) {}

          @Override
          public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
          }
        };

    view.setBackground(composedDrawable);
  }

  private static int dpToPx(float dp, Context context) {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
  }
}
