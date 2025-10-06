package com.decryptor.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NumberSystemUtils {

  // ✅ Converts decimal string safely
  public static String fromDecimal(String input) {
    try {
      long value = Long.parseLong(input);
      return String.valueOf(value);
    } catch (NumberFormatException e) {
      return "Invalid Decimal";
    }
  }

  // ✅ Converts binary to decimal
  public static String fromBinary(String input) {
    try {
      if (input.startsWith("-")) {
        return "-" + Long.parseLong(input.substring(1), 2);
      }
      return String.valueOf(Long.parseLong(input, 2));
    } catch (NumberFormatException e) {
      return "Invalid Binary";
    }
  }

  // ✅ Converts octal to decimal
  public static String fromOctal(String input) {
    try {
      if (input.startsWith("-")) {
        return "-" + Long.parseLong(input.substring(1), 8);
      }
      return String.valueOf(Long.parseLong(input, 8));
    } catch (NumberFormatException e) {
      return "Invalid Octal";
    }
  }

  // ✅ Converts hexadecimal to decimal (supports with/without 0x)
  public static String fromHex(String input) {
    try {
      String temp = input.trim();
      boolean isNegative = temp.startsWith("-");
      if (isNegative) temp = temp.substring(1);
      if (temp.startsWith("0x") || temp.startsWith("0X")) {
        temp = temp.substring(2);
      }
      long value = Long.parseLong(temp, 16);
      return (isNegative ? "-" : "") + value;
    } catch (NumberFormatException e) {
      return "Invalid Hex";
    }
  }

  // ✅ Shows multi-format results in resultEditText (copyable)
  public static void setCopyableResults(
      final TextView tv, String decimal, String binary, String octal, String hex) {

    tv.setTypeface(Typeface.MONOSPACE);
    tv.setMovementMethod(LinkMovementMethod.getInstance());

    String labelDec = "Decimal: ";
    String labelBin = "Binary:  ";
    String labelOct = "Octal:   ";
    String labelHex = "Hex:     ";

    String text =
        labelDec + decimal + "\n" + labelBin + binary + "\n" + labelOct + octal + "\n" + labelHex
            + hex;

    SpannableStringBuilder ssb = new SpannableStringBuilder(text);

    attachCopyableSpan(tv.getContext(), ssb, text, labelDec, decimal);
    attachCopyableSpan(tv.getContext(), ssb, text, labelBin, binary);
    attachCopyableSpan(tv.getContext(), ssb, text, labelOct, octal);
    attachCopyableSpan(tv.getContext(), ssb, text, labelHex, hex);

    tv.setText(ssb);
  }

  // ✅ Adds clickable spans for copy-to-clipboard behavior
  private static void attachCopyableSpan(
      final Context ctx,
      SpannableStringBuilder ssb,
      String fullText,
      String label,
      final String value) {
    int start = fullText.indexOf(label);
    if (start < 0) return;
    start += label.length();
    int end = start + value.length();
    if (end > ssb.length()) return;

    ClickableSpan span =
        new ClickableSpan() {
          @Override
          public void onClick(View widget) {
            ClipboardManager cm =
                (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("number", value);
            cm.setPrimaryClip(clip);
            Toast.makeText(ctx, "Copied: " + value, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setFakeBoldText(true);
          }
        };

    ssb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}
