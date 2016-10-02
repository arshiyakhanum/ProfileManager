package com.arshiya.mapsapi.common;

import android.app.Application;
import android.content.Context;

/**
 * Created by arshiya on 10/25/2015.
 */
public class ProfileManagerApplication extends Application {

  private static final String TAG = ProfileManagerApplication.class.getSimpleName();
  private static ProfileManagerApplication mProfileManagerApplication;

  public ProfileManagerApplication() {
    mProfileManagerApplication = this;
  }

  public static Context getContext() {
    if (null == mProfileManagerApplication) {
      return new ProfileManagerApplication();
    }
    return mProfileManagerApplication;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mProfileManagerApplication = this;

    Logger.enableDebuggingForDebugBuild(this);
    Logger.setLogLevel(Logger.VERBOSE);
  }
}
