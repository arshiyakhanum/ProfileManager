package com.arshiya.mapsapi.settings;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.arshiya.mapsapi.profilemanager.UpdateProfile;
import com.arshiya.mapsapi.storage.sharedpreference.ProfileManagerSharedPref;

import static com.arshiya.mapsapi.common.Constants.RINGER_MODE_SILENT;

/**
 * Created by arshiya on 11/5/2015.
 */
public class NightModeStartIntentService extends IntentService {

  private static final String TAG = NightModeStartIntentService.class.getSimpleName();

  public NightModeStartIntentService() {
    super(null);
  }

  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public NightModeStartIntentService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, " onHandle");
    //save current profile
    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    ProfileManagerSharedPref profileManagerSharedPref =
            ProfileManagerSharedPref.gcSharedPreferenceInstance(this);
    profileManagerSharedPref.saveRingerMode(audioManager.getRingerMode());
    profileManagerSharedPref.nightModeOn(true);
    new UpdateProfile(RINGER_MODE_SILENT, audioManager);
  }
}
