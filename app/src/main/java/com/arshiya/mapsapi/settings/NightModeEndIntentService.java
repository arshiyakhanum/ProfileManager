package com.arshiya.mapsapi.settings;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.arshiya.mapsapi.profilemanager.UpdateProfile;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;

/**
 * Created by arshiya on 11/5/2015.
 */
public class NightModeEndIntentService extends IntentService {

  private static final String TAG = NightModeEndIntentService.class.getSimpleName();

  public NightModeEndIntentService() {
    super(null);
  }

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public NightModeEndIntentService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, " onHandle");
    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    ProfileManagerSharedPref profileManagerSharedPref =
            ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
    profileManagerSharedPref.nightModeOn(false);
    new UpdateProfile(profileManagerSharedPref.getSavedRingerMode(), audioManager);
  }
}
