package com.decryptor.encryptor;

import java.math.BigInteger;

public class BinaryCalculator {

  /**
   * Binary expression হিসাব করে (যেমন "101 + 10 - 1 + -11") এবং binary রেজাল্ট রিটার্ন করে।
   *
   * <p>✅ Uses BigInteger for unlimited precision.
   */
  public static String calculate(String input) {
    if (input == null || input.trim().isEmpty()) return "";

    // সব স্পেস রিমুভ
    input = input.replaceAll("\\s+", "");

    // যদি ইনপুট '+' বা '-' দিয়ে শুরু না হয়, তাহলে '+' যোগ করে নিই
    if (!input.startsWith("+") && !input.startsWith("-")) {
      input = "+" + input;
    }

    BigInteger result = BigInteger.ZERO;
    int index = 0;

    // প্রতিটি সেগমেন্ট পার্স করি
    while (index < input.length()) {
      char sign = input.charAt(index++);
      int start = index;

      // পরবর্তী '+' বা '-' পর্যন্ত সংখ্যা বের করি
      while (index < input.length() && input.charAt(index) != '+' && input.charAt(index) != '-') {
        index++;
      }

      String binary = input.substring(start, index);
      binary = sanitizeBinary(binary);
      if (binary.equals("0")) continue;

      try {
        BigInteger value = new BigInteger(binary, 2);
        if (sign == '-') value = value.negate();
        result = result.add(value);
      } catch (NumberFormatException e) {
        return "Error";
      }
    }

    if (result.equals(BigInteger.ZERO)) return "0";

    // রেজাল্ট ফরম্যাট করা (negative হলে - চিহ্নসহ)
    return (result.signum() < 0 ? "-" : "") + result.abs().toString(2);
  }

  /** ইনপুট থেকে শুধুমাত্র 0 ও 1 রাখে */
  private static String sanitizeBinary(String bin) {
    if (bin == null || bin.isEmpty()) return "0";
    bin = bin.replaceAll("[^01]", "");
    if (bin.isEmpty()) return "0";
    return bin;
  }

  @Override
  public String toString() {
    return "BinaryCalculator Utility Class (BigInteger Based)";
  }
}
