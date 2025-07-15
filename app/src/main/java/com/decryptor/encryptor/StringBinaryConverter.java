package com.decryptor.encryptor;

import java.nio.charset.StandardCharsets;

public class StringBinaryConverter {

  // Convert text to binary using UTF-8 (handles any character)
  public static String textToBinary(String text) {
    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
    StringBuilder binary = new StringBuilder();
    for (byte b : bytes) {
      binary.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
    }
    return binary.toString();
  }

  // Convert binary back to text using UTF-8
  public static String binaryToText(String binary) {
    int byteCount = binary.length() / 8;
    byte[] bytes = new byte[byteCount];
    for (int i = 0; i < byteCount; i++) {
      String byteString = binary.substring(i * 8, (i + 1) * 8);
      bytes[i] = (byte) Integer.parseInt(byteString, 2);
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }

  /*  // Test it
  public static void main(String[] args) {
      String originalText = "Encrypt this: 😊 বাংলা ❤ 中文 عربى 🔒";
      String binary = textToBinary(originalText);
      String recovered = binaryToText(binary);

      System.out.println("Original Text: " + originalText);
      System.out.println("Binary String: " + binary);
      System.out.println("Recovered Text: " + recovered);
  } */
}
