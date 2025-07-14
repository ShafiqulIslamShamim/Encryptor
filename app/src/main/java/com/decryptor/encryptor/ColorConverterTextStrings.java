package com.decryptor.encryptor;

import android.graphics.Color;

/** Utility class for color code conversions and other operations. */
public class ColorConverterTextStrings {
  /** Converts a hexadecimal string to a #AARRGGBB color code. */
  public static String a(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    String sign = "";
    if (str.startsWith("-")) {
      str = str.substring(1);
      sign = "-";
    }
    if (str.startsWith("0x")) {
      str = str.substring(2);
    }
    if (!str.matches("[0-9A-Fa-f]+")) {
      return null;
    }
    try {
      int value = Integer.parseInt(sign + str, 16);
      return String.format("#%08x", value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** Converts a #RRGGBB or #AARRGGBB color code to a 0xAARRGGBB hex string. */
  public static String b(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    if (!str.startsWith("#")) {
      str = "#" + str;
    }
    if (!str.matches("#[0-9A-Fa-f]{6}|[0-9A-Fa-f]{8}")) {
      return null;
    }
    try {
      int color = Color.parseColor(str);
      return String.format("0x%08x", color);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  // Other methods (e.g., a(List<String>, String), a(int)) go here

  public static String c(String str) {
    String str2 = "";
    if (str.startsWith("-")) {
      str = str.substring(1);
      str2 = "-";
    }
    if (str.startsWith("0x")) {
      str = str.substring(2);
    }
    return String.format(
        "#%08x",
        new Integer(
            Integer.parseInt(new StringBuffer().append(str2).append(str).toString(), 16) & (-1)));
  }

  public static String d(String str) {
    if (!str.startsWith("#")) {
      str = new StringBuffer().append("#").append(str).toString();
    }
    int parseColor = Color.parseColor(str);
    if (Color.alpha(parseColor) >= 128) {
      return new StringBuffer()
          .append("-0x")
          .append(Integer.toHexString(parseColor * (-1)))
          .toString();
    }
    return new StringBuffer().append("0x").append(Integer.toHexString(parseColor)).toString();
  }
}
