package com.decryptor.encryptor;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureCrypto {
  private static final int GCM_TAG_LENGTH = 128; // in bits
  private static final int SALT_LENGTH = 16; // 16-byte salt
  private static final int IV_LENGTH = 12; // 12-byte IV for GCM
  private static final int KEY_LENGTH = 256; // 256-bit key for AES-256
  private static final int ITERATION_COUNT = 1000; // Reduced for performance
  private static final int CHUNK_SIZE = 16384; // 16KB chunks for large text
  private static final SecureRandom secureRandom = new SecureRandom();
  private static SecretKeySpec cachedKey = null;
  private static byte[] cachedSalt = null;

  static {
    secureRandom.nextBytes(new byte[1]); // Pre-initialize SecureRandom to avoid first-call delay
  }

  // Generate AES key from password using PBKDF2
  public static SecretKeySpec getSecretKey(String password, byte[] salt) {
    if (cachedKey != null && Arrays.equals(cachedSalt, salt)) {
      return cachedKey;
    }
    try {
      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
      SecretKeyFactory factory =
          SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); // Use SHA256 for better performance
      byte[] keyBytes = factory.generateSecret(spec).getEncoded();
      cachedKey = new SecretKeySpec(keyBytes, "AES");
      cachedSalt = salt.clone();
      return cachedKey;
    } catch (Exception e) {
      throw new RuntimeException("Failed to derive key", e);
    }
  }

  // Encrypt data using AES-256-GCM with password-derived key
  public static String encryptData(String data, String password) throws Exception {
    byte[] salt = new byte[SALT_LENGTH];
    secureRandom.nextBytes(salt);
    SecretKeySpec aesKey = getSecretKey(password, salt); // Fixed: Complete variable name
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

    List<byte[]> encryptedChunks = new ArrayList<>();
    byte[] dataBytes = data.getBytes("UTF-8");
    int offset = 0;

    while (offset < dataBytes.length) {
      int length = Math.min(CHUNK_SIZE, dataBytes.length - offset);
      byte[] chunk = new byte[length];
      System.arraycopy(dataBytes, offset, chunk, 0, length);
      offset += length;

      byte[] iv = new byte[IV_LENGTH];
      secureRandom.nextBytes(iv);
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

      byte[] encryptedBytes = cipher.doFinal(chunk);
      byte[] combinedChunk = new byte[IV_LENGTH + encryptedBytes.length];
      System.arraycopy(iv, 0, combinedChunk, 0, IV_LENGTH);
      System.arraycopy(encryptedBytes, 0, combinedChunk, IV_LENGTH, encryptedBytes.length);
      encryptedChunks.add(combinedChunk);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(salt);
    for (byte[] chunk : encryptedChunks) {
      outputStream.write(chunk);
    }

    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
  }

  // Decrypt data using AES-256-GCM with password-derived key
  public static String decryptData(String encryptedData, String password) throws Exception {
    byte[] decodedData = Base64.decode(encryptedData, Base64.DEFAULT);

    // Extract salt
    byte[] salt = new byte[SALT_LENGTH];
    System.arraycopy(decodedData, 0, salt, 0, SALT_LENGTH);

    // Derive AES key
    SecretKeySpec aesKey = getSecretKey(password, salt);

    // Extract and decrypt chunks
    List<byte[]> decryptedChunks = new ArrayList<>();
    int offset = SALT_LENGTH;

    while (offset < decodedData.length) {
      if (offset + IV_LENGTH > decodedData.length) {
        throw new IllegalArgumentException("Invalid encrypted data format");
      }
      byte[] iv = new byte[IV_LENGTH];
      System.arraycopy(decodedData, offset, iv, 0, IV_LENGTH);
      offset += IV_LENGTH;

      int cipherTextLength =
          Math.min(CHUNK_SIZE + 16, decodedData.length - offset); // +16 for GCM tag
      if (cipherTextLength <= 0) {
        throw new IllegalArgumentException("Invalid ciphertext length");
      }
      byte[] cipherText = new byte[cipherTextLength];
      System.arraycopy(decodedData, offset, cipherText, 0, cipherTextLength);
      offset += cipherTextLength;

      // Initialize cipher for decryption
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

      // Decrypt chunk
      byte[] decryptedBytes = cipher.doFinal(cipherText);
      decryptedChunks.add(decryptedBytes);
    }

    // Calculate total length and combine decrypted chunks
    int totalLength = 0;
    for (byte[] chunk : decryptedChunks) {
      totalLength += chunk.length;
    }
    byte[] combinedDecrypted = new byte[totalLength];
    int currentOffset = 0;
    for (byte[] chunk : decryptedChunks) {
      System.arraycopy(chunk, 0, combinedDecrypted, currentOffset, chunk.length);
      currentOffset += chunk.length;
    }

    return new String(combinedDecrypted, "UTF-8");
  }

  /*
  // Main method for testing
  public static void main(String[] args) {
      try {
          String password = "MySecurePassword123";
          // Create a large text by repeating a message
          String message = "Hello, friend! This is a very long secret message...";
          StringBuilder largeMessage = new StringBuilder();
          for (int i = 0; i < 100; i++) {
              largeMessage.append(message);
          }

          // Encrypt message
          String encryptedMessage = encryptData(largeMessage.toString(), password);
          System.out.println("Encrypted: " + encryptedMessage);

          // Decrypt message
          String decryptedMessage = decryptData(encryptedMessage, password);
          System.out.println("Decrypted: " + decryptedMessage);

          // Verify if decrypted message matches original
          if (largeMessage.toString().equals(decryptedMessage)) {
              System.out.println("Test passed: Decrypted message matches original.");
          } else {
              System.out.println("Test failed: Decrypted message does not match original.");
          }

      } catch (Exception e) {
          System.err.println("Error during test: " + e.getMessage());
          e.printStackTrace();
      }
  }
  */
}
