package com.princecoder.getajob;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Prinzly Ngotoum on 3/24/16.
 */
public class JobApplication extends Application {
    private Tracker mTracker;

    private static volatile JobApplication mInstance;
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */

    public JobApplication() {
        super();
        mInstance = this;
    }

    public JobApplication getInstance(){
        return mInstance;
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getApplicationContext());
            mTracker =analytics.newTracker(getString(R.string.analytic_key));
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }
}
