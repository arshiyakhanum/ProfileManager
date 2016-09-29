package com.arshiya.mapsapi.common;

import android.graphics.Typeface;

/**
 * Created by akhanumx on 11/4/2015.
 */
public class Fonts {

  public final static Typeface ROBOTOLIGHT =
      Typeface.createFromAsset(ProfileManagerApplication.getContext().getAssets(),
          "fonts/Roboto-Light.ttf");
  public final static Typeface ROBOTOBOLD =
      Typeface.createFromAsset(ProfileManagerApplication.getContext().getAssets(),
          "fonts/Roboto-Bold.ttf");
  public final static Typeface ROBOTOREGULAR =
      Typeface.createFromAsset(ProfileManagerApplication.getContext().getAssets(),
          "fonts/Roboto-Regular.ttf");
  public final static Typeface ROBOTOMEDIUM =
      Typeface.createFromAsset(ProfileManagerApplication.getContext().getAssets(),
          "fonts/Roboto-Medium.ttf");
}
