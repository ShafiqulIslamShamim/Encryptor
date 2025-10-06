package com.decryptor.encryptor;

import java.util.HashSet;
import java.util.Set;

/**
 * A flexible string input filter utility.
 *
 * <p>Example: String result = new InputFilter.Builder() .allowHex() .allowOperators("+", "-")
 * .allowSpaces() .build() .filter("A1 +B@#3"); // -> "A1 +B3"
 */
public class InputFilter {

  private final String allowedPattern;

  private InputFilter(String allowedPattern) {
    this.allowedPattern = allowedPattern;
  }

  public String filter(String input) {
    if (input == null) return "";
    return input.replaceAll("[^" + allowedPattern + "]", "");
  }

  // -------------------------------------------------------------------------
  // Builder Inner Class
  // -------------------------------------------------------------------------
  public static class Builder {
    private final Set<String> allowedChars = new HashSet<>();

    // ------------------------- Basic Sets -------------------------
    public Builder allowDigits() {
      allowedChars.add("0-9");
      return this;
    }

    public Builder allowLetters() {
      allowedChars.add("a-zA-Z");
      return this;
    }

    public Builder allowSpaces() {
      allowedChars.add(" ");
      return this;
    }

    public Builder allowUnderscore() {
      allowedChars.add("_");
      return this;
    }

    public Builder allowDash() {
      allowedChars.add("-");
      return this;
    }

    // ------------------------- Number Systems -------------------------
    public Builder allowBinary() {
      allowedChars.add("01");
      return this;
    }

    public Builder allowOctal() {
      allowedChars.add("0-7");
      return this;
    }

    public Builder allowDecimal() {
      allowedChars.add("0-9");
      return this;
    }

    public Builder allowHex() {
      allowedChars.add("0-9a-fA-F");
      return this;
    }

    public Builder allowHex0x() {
      allowedChars.add("0-9a-fA-FxX");
      return this;
    }

    // ------------------------- Math Operators -------------------------
    public Builder allowOperators(String... ops) {
      for (String op : ops) {
        // escape regex special chars
        allowedChars.add("\\" + op);
      }
      return this;
    }

    // ------------------------- Custom -------------------------
    public Builder allowCustom(String regexRange) {
      allowedChars.add(regexRange);
      return this;
    }

    // ------------------------- Build -------------------------
    public InputFilter build() {
      StringBuilder pattern = new StringBuilder();
      for (String s : allowedChars) pattern.append(s);
      return new InputFilter(pattern.toString());
    }
  }
}
