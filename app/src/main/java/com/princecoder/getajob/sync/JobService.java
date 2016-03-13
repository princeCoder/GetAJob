package com.princecoder.getajob.sync;

import android.app.IntentService;
import android.content.Intent;

import com.princecoder.getajob.model.Job;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobService extends IntentService{

    //log Tag
    private final String LOG_TAG = JobService.class.getSimpleName();

    public static final String FETCH_JOB = "com.princecoder.sync.action.FETCH_BOOK";
    public static final String DELETE_JOB = "com.princecoder.sync.action.DELETE_BOOK";
    public static final String SAVE_JOB = "com.princecoder.sync.action.SAVE_BOOK";


    public JobService() {
        super("Get A Job Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Job received from the intent
            Job job=(Job)intent.getParcelableExtra("job");

            //Action
            final String action = intent.getAction();

            if (FETCH_JOB.equals(action)) {

                //Fetch the Job from Internet
                fetchJob(job);

            } else if (DELETE_JOB.equals(action)) {
                //We delete the job
                deleteJob(job);
            }
            else if (SAVE_JOB.equals(action)){
                    //We save the Job
                    saveJob(job);
            }
        }
    }

    //Save Job in the database
    private void saveJob(Job job){

    }

    //Delete the job in the database
    private void deleteJob(Job job){

    }

    //Fetch jobs from the database
    private void fetchJob(Job job) {

    }


}
