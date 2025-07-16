package com.decryptor.encryptor;

import android.util.Base64;

public class Base {

  public static String a(String str, String str2) {
    return a(c(str, str2));
  }

  public static String b(String str, String str2) {
    return c(b(str), str2);
  }

  public static String a(String str) {
    try {
      return new String(Base64.encode(str.getBytes(), 2));
    } catch (Exception e) {
      return (String) null;
    }
  }

  public static String b(String str) {
    try {
      return new String(Base64.decode(str.getBytes(), 2));
    } catch (Exception e) {
      return (String) null;
    }
  }

  public static String c(String str, String str2) {
    try {
      if (str == null || str2 == null) {
        return (String) null;
      }
      char[] charArray = str2.toCharArray();
      char[] charArray2 = str.toCharArray();
      int length = charArray2.length;
      int length2 = charArray.length;
      char[] cArr = new char[length];
      for (int i = 0; i < length; i++) {
        cArr[i] = (char) (charArray2[i] ^ charArray[i % length2]);
      }
      return new String(cArr);
    } catch (Exception e) {
      return (String) null;
    }
  }
}
