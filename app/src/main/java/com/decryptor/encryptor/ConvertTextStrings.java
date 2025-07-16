package com.decryptor.encryptor;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertTextStrings {
  public static final String a = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  public static final String b = "0123456789ABCDEF";
  private static final Pattern d = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
  public static final String[] c = {
    "{LABEL}",
    "{DEFAULT_LABEL}",
    "{PACKAGE}",
    "{VERSION}",
    "{CODE}",
    "{MINSDK}",
    "{TARGETSDK}",
    "{MINVER}",
    "{TARGETVER}",
    "{MINNAME}",
    "{TARGETNAME}",
    "{ARCH}",
    "{DATE}",
    "{TIME}"
  };

  public static String a(String str) {
    if (str == null) {
      return (String) null;
    }
    byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
    StringBuilder sb = new StringBuilder(bytes.length);
    for (byte b2 : bytes) {
      if (b2 < 97
          ? b2 < 65 ? b2 < 48 ? b2 == 45 || b2 == 46 : b2 <= 57 : b2 <= 90 || b2 == 95
          : b2 <= 122 || b2 == 126) {
        sb.append((char) b2);
      } else {
        sb.append('%').append(b.charAt((b2 >> 4) & 15)).append(b.charAt(b2 & 15));
      }
    }
    return sb.toString();
  }

  public static String b(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    byte[] bytes = new byte[str.length()];
    int byteIndex = 0;

    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c != '%') {
        sb.append(c);
      } else {
        if (i + 2 >= str.length()) {
          return null; // Invalid percent-encoding
        }
        try {
          String hex = str.substring(i + 1, i + 3);
          bytes[byteIndex++] = (byte) Integer.parseInt(hex, 16);
          i += 2;
        } catch (NumberFormatException e) {
          return null; // Invalid hex value
        }
      }
    }
    if (byteIndex > 0) {
      sb.append(new String(bytes, 0, byteIndex, Charset.forName("UTF-8")));
    }
    return sb.toString();
  }

  public static String c(String str) {
    String stringBuffer;
    String str2 = "^";
    int i = 0;
    while (i < str.length()) {
      char charAt = str.charAt(i);
      switch (charAt) {
        case '*':
          stringBuffer = new StringBuffer().append(str2).append(".*").toString();
          break;
        case '.':
          stringBuffer = new StringBuffer().append(str2).append("\\.").toString();
          break;
        case '?':
          stringBuffer = new StringBuffer().append(str2).append('.').toString();
          break;
        case '\\':
          stringBuffer = new StringBuffer().append(str2).append("\\\\").toString();
          break;
        default:
          stringBuffer = new StringBuffer().append(str2).append(charAt).toString();
          break;
      }
      i++;
      str2 = stringBuffer;
    }
    return new StringBuffer().append(str2).append('$').toString();
  }

  public static String d(String str) {
    StringBuilder sb = new StringBuilder();
    boolean z = false;
    for (int i = 0; i < str.length(); i++) {
      char charAt = str.charAt(i);
      if (z) {
        if (charAt == 't') {
          sb.append('\t');
        } else if (charAt == 'b') {
          sb.append('\b');
        } else if (charAt == 'r') {
          sb.append('\r');
        } else if (charAt == 'n') {
          sb.append('\n');
        } else if (charAt == 'f') {
          sb.append('\f');
        } else if (charAt == '\\' || charAt == '\'' || charAt == '\"') {
          sb.append(charAt);
        } else {
          sb.append('\\').append(charAt);
        }
        z = false;
      } else if (charAt == '\\') {
        z = true;
      } else {
        sb.append(charAt);
      }
    }
    return sb.toString();
  }

  public static boolean e(String str) {
    return str.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
  }

  public static String f(String str) {
    return str.replaceAll("\\\\n", "\n")
        .replaceAll("\\\\\"", "\"")
        .replaceAll("\\\\'", "'")
        .replace("&amp;", "&")
        .replace("&lt;", "<");
  }

  public static String a(int i) {
    Random random = new Random();
    StringBuilder sb = new StringBuilder(i);
    for (int i2 = 0; i2 < i; i2++) {
      char charAt = a.charAt(random.nextInt(a.length()));
      while (i2 == 0 && Character.isDigit(charAt)) {
        charAt = a.charAt(random.nextInt(a.length()));
      }
      sb.append(charAt);
    }
    return sb.toString();
  }

  public static boolean a(char c2) {
    return (c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z');
  }

  public static void a(TextView textView, String str) {
    SpannableString spannableString = new SpannableString(str);
    spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), 33);
    textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    textView.setClickable(true);
  }

  /**
   * public static void a(Context context, String str) { if (!o(str)) { try { ((ClipboardManager)
   * context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(context.getString(0x7f0a001d),
   * str)); a(context, 0x7f0a01df); } catch (Exception e) { a(context, 0x7f0a01e3); } } }
   *
   * <p>public static String a(Context context) { ClipboardManager clipboardManager =
   * (ClipboardManager) context.getSystemService("clipboard"); if
   * (clipboardManager.hasPrimaryClip()) { ClipDescription primaryClipDescription =
   * clipboardManager.getPrimaryClipDescription(); ClipData primaryClip =
   * clipboardManager.getPrimaryClip(); if (primaryClipDescription != null && primaryClip != null &&
   * (primaryClipDescription.hasMimeType("text/plain") ||
   * primaryClipDescription.hasMimeType("text/html"))) { return
   * String.valueOf(primaryClip.getItemAt(0).getText()); } } return (String) null; }
   *
   * <p>public static void b(Context context, String str) { int i; int a2; try { switch (av.b) {
   * case 1: i = 0x7f02005a; a2 = h.a(context, 0x7f0e0059); break; case 2: i = 0x7f020059; a2 =
   * h.a(context, 0x7f0e0059); break; default: i = 0x7f020058; a2 = h.a(context, 0x7f0e0058); break;
   * } Drawable drawable = context.getResources().getDrawable(i); View inflate =
   * LayoutInflater.from(context).inflate(0x7f040068, (ViewGroup) null);
   * inflate.setBackground(drawable); TextView textView = (TextView)
   * inflate.findViewById(0x7f0f022e); textView.setTextColor(a2); textView.setText(str); Toast toast
   * = new Toast(context); toast.setDuration(0); toast.setView(inflate); toast.show(); } catch
   * (Exception e) { } }
   *
   * <p>public static void a(Context context, int i) { try { b(context, context.getString(i)); }
   * catch (Exception e) { } }
   *
   * <p>public static void a(Context context, int i, Object... objArr) { try { b(context,
   * context.getString(i, objArr)); } catch (Exception e) { } }
   */

  /** public static String a(long j) { return new SimpleDateFormat(av.A).format(new Date(j)); } */
  public static String a(long j, String str) {
    return new SimpleDateFormat(str).format(new Date(j));
  }

  public static String g(String str) {
    return new SimpleDateFormat(str).format(new Date());
  }

  public static String a(String str, int i) {
    StringBuffer stringBuffer = new StringBuffer();
    Matcher matcher = Pattern.compile("\\{(\\d+)\\}").matcher(str);
    while (matcher.find()) {
      matcher.appendReplacement(
          stringBuffer,
          String.format(
              new StringBuffer()
                  .append(
                      new StringBuffer().append("%0").append(matcher.group(1).length()).toString())
                  .append("d")
                  .toString(),
              new Long(Long.parseLong(matcher.group(1)) + i)));
    }
    matcher.appendTail(stringBuffer);
    return stringBuffer.toString();
  }

  public static void a(Context context, TextView textView, int i) {
    if (Build.VERSION.SDK_INT < 23) {
      textView.setTextAppearance(context, i);
    } else {
      textView.setTextAppearance(i);
    }
  }

  public static String h(String str) {
    return str.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s", " ");
  }

  public static boolean i(String str) {
    return Pattern.compile("[\\\\/:*?\"<>|]+").matcher(str).find();
  }

  public static boolean j(String str) {
    return str.trim()
            .matches(
                new StringBuffer()
                    .append(
                        new StringBuffer()
                            .append(
                                new StringBuffer()
                                    .append(
                                        new StringBuffer()
                                            .append(
                                                new StringBuffer()
                                                    .append(
                                                        new StringBuffer()
                                                            .append("^")
                                                            .append(
                                                                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                                                            .toString())
                                                    .append("\\s?,\\s?")
                                                    .toString())
                                            .append(
                                                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                                            .toString())
                                    .append("\\s?,\\s?")
                                    .toString())
                            .append("([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                            .toString())
                    .append("$")
                    .toString())
        || str.trim()
            .matches(
                new StringBuffer()
                    .append(
                        new StringBuffer()
                            .append(
                                new StringBuffer()
                                    .append(
                                        new StringBuffer()
                                            .append(
                                                new StringBuffer()
                                                    .append(
                                                        new StringBuffer()
                                                            .append(
                                                                new StringBuffer()
                                                                    .append(
                                                                        new StringBuffer()
                                                                            .append("^")
                                                                            .append(
                                                                                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                                                                            .toString())
                                                                    .append("\\s?,\\s?")
                                                                    .toString())
                                                            .append(
                                                                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                                                            .toString())
                                                    .append("\\s?,\\s?")
                                                    .toString())
                                            .append(
                                                "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                                            .toString())
                                    .append("\\s?,\\s?")
                                    .toString())
                            .append("([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")
                            .toString())
                    .append("$")
                    .toString());
  }

  public static int k(String str) {
    int parseInt;
    int parseInt2;
    int parseInt3;
    int parseInt4;
    try {
      String[] split = str.trim().split(",");
      if (split.length == 3) {
        parseInt = 255;
        parseInt2 = Integer.parseInt(split[0].trim());
        parseInt3 = Integer.parseInt(split[1].trim());
        parseInt4 = Integer.parseInt(split[2].trim());
      } else {
        parseInt = Integer.parseInt(split[0].trim());
        parseInt2 = Integer.parseInt(split[1].trim());
        parseInt3 = Integer.parseInt(split[2].trim());
        parseInt4 = Integer.parseInt(split[3].trim());
      }
      return Color.argb(parseInt, parseInt2, parseInt3, parseInt4);
    } catch (Exception e) {
      return -16777216;
    }
  }

  public static String a(String str, String str2) {
    if (str.equals("")) {
      return "";
    }
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(str2);
      messageDigest.reset();
      messageDigest.update(str.getBytes("UTF-8"));
      return a(messageDigest.digest());
    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
      return (String) null;
    }
  }

  public static String a(byte[] bArr) {
    StringBuilder sb = new StringBuilder();
    for (byte b2 : bArr) {
      sb.append(String.format("%02x", new Byte(b2)));
    }
    return sb.toString();
  }

  public static String a(byte[] bArr, String str) {
    StringBuilder sb = new StringBuilder();
    for (byte b2 : bArr) {
      sb.append(
          new StringBuffer()
              .append(
                  new StringBuffer()
                      .append("0x")
                      .append(String.format("%x", new Byte(b2)))
                      .toString())
              .append(str)
              .toString());
    }
    return sb.toString();
  }

  public static byte[] l(String str) {
    String[] split = str.replaceAll("..(?!$)", "$0 ").split(" ");
    byte[] bArr = new byte[split.length];
    for (int i = 0; i < split.length; i++) {
      bArr[i] = (byte) Integer.parseInt(split[i], 16);
    }
    return bArr;
  }

  public static byte[] a(String[] strArr) {
    byte[] bArr = new byte[strArr.length];
    for (int i = 0; i < strArr.length; i++) {
      bArr[i] =
          (byte) Integer.parseInt(strArr[i].trim().substring(2).replaceAll("[^A-Fa-f0-9]", ""), 16);
    }
    return bArr;
  }

  public static String m(String str) {
    Matcher matcher = Pattern.compile("\\\\u([0-9a-f]{4})", 2).matcher(str);
    StringBuffer stringBuffer = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(
          stringBuffer, String.valueOf((char) Integer.parseInt(matcher.group(1), 16)));
    }
    matcher.appendTail(stringBuffer);
    return stringBuffer.toString();
  }

  public static String n(String str) {
    if (!str.equals("")) {
      StringBuilder sb = new StringBuilder();
      for (char c2 : str.toCharArray()) {
        if (c2 >= 128) {
          sb.append("\\u").append(String.format("%04X", new Integer(c2)));
        } else {
          sb.append(c2);
        }
      }
      return sb.toString();
    }
    return str;
  }

  public static void a(EditText editText, CharSequence charSequence) {
    int max = Math.max(editText.getSelectionStart(), 0);
    int max2 = Math.max(editText.getSelectionEnd(), 0);
    editText.getText().replace(Math.min(max, max2), Math.max(max, max2), charSequence);
    editText.requestFocus();
  }

  public static void a(ViewGroup viewGroup, int i) {
    int childCount = viewGroup.getChildCount();
    for (int i2 = 0; i2 < childCount; i2++) {
      View childAt = viewGroup.getChildAt(i2);
      if (childAt instanceof ViewGroup) {
        a((ViewGroup) childAt, i);
      } else if (childAt instanceof TextView) {
        ((TextView) childAt).setTextSize(2, i);
      }
    }
  }

  public static boolean a(Editable editable) {
    return editable == null || o(editable.toString());
  }

  public static boolean a(CharSequence charSequence) {
    return charSequence == null || o(charSequence.toString());
  }

  public static boolean o(String str) {
    return str == null || str.equals("");
  }

  public static String b(CharSequence charSequence) {
    return charSequence == null ? "" : charSequence.toString();
  }

  public static void a(List<String> list, List<String> list2) {
    if (list != null && list2 != null) {
      for (String str : list2) {
        if (!list.contains(str)) {
          list.add(str);
        }
      }
    }
  }

  /**
   * public static String a(a aVar, String str) { return a(aVar, str, (List<String>) null); }
   *
   * <p>public static String a(a aVar, String str, List<String> list) { String replace; String
   * replace2; String g; String g2; String replace3; String replace4; String replace5; StringBuffer
   * stringBuffer = new StringBuffer(); List list2 = (List) null; Matcher matcher =
   * Pattern.compile("\\{(LABEL|DEFAULT_LABEL|VERSION|ARCH|DATE|TIME|MINNAME|TARGETNAME)([^\\}]+)\\}").matcher(str);
   * while (matcher.find()) { if (matcher.group(1).equals("LABEL")) { try { replace5 =
   * aVar.h().replaceAll("\\s", matcher.group(2)); } catch (Exception e) { replace5 =
   * aVar.h().replace(" ", matcher.group(2)); } matcher.appendReplacement(stringBuffer, replace5); }
   * else if (matcher.group(1).equals("DEFAULT_LABEL")) { try { replace4 =
   * aVar.l().replaceAll("\\s", matcher.group(2)); } catch (Exception e2) { replace4 =
   * aVar.l().replace(" ", matcher.group(2)); } matcher.appendReplacement(stringBuffer, replace4); }
   * else if (matcher.group(1).equals("VERSION")) { try { replace3 = aVar.n().replaceAll("\\s",
   * matcher.group(2)); } catch (Exception e3) { replace3 = aVar.n().replace(" ", matcher.group(2));
   * } matcher.appendReplacement(stringBuffer, replace3); } else if
   * (matcher.group(1).equals("DATE")) { try { g2 = g("yyyy-MM-dd").replace("-", matcher.group(2));
   * } catch (Exception e4) { g2 = g("yyyy-MM-dd"); } matcher.appendReplacement(stringBuffer, g2); }
   * else if (matcher.group(1).equals("TIME")) { try { g = g("HH-mm-ss").replace("-",
   * matcher.group(2)); } catch (Exception e5) { g = g("HH-mm-ss"); }
   * matcher.appendReplacement(stringBuffer, g); } else if (matcher.group(1).equals("ARCH")) { if
   * (list2 == null) { list2 = aVar.G(); } if (!list2.isEmpty()) {
   * matcher.appendReplacement(stringBuffer, ai.a(list2, matcher.group(2))); } else if (list !=
   * null) { matcher.appendReplacement(stringBuffer, ai.a(list, matcher.group(2))); } else {
   * matcher.appendReplacement(stringBuffer, "universal"); } } else if
   * (matcher.group(1).equals("MINNAME")) { try { replace2 = aVar.v().replaceAll("\\s",
   * matcher.group(2)); } catch (Exception e6) { replace2 = aVar.v().replace(" ", matcher.group(2));
   * } matcher.appendReplacement(stringBuffer, replace2); } else if
   * (matcher.group(1).equals("TARGETNAME")) { try { replace = aVar.w().replaceAll("\\s",
   * matcher.group(2)); } catch (Exception e7) { replace = aVar.w().replace(" ", matcher.group(2));
   * } matcher.appendReplacement(stringBuffer, replace); } } matcher.appendTail(stringBuffer); if
   * (str.indexOf("{ARCH}") >= 0 && list2 == null) { list2 = aVar.G(); if (list2.isEmpty()) { if
   * (list != null) { list2.addAll(list); } else { list2.add("universal"); } } } return
   * stringBuffer.toString().replace("{DATE}", g("yyyy-MM-dd")).replace("{TIME}",
   * g("HH-mm-ss")).replace("{ARCH}", ai.a(list2, ",")).replace("{LABEL}",
   * aVar.h()).replace("{DEFAULT_LABEL}", aVar.l()).replace("{PACKAGE}",
   * aVar.m()).replace("{VERSION}", aVar.n()).replace("{CODE}",
   * String.valueOf(aVar.o())).replace("{MINSDK}", String.valueOf(aVar.r())).replace("{TARGETSDK}",
   * String.valueOf(aVar.t())).replace("{MINVER}", ai.a(aVar.r())).replace("{TARGETVER}",
   * ai.a(aVar.t())).replace("{MINNAME}", aVar.v()).replace("{TARGETNAME}", aVar.w()); }
   */
  public static String c(CharSequence charSequence) {
    return d.matcher(charSequence).replaceAll("\\\\$0");
  }

  public static void a(Editable editable, Class<? extends CharacterStyle> cls) {
    for (CharacterStyle characterStyle :
        (CharacterStyle[]) editable.getSpans(0, editable.length(), cls)) {
      editable.removeSpan(characterStyle);
    }
  }

  /**
   * public static void c(Context context, String str) { try { Intent intent = new
   * Intent("android.intent.action.SEND"); intent.setType("text/plain");
   * intent.putExtra("android.intent.extra.TEXT", str); intent.addFlags(0x10000000);
   * context.startActivity(Intent.createChooser(intent, context.getString(0x7f0a0180))); } catch
   * (Exception e) { a(context, 0x7f0a01e3); } }
   */
  public static void a(TextView textView) {
    textView.setText(p(textView.getText().toString()));
  }

  public static Spanned p(String str) {
    return Build.VERSION.SDK_INT < 24 ? Html.fromHtml(str) : Html.fromHtml(str, 0);
  }

  /**
   * public static String q(String str) { if (!o(str)) { Random random = new Random(); StringBuilder
   * sb = new StringBuilder(); String[] split = str.split("\\."); for (int i = 0; i < split.length;
   * i++) { if (split[i].length() != 0) { if (i > 0 && sb.length() > 0) { sb.append("."); } for
   * (char c2 : split[i].toCharArray()) { if (Character.isDigit(c2)) {
   * sb.append(String.valueOf(ai.a(0, 9))); } else { char charAt =
   * i.p.charAt(random.nextInt(i.p.length())); if (Character.isUpperCase(c2)) { charAt =
   * Character.toUpperCase(charAt); } sb.append(charAt); } } } } return sb.toString(); } return str;
   * }
   */
  public static String r(String str) {
    String stringBuffer =
        new StringBuffer().append("abcdefghijklmnopqrstuvwxyz").append("1234567890").toString();
    char charAt = str.charAt(str.length() - 1);
    if (Character.isUpperCase(charAt)) {
      stringBuffer = stringBuffer.toUpperCase();
    }
    int indexOf = stringBuffer.indexOf(charAt);
    String substring = str.substring(0, str.length() - 1);
    if (indexOf < 0 || indexOf >= stringBuffer.length() - 1) {
      return new StringBuffer().append(substring).append(stringBuffer.substring(0, 1)).toString();
    }
    return new StringBuffer()
        .append(substring)
        .append(stringBuffer.substring(indexOf + 1, indexOf + 2))
        .toString();
  }

  public static String b(String str, int i) {
    StringBuilder sb = new StringBuilder();
    String[] split = str.split("\\s");
    int i2 = 0;
    while (true) {
      if (i2 >= split.length) {
        break;
      }
      if (i2 > 0) {
        sb.append(" ");
      }
      sb.append(split[i2]);
      if (sb.length() <= i || i2 >= split.length - 1) {
        i2++;
      } else {
        sb.append("...");
        break;
      }
    }
    return sb.toString();
  }

  public static String a(String str, String str2, String str3) {
    StringBuilder sb = new StringBuilder(str);
    StringBuilder sb2 = new StringBuilder(str.toLowerCase());
    String lowerCase = str2.toLowerCase();
    int i = 0;
    while (true) {
      int indexOf = sb2.indexOf(lowerCase, i);
      if (indexOf != -1) {
        sb.replace(indexOf, lowerCase.length() + indexOf, str3);
        sb2.replace(indexOf, lowerCase.length() + indexOf, str3);
        i = indexOf + str3.length();
      } else {
        sb2.setLength(0);
        sb2.trimToSize();
        return sb.toString();
      }
    }
  }

  public static String c(String str, int i) {
    int i2;
    int i3;
    int i4 = 0;
    for (int i5 = 0; i5 < str.length(); i5 = i5 + i3 + 1) {
      char charAt = str.charAt(i5);
      if (charAt <= 127) {
        i2 = 1;
        i3 = 0;
      } else if (charAt <= 2047) {
        i2 = 2;
        i3 = 0;
      } else if (charAt <= 55295) {
        i2 = 3;
        i3 = 0;
      } else if (charAt <= 57343) {
        i2 = 4;
        i3 = 1;
      } else {
        i2 = 3;
        i3 = 0;
      }
      if (i4 + i2 > i) {
        return str.substring(0, i5);
      }
      i4 += i2;
    }
    return str;
  }

  public static void a(EditText editText, int i) {
    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(i)});
  }

  /**
   * public static SpannableString a(SpannableString spannableString) { HashSet hashSet = new
   * HashSet(); Matcher matcher = Pattern.compile(String.format(Locale.US, "#[0-9a-fA-F]{%d}", new
   * Integer(8))).matcher(spannableString); while (matcher.find()) { try { int parseColor =
   * Color.parseColor(matcher.group(0)); int i = b.a(parseColor) ? -1 : -16777216; hashSet.add(new
   * Integer(matcher.start())); spannableString.setSpan(new BackgroundColorSpan(parseColor),
   * matcher.start(), matcher.end(), 33); spannableString.setSpan(new ForegroundColorSpan(i),
   * matcher.start(), matcher.end(), 33); } catch (Exception e) { } } Matcher matcher2 =
   * Pattern.compile(String.format(Locale.US, "#[0-9a-fA-F]{%d}", new
   * Integer(6))).matcher(spannableString); while (matcher2.find()) { if (!hashSet.contains(new
   * Integer(matcher2.start()))) { try { int parseColor2 = Color.parseColor(matcher2.group(0)); int
   * i2 = b.a(parseColor2) ? -1 : -16777216; hashSet.add(new Integer(matcher2.start()));
   * spannableString.setSpan(new BackgroundColorSpan(parseColor2), matcher2.start(), matcher2.end(),
   * 33); spannableString.setSpan(new ForegroundColorSpan(i2), matcher2.start(), matcher2.end(),
   * 33); } catch (Exception e2) { } } } Matcher matcher3 = Pattern.compile(String.format(Locale.US,
   * "#[0-9a-fA-F]{%d}", new Integer(4))).matcher(spannableString); while (matcher3.find()) { if
   * (!hashSet.contains(new Integer(matcher3.start()))) { try { String group = matcher3.group(0);
   * StringBuilder sb = new StringBuilder(); for (int i3 = 1; i3 < 5; i3++) {
   * sb.append(group.charAt(i3)).append(group.charAt(i3)); } int parseColor3 = Color.parseColor(new
   * StringBuffer().append("#").append(sb.toString()).toString()); int i4 = b.a(parseColor3) ? -1 :
   * -16777216; hashSet.add(new Integer(matcher3.start())); spannableString.setSpan(new
   * BackgroundColorSpan(parseColor3), matcher3.start(), matcher3.end(), 33);
   * spannableString.setSpan(new ForegroundColorSpan(i4), matcher3.start(), matcher3.end(), 33); }
   * catch (Exception e3) { } } } Matcher matcher4 = Pattern.compile(String.format(Locale.US,
   * "#[0-9a-fA-F]{%d}", new Integer(3))).matcher(spannableString); while (matcher4.find()) { if
   * (!hashSet.contains(new Integer(matcher4.start()))) { try { String group2 = matcher4.group(0);
   * StringBuilder sb2 = new StringBuilder(); for (int i5 = 1; i5 < 4; i5++) {
   * sb2.append(group2.charAt(i5)).append(group2.charAt(i5)); } int parseColor4 =
   * Color.parseColor(new StringBuffer().append("#").append(sb2.toString()).toString()); int i6 =
   * b.a(parseColor4) ? -1 : -16777216; spannableString.setSpan(new
   * BackgroundColorSpan(parseColor4), matcher4.start(), matcher4.end(), 33);
   * spannableString.setSpan(new ForegroundColorSpan(i6), matcher4.start(), matcher4.end(), 33); }
   * catch (Exception e4) { } } } return spannableString; }
   */
}
