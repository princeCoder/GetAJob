package com.princecoder.getajob;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Prinzly Ngotoum on 3/24/16.
 */
public class JobApplication extends Application {
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public  Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getApplicationContext());
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker =analytics.newTracker("UA-75589722-1");
        }
        return mTracker;
    }
}
