package com.arshiya.mapsapi.settings;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by akhanumx on 11/5/2015.
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
        int mode = audioManager.getRingerMode();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

    }
}
