package com.decryptor.encryptor;

public class EncryptionUtils {
  static {
    System.loadLibrary("native-lib");
  }

  public static native String complexEncryptDeta(String input, String alias);

  public static native String complexDecryptDeta(String result, String alias);

  public static native String easyEncryptDeta(String input, String alias);

  public static native String easyDecryptDeta(String result, String alias);
}
