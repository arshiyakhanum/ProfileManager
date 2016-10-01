package com.arshiya.mapsapi.common;

/**
 * @author Arshiya Khanum on 10/1/2016.
 */
public class Logger {

  public static final int V = 0;
  public static final int D = 1;
  public static final int I = 2;
  public static final int W = 3;
  public static final int E = 4;
  public static final int A = 5;

  private static int mMinLogLevel = V;

  public static synchronized void isDebuggable(boolean isDebuggable) {
    if (isDebuggable) {
      mMinLogLevel = V;
    } else {
      mMinLogLevel = W;
    }
  }

  public static synchronized void v(String className, String method, String message) {
    log(V, className + "." + method, message);
  }

  public static synchronized void d(String className, String method, String message) {
    log(D, className + "." + method, message);
  }

  public static synchronized void i(String className, String method, String message) {
    log(I, className + "." + method, message);
  }

  public static synchronized void w(String className, String method, String message) {
    log(W, className + "." + method, message);
  }

  public static synchronized void e(String className, String method, String message) {
    log(E, className + "." + method, message);
  }

  public static synchronized void a(String className, String method, String message) {
    log(A, className + "." + method, message);
  }

  public static synchronized void v(String method, String message) {
    log(V, method, message);
  }

  public static synchronized void d(String method, String message) {
    log(D, method, message);
  }

  public static synchronized void i(String method, String message) {
    log(I, method, message);
  }

  public static synchronized void w(String method, String message) {
    log(W, method, message);
  }

  public static synchronized void e(String method, String message) {
    log(E, method, message);
  }

  public static synchronized void a(String method, String message) {
    log(A, method, message);
  }

  private static synchronized void log(int level, String tag, String message) {
    if (level < mMinLogLevel) {
      return;
    }

    switch (level) {
      case V:
        android.util.Log.v(tag, message);
        break;

      case D:
        android.util.Log.d(tag, message);
        break;

      case I:
        android.util.Log.i(tag, message);
        break;

      case W:
        android.util.Log.w(tag, message);
        break;

      case E:
        android.util.Log.e(tag, message);
        break;

      case A:
        android.util.Log.wtf(tag, message);
        break;
    }
  }
}
