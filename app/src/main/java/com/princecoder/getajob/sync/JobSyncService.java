package com.princecoder.getajob.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Prinzly Ngotoum on 3/24/16.
 */
public class JobSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static JobSyncAdapter sJobSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("JobSyncService", "onCreate - JobSyncService");
        synchronized (sSyncAdapterLock) {
            if (sJobSyncAdapter == null) {
                sJobSyncAdapter = new JobSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sJobSyncAdapter.getSyncAdapterBinder();
    }
}