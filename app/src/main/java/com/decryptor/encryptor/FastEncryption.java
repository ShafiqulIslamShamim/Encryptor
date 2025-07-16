package com.decryptor.encryptor;

import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FastEncryption {
  private static final String ALGORITHM = "AES";

  public static SecretKeySpec getSecreteKey(String str) {
    try {
      return new SecretKeySpec(
          Arrays.copyOf(
              MessageDigest.getInstance("SHA-1").digest(str.getBytes(StandardCharsets.UTF_8)), 16),
          ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getStrForDecryption(String str, String str2) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(2, getSecreteKey(str2));
      return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
    } catch (Exception e) {
      Log.e("Error while decrypting: ", "" + e.toString());
      return null;
    }
  }

  public static String getStrForEncryption(String str, String str2) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(1, getSecreteKey(str2));
      return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
    } catch (Exception e) {
      Log.e("Error while encrypting: ", "" + e.toString());
      return null;
    }
  }
}
