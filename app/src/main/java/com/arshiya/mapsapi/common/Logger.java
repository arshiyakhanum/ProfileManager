package com.arshiya.mapsapi.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * @author Arshiya Khanum on 10/1/2016.
 */
public class Logger {

  public static final int VERBOSE = 0;
  public static final int DEBUG = 1;
  public static final int INFO = 2;
  public static final int WARNING = 3;
  public static final int ERROR = 4;
  public static final int ASSERT = 5;

  private static int MIN_LOG_LEVEL = VERBOSE;

  private Logger() {
  }

  public static void enableDebuggingForDebugBuild(Context context) {
    if (null == context) {
      return;
    }
    try {
      boolean debuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
      if (debuggable) {
        setLogStatus(true);
      }
    } catch (Exception e) {
      Logger.e("Logger : enableDebuggingForDebugBuild", e);
    }

  }

  public static void v(String tag, String message) {
    if (isDebuugable() && VERBOSE >= MIN_LOG_LEVEL) {
      Log.v(tag, message);
    }
  }

  public static void d(String tag, String message) {
    if (isDebuugable() && DEBUG >= MIN_LOG_LEVEL) {
      Log.d(tag, message);
    }
  }

  public static void i(String tag, String message) {
    if (isDebuugable() && INFO >= MIN_LOG_LEVEL) {
      Log.i(tag, message);
    }
  }

  public static void w(String tag, String message) {
    if (isDebuugable() && WARNING >= MIN_LOG_LEVEL) {
      Log.w(tag, message);
    }
  }

  public static void e(String tag, String message) {
    if (isDebuugable() && ERROR >= MIN_LOG_LEVEL) {
      Log.e(tag, message);
    }
  }

  public static void wtf(String tag, String message) {
    if (isDebuugable() && ASSERT >= MIN_LOG_LEVEL) {
      Log.wtf(tag, message);
    }
  }


  public static void v(String message, Throwable tr) {
    if (isDebuugable() && VERBOSE >= MIN_LOG_LEVEL) {
      Log.v(TAG, message, tr);
    }
  }

  public static void d(String message, Throwable tr) {
    if (isDebuugable() && DEBUG >= MIN_LOG_LEVEL) {
      Log.d(TAG, message, tr);
    }
  }

  public static void i(String message, Throwable tr) {
    if (isDebuugable() && INFO >= MIN_LOG_LEVEL) {
      Log.i(TAG, message, tr);
    }
  }

  public static void w(String message, Throwable tr) {
    if (isDebuugable() && WARNING >= MIN_LOG_LEVEL) {
      Log.w(TAG, message, tr);
    }
  }

  public static void e(String message, Throwable tr) {
    if (isDebuugable() && ERROR >= MIN_LOG_LEVEL) {
      Log.e(TAG, message, tr);
    }
  }

  public static void wtf(String message, Throwable tr) {
    if (isDebuugable() && ASSERT >= MIN_LOG_LEVEL) {
      Log.wtf(TAG, message, tr);
    }
  }

  public static final String TAG;

  static {
    TAG = "SmartProfile_V" + Constants.APP_VERSION_NAME;
  }

  private static boolean DEBUG_ENABLED = false;

  private static void setLogStatus(boolean value) {
    DEBUG_ENABLED = value;
  }

  public static void setLogLevel(int level) {
    MIN_LOG_LEVEL = level;
  }

  private static boolean isDebuugable() {
    return DEBUG_ENABLED;
  }
}
