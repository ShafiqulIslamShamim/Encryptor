package com.decryptor.encryptor;

import android.app.Application;
import android.content.Context;

public class EncryptorApp extends Application {
  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    AppContext.init(this);
  }
}
