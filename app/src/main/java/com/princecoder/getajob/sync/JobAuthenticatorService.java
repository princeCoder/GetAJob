package com.princecoder.getajob.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Prinzly Ngotoum on 3/24/16.
 */
public class JobAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private JobAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new JobAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
