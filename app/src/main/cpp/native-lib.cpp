#include <jni.h>
#include <string>
#include <android/log.h>

// Define log tag for Android logging
#define LOG_TAG "JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Encrypt data using the SecureCrypto Java class (complex encryption)
extern "C"
JNIEXPORT jstring JNICALL
Java_com_decryptor_encryptor_EncryptionUtils_complexEncryptDeta(JNIEnv *env, jclass clazz, jstring input, jstring alias) {
    // Find the SecureCrypto Java class
    jclass EncryptionUtilsClass = env->FindClass("com/decryptor/encryptor/SecureCrypto");
    
    // Get the static encryptData method ID with the signature (String, String) -> String
    jmethodID encryptMethod = env->GetStaticMethodID(
        EncryptionUtilsClass, 
        "encryptData",
        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    );

    // Call the encryptData method and return the result
    return (jstring) env->CallStaticObjectMethod(EncryptionUtilsClass, encryptMethod, input, alias);
}

// Decrypt data using the SecureCrypto Java class (complex decryption)
extern "C"
JNIEXPORT jstring JNICALL
Java_com_decryptor_encryptor_EncryptionUtils_complexDecryptDeta(JNIEnv *env, jclass clazz, jstring result, jstring alias) {
    // Find the SecureCrypto Java class
    jclass EncryptionUtilsClass = env->FindClass("com/decryptor/encryptor/SecureCrypto");

    // Get the static decryptData method ID with the signature (String, String) -> String
    jmethodID decryptMethod = env->GetStaticMethodID(
        EncryptionUtilsClass, 
        "decryptData",
        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    );

    // Call the decryptData method and return the result
    return (jstring) env->CallStaticObjectMethod(EncryptionUtilsClass, decryptMethod, result, alias);
}

// Encrypt data using the FastEncryption Java class (easy/fast encryption)
extern "C"
JNIEXPORT jstring JNICALL
Java_com_decryptor_encryptor_EncryptionUtils_easyEncryptDeta(JNIEnv *env, jclass clazz, jstring input, jstring alias) {
    // Find the FastEncryption Java class
    jclass EncryptionUtilsClass = env->FindClass("com/decryptor/encryptor/FastEncryption");

    // Get the static getStrForEncryption method ID with the signature (String, String) -> String
    jmethodID encryptMethod = env->GetStaticMethodID(
        EncryptionUtilsClass, 
        "getStrForEncryption",
        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    );

    // Call the getStrForEncryption method and return the result
    return (jstring) env->CallStaticObjectMethod(EncryptionUtilsClass, encryptMethod, input, alias);
}

// Decrypt data using the FastEncryption Java class (easy/fast decryption)
extern "C"
JNIEXPORT jstring JNICALL
Java_com_decryptor_encryptor_EncryptionUtils_easyDecryptDeta(JNIEnv *env, jclass clazz, jstring result, jstring alias) {
    // Find the FastEncryption Java class
    jclass EncryptionUtilsClass = env->FindClass("com/decryptor/encryptor/FastEncryption");

    // Get the static getStrForDecryption method ID with the signature (String, String) -> String
    jmethodID decryptMethod = env->GetStaticMethodID(
        EncryptionUtilsClass, 
        "getStrForDecryption",
        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    );

    // Call the getStrForDecryption method and return the result
    return (jstring) env->CallStaticObjectMethod(EncryptionUtilsClass, decryptMethod, result, alias);
}