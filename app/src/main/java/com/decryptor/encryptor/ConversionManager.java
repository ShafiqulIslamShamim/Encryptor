package com.decryptor.encryptor;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.*;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ConversionManager {
  private static final String TAG = "ConversionManager";
  private final AppCompatActivity activity;
  private final TextInputEditText inputEditText;
  private final TextInputEditText resultEditText;

  private final TextInputLayout inputEditTextLayout;
  private final TextInputLayout resultEditTextLayout;
  private final ErrorHandler errorHandler;
  private boolean isProgrammaticTextChange = false;

  public ConversionManager(
      AppCompatActivity activity,
      TextInputEditText inputEditText,
      TextInputEditText resultEditText,
      TextInputLayout inputEditTextLayout,
      TextInputLayout resultEditTextLayout,
      ErrorHandler errorHandler) {
    this.activity = activity;
    this.inputEditText = inputEditText;
    this.resultEditText = resultEditText;

    this.inputEditTextLayout = inputEditTextLayout;
    this.resultEditTextLayout = resultEditTextLayout;

    this.errorHandler = errorHandler;
  }

  public void performConversion(boolean isInputFocused) {
    int selectedIndex = SharedPrefValues.getValue("converter_select", 27);

    String input = inputEditText.getText().toString();
    String result = resultEditText.getText().toString();

    boolean useHexFormat = SharedPrefValues.getValue("converter_hex", false); // Equivalent to a15
    String str9 = ""; // For handling negative number prefixes

    try {
      switch (selectedIndex) {
        case 0: // Base64
          if (!isInputFocused) {
            String decoded = Base.b(result); // Base64 decode
            if (decoded != null) {
              isProgrammaticTextChange = true;
              inputEditText.setText(decoded);
              isProgrammaticTextChange = false;
            } else {
              errorHandler.highlightError(
                  inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
              Log.e(TAG, "Base64 decode failed for input: " + result);
            }
          } else {
            String encoded = Base.a(input); // Base64 encode
            if (encoded != null) {
              isProgrammaticTextChange = true;
              resultEditText.setText(encoded);
              isProgrammaticTextChange = false;
            } else {
              errorHandler.highlightError(
                  inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
              Log.e(TAG, "Base64 encode failed for input: " + input);
            }
          }
          break;

        case 1: // Custom conversion 1 (ConvertTextStrings.m/ConvertTextStrings.n)
          if (!isInputFocused) {
            String decoded = ConvertTextStrings.m(result);
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded = ConvertTextStrings.n(input);
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 2: // Custom conversion 2 (ConvertTextStrings.b/ConvertTextStrings.a)
          if (!isInputFocused) {
            String decoded = ConvertTextStrings.b(result);
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded = ConvertTextStrings.a(input);
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 3: // MD5
          String md5 = ConvertTextStrings.a(input, "MD5");
          if (md5 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(md5);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "MD5 hash failed for input: " + input);
          }
          break;

        case 4: // SHA-1
          String sha1 = ConvertTextStrings.a(input, "SHA-1");
          if (sha1 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(sha1);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "SHA-1 hash failed for input: " + input);
          }
          break;

        case 5: // SHA-224
          String sha224 = ConvertTextStrings.a(input, "SHA-224");
          if (sha224 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(sha224);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "SHA-224 hash failed for input: " + input);
          }
          break;

        case 6: // SHA-256
          String sha256 = ConvertTextStrings.a(input, "SHA-256");
          if (sha256 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(sha256);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "SHA-256 hash failed for input: " + input);
          }
          break;

        case 7: // SHA-384
          String sha384 = ConvertTextStrings.a(input, "SHA-384");
          if (sha384 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(sha384);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "SHA-384 hash failed for input: " + input);
          }
          break;

        case 8: // SHA-512
          String sha512 = ConvertTextStrings.a(input, "SHA-512");
          if (sha512 != null) {
            isProgrammaticTextChange = true;
            resultEditText.setText(sha512);
            isProgrammaticTextChange = false;
          } else {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            Log.e(TAG, "SHA-512 hash failed for input: " + input);
          }
          break;

        case 9: // UTF-8
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "UTF-8");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("UTF-8")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("UTF-8"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 10: // UTF-16
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "UTF-16");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("UTF-16")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("UTF-16"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 11: // UTF-16LE
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "UTF-16LE");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("UTF-16LE")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("UTF-16LE"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 12: // UTF-16BE
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "UTF-16BE");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("UTF-16BE")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("UTF-16BE"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 13: // US-ASCII
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "US-ASCII");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("US-ASCII")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("US-ASCII"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 14: // ISO-8859-1
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "ISO-8859-1");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("ISO-8859-1"))
                        .replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("ISO-8859-1"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 15: // CP1251
          if (!isInputFocused) {
            byte[] bytes =
                useHexFormat
                    ? ConvertTextStrings.l(result.replaceAll("[^A-Fa-f0-9]", ""))
                    : ConvertTextStrings.a(result.split("\n"));
            String decoded = new String(bytes, "CP1251");
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                useHexFormat
                    ? ConvertTextStrings.a(input.getBytes("CP1251")).replaceAll("..(?!$)", "$0 ")
                    : ConvertTextStrings.a(input.getBytes("CP1251"), "\n");
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 16: // Binary
          if (!isInputFocused) {
            if (result.startsWith("-")) {
              result = result.substring(1);
              str9 = "-";
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(String.valueOf(Long.parseLong(result, 2)))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            if (input.startsWith("-")) {
              input = input.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append(Long.toBinaryString(Long.parseLong(input)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 17: // Octal
          if (!isInputFocused) {
            if (result.startsWith("-")) {
              result = result.substring(1);
              str9 = "-";
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(String.valueOf(Long.parseLong(result, 8)))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            if (input.startsWith("-")) {
              input = input.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append(Long.toOctalString(Long.parseLong(input)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 18: // Hex (no prefix)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(String.valueOf(Long.parseLong(temp, 16)))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            if (input.startsWith("-")) {
              input = input.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append(Long.toHexString(Long.parseLong(input)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 19: // Hex (with 0x prefix)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(String.valueOf(Long.parseLong(temp, 16)))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            if (input.startsWith("-")) {
              input = input.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append("0x")
                    .append(Long.toHexString(Long.parseLong(input)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 20: // Hex (with 0x prefix, padded)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(String.valueOf(Long.parseLong(temp, 16)))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String temp = input;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            String hexString = Long.toHexString(Long.parseLong(temp));
            for (int length = 8 - hexString.length(); length > 0; length--) {
              hexString = new StringBuffer().append("0").append(hexString).toString();
            }
            String encoded =
                new StringBuffer().append(str9).append("0x").append(hexString).toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 21: // Float (Binary)
          if (!isInputFocused) {
            if (result.startsWith("-")) {
              result = result.substring(1);
              str9 = "-";
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(
                        String.valueOf(
                            Float.intBitsToFloat(new Long(Long.parseLong(result, 2)).intValue())))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            if (input.startsWith("-")) {
              input = input.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append(Long.toBinaryString(Float.floatToIntBits(Float.parseFloat(input))))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 22: // Float (Octal)
          if (!isInputFocused) {
            if (result.startsWith("-")) {
              result = result.substring(1);
              str9 = "-";
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(
                        String.valueOf(
                            Float.intBitsToFloat(new Long(Long.parseLong(result, 8)).intValue())))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String temp = input;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            String encoded =
                new StringBuffer()
                    .append(str9)
                    .append(Long.toOctalString(Float.floatToIntBits(Float.parseFloat(temp))))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 23: // Float (Hex, no prefix)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(
                        String.valueOf(
                            Float.intBitsToFloat(new Long(Long.parseLong(temp, 16)).intValue())))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                String.format("%x", Float.floatToIntBits(Float.parseFloat(input)) & (-1));
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 24: // Float (Hex, with 0x prefix)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(
                        String.valueOf(
                            Float.intBitsToFloat(new Long(Long.parseLong(temp, 16)).intValue())))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                new StringBuffer()
                    .append("0x")
                    .append(
                        String.format("%x", Float.floatToIntBits(Float.parseFloat(input)) & (-1)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 25: // Float (Hex, with 0x prefix, padded)
          if (!isInputFocused) {
            String temp = result;
            if (temp.startsWith("-")) {
              temp = temp.substring(1);
              str9 = "-";
            }
            if (temp.startsWith("0x")) {
              temp = temp.substring(2);
            }
            String decoded =
                new StringBuffer()
                    .append(str9)
                    .append(
                        String.valueOf(
                            Float.intBitsToFloat(new Long(Long.parseLong(temp, 16)).intValue())))
                    .toString();
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded =
                new StringBuffer()
                    .append("0x")
                    .append(
                        String.format("%08x", Float.floatToIntBits(Float.parseFloat(input)) & (-1)))
                    .toString();
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 26: // URL Encode/Decode
          if (!isInputFocused) {
            String decoded = ColorConverterTextStrings.c(result);
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded = ColorConverterTextStrings.d(input);
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 27: //  Text Encode/Decode with private key
          String alias = PasswordManager.getPrivateKey();

          try {

            if (!isInputFocused) {

              String decoded = SecureCrypto.decryptData(result, alias);

              isProgrammaticTextChange = true;
              inputEditText.setText(decoded);
              isProgrammaticTextChange = false;
            } else {

              if (!TextUtils.isEmpty(input)) {

                String encoded = SecureCrypto.encryptData(input, alias);

                isProgrammaticTextChange = true;
                resultEditText.setText(encoded);
                isProgrammaticTextChange = false;
              }
            }

          } catch (Exception e) {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            if (!PasswordManager.isPrivateKeySet() && TextUtils.isEmpty(alias)) {
              PasswordManager.startBiometricAuth(activity, activity, false);
            }
          }

          break;

        case 28: // Color (Hex to Decimal)
          if (!isInputFocused) {
            String decoded = String.format("#%08x", Integer.parseInt(result) & (-1));
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String temp = input;
            if (!temp.startsWith("#")) {
              temp = "#" + temp;
            }
            String encoded = String.valueOf(Color.parseColor(temp));
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 29: // Color (Hex to RGBA)
          if (!isInputFocused) {
            if (!ConvertTextStrings.j(result)) {
              throw new Exception("Invalid RGBA format");
            }
            String decoded = String.format("#%08x", ConvertTextStrings.k(result) & (-1));
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String temp = input;
            if (!temp.startsWith("#")) {
              temp = "#" + temp;
            }
            int parseColor = Color.parseColor(temp);
            String encoded =
                String.format(
                    "%d,%d,%d,%d",
                    Color.alpha(parseColor),
                    Color.red(parseColor),
                    Color.green(parseColor),
                    Color.blue(parseColor));
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        case 30: //  Text Encode/Decode with private key
          String aliastwice = PasswordManager.getPrivateKey();
          try {
            if (!isInputFocused) {

              String decoded = FastEncryption.getStrForDecryption(result, aliastwice);

              isProgrammaticTextChange = true;
              inputEditText.setText(decoded);
              isProgrammaticTextChange = false;
            } else {

              if (!TextUtils.isEmpty(input)) {

                String encoded = FastEncryption.getStrForEncryption(input, aliastwice);

                isProgrammaticTextChange = true;
                resultEditText.setText(encoded);
                isProgrammaticTextChange = false;
              }
            }

          } catch (Exception e) {
            errorHandler.highlightError(
                inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
            if (!PasswordManager.isPrivateKeySet() && TextUtils.isEmpty(aliastwice)) {
              PasswordManager.startBiometricAuth(activity, activity, false);
            }
          }

          break;

        case 31: // Custom conversion 31 (ConvertText to binary)
          if (!isInputFocused) {
            String decoded = StringBinaryConverter.binaryToText(result);
            isProgrammaticTextChange = true;
            inputEditText.setText(decoded);
            isProgrammaticTextChange = false;
          } else {
            String encoded = StringBinaryConverter.textToBinary(input);
            isProgrammaticTextChange = true;
            resultEditText.setText(encoded);
            isProgrammaticTextChange = false;
          }
          break;

        default:
          Log.w(TAG, "Unknown SharedPreferences index: " + selectedIndex);
          break;
      }

    } catch (Exception e) {
      Log.e(TAG, "Conversion error: " + e.getMessage());
      errorHandler.highlightError(
          inputEditText, resultEditText, inputEditTextLayout, resultEditTextLayout);
    }
  }

  public boolean isProgrammaticTextChange() {
    return isProgrammaticTextChange;
  }

  public void setProgrammaticTextChange(boolean value) {
    isProgrammaticTextChange = value;
  }
}
