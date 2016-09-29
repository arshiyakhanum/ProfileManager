package com.arshiya.mapsapi.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by akhanumx on 10/25/2015.
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

  @Override public void onCreate() {
    super.onCreate();
    Log.d(TAG, " onCreate");
    mProfileManagerApplication = this;
  }
}
