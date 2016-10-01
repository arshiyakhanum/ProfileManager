package com.arshiya.mapsapi.profilemanager;

import android.media.AudioManager;
import android.util.Log;

import static com.arshiya.mapsapi.common.Constants.RINGER_MODE_NORMAL;
import static com.arshiya.mapsapi.common.Constants.RINGER_MODE_SILENT;
import static com.arshiya.mapsapi.common.Constants.RINGER_MODE_VIBRATE;

/**
 * Created by arshiya on 15/11/15.
 */
public class UpdateProfile {

  private static final String TAG = UpdateProfile.class.getSimpleName();
  private AudioManager mAudioManager;

  private UpdateProfile() {
  }

  public UpdateProfile(int profile, AudioManager audioManager) {
    mAudioManager = audioManager;
    changeProfile(profile);
    Log.d(TAG, "updated profile to : " + profile);
  }

  private void changeProfile(int profile) {
    switch (profile) {
      case RINGER_MODE_NORMAL:
        Log.d(TAG, "updated profile to : NORMAL");
        setNormalVolumeSettings();
        break;

      case RINGER_MODE_SILENT:
        Log.d(TAG, "updated profile to : SILENT");
        setSilentVolumeSettings();
        break;

      case RINGER_MODE_VIBRATE:
        Log.d(TAG, "updated profile to : VIBRATE");
        setVibrateVolumeSettings();
        break;
    }
  }

  private void setNormalVolumeSettings() {
    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
  }

  private void setSilentVolumeSettings() {
    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
  }

  private void setVibrateVolumeSettings() {
    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
  }
}
