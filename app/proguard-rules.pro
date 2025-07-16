# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Preserve SecureCrypto class and its methods
-keep class com.decryptor.encryptor.SecureCrypto {
    public static java.lang.String encryptData(java.lang.String, java.lang.String);
    public static java.lang.String decryptData(java.lang.String, java.lang.String);
}

# Preserve FastEncryption class and its methods
-keep class com.decryptor.encryptor.FastEncryption {
    public static java.lang.String getStrForEncryption(java.lang.String, java.lang.String);
    public static java.lang.String getStrForDecryption(java.lang.String, java.lang.String);
}

# Optional: Keep the EncryptionUtils class (if used in Java layer too)
-keep class com.decryptor.encryptor.EncryptionUtils {
    <methods>;
}