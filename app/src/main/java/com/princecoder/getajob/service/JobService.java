package com.princecoder.getajob.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;

import java.util.ArrayList;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobService extends IntentService{

    //log Tag
    private final String LOG_TAG = JobService.class.getSimpleName();

    public static final String FETCH_PAGES_FROM_INTERNET = "com.princecoder.sync.action.FETCH_PAGES_FROM_INTERNET";
    public static final String DELETE_JOB = "com.princecoder.sync.action.DELETE_JOB";
    public static final String DELETE_RECENT_SEARCH = "com.princecoder.sync.action.DELETE_RECENT_SEARCH";
    public static final String SAVE_JOB = "com.princecoder.sync.action.SAVE_JOB";
    public static final String DOES_JOB_EXIST = "com.princecoder.sync.action.DOES_JOB_EXIST";
    public static final String SAVE_RECENT_SEARCH = "com.princecoder.sync.action.SAVE_RECENT_SEARCH";
    public static final String SERVICE_JOBS = "SERVICE_JOBS";

    // Job List tag
    public static final String JOB_LIST_TAG = "JOB_LIST_TAG";

    // Job tag
    public static final String JOB_TAG = "JOB_TAG";

    // Recent tag
    public static final String RECENT_TAG = "RECENT_TAG";

    //Page number tag
    public static final String PAGE_TAG = "PAGE_TAG";

    //Message  tag
    public static final String MESSAGE = "MESSAGE";


    public JobService() {
        super("GetAJobService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Job received from the intent
            Job job=intent.getParcelableExtra(JOB_TAG);

            //Action
            final String action = intent.getAction();
            if (DOES_JOB_EXIST.equals(action)) {
                Intent intent1 = new Intent(DOES_JOB_EXIST);
                intent1.putExtra(DOES_JOB_EXIST,isJobFound(job.getId()));
                getApplicationContext().sendBroadcast(intent1);

            } else if (DELETE_JOB.equals(action)) {

                //We delete the job
                deleteJob(job);
            } else if (SAVE_JOB.equals(action)){
                String message;
                if(!isJobFound(job.getId())){
                    //We save the Job
                    saveJob(job);
                }
                else{
                    // We brodcast the response to the fragment
                    Intent saveIntent = new Intent(SAVE_JOB);
                    saveIntent.putExtra(MESSAGE,"Job already saved");
                    getApplicationContext().sendBroadcast(saveIntent);
                }
            } else if (DELETE_RECENT_SEARCH.equals(action)) {
                //We delete the recent search
                RecentSearch search=intent.getParcelableExtra(RECENT_TAG);
                deleteRecentSearch(search);
            } else if (SAVE_RECENT_SEARCH.equals(action)){

                RecentSearch search=intent.getParcelableExtra(RECENT_TAG);
                //We save recent search
                saveRecentSearch(search);
            }
        }
    }

    private synchronized  void deleteRecentSearch(RecentSearch recent) {
        if(recent!=null) {
            //Selection clause
            String mSelectionClause = JobContract.RecentEntry.TITLE + " = ? AND " +JobContract.RecentEntry.LOCATION + " = ?";

            //Selection Argument
            String[] selectionArgs = new String[]{recent.getTitle(),recent.getLocation()!=null?recent.getLocation():""};
            getContentResolver().delete(JobContract.RecentEntry.CONTENT_URI, mSelectionClause, selectionArgs);

            // We broadcast the response to the fragment
            Intent intent = new Intent(DELETE_RECENT_SEARCH);
            getApplicationContext().sendBroadcast(intent);
        }
    }

    /**
     * Save a recent search
     * @param search
     * If that recent search exist already, We delete it and save it back again
     */
    private void saveRecentSearch(RecentSearch search) {

            deleteRecentSearch(search);
            ContentValues values= new ContentValues();
            values.put(JobContract.RecentEntry.TITLE,search.getTitle());
            values.put(JobContract.RecentEntry.LOCATION,search.getLocation());
            getContentResolver().insert(JobContract.RecentEntry.CONTENT_URI, values);
    }

    /**
     * check if a provided Job id already exist
     * @param id
     * @return
     */
    private boolean isJobFound(String id){
        Cursor jobEntry = getContentResolver().query(
                JobContract.JobEntry.buildJobUri(Long.parseLong(id)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        boolean found= jobEntry.getCount()>0?true:false;
        jobEntry.close();
        return found;
    }

    /**
     * check if a provided Recent search already exist
     * @param recent
     * @return
     */
    private boolean isRecentFound(RecentSearch recent){
        //Selection clause
        String mSelectionClause = JobContract.RecentEntry.TITLE + " = ? AND " +JobContract.RecentEntry.LOCATION + " = ?";
        //Selection Argument
        String[] selectionArgs = new String[]{recent.getTitle(),recent.getLocation()!=null?recent.getLocation():""};
        Cursor jobEntry = getContentResolver().query(
                JobContract.RecentEntry.CONTENT_URI,
                null,
                mSelectionClause,
                selectionArgs,
                null);

        boolean found= jobEntry.getCount()>0?true:false;
        jobEntry.close();
        return found;
    }

    //Save Job in the database
    private void saveJob(Job job){
        ContentValues values= new ContentValues();
        values.put(JobContract.JobEntry._ID, job.getId());
        values.put(JobContract.JobEntry.TITLE,job.getTitle());
        values.put(JobContract.JobEntry.COMPANY_LOGO,job.getCompanyLogo());
        values.put(JobContract.JobEntry.LOCATION,job.getLocation());
        values.put(JobContract.JobEntry.DESC,job.getDescription());
        values.put(JobContract.JobEntry.PERKS,job.getPerks());
        values.put(JobContract.JobEntry.POST_DATE,job.getPostDate());
        values.put(JobContract.JobEntry.RELOCATION_ASSISTANCE,job.getRelocationAssistance());
        values.put(JobContract.JobEntry.COMPANY_NAME,job.getCompanyName());
        values.put(JobContract.JobEntry.KEYWORDS,job.getKeywords());
        values.put(JobContract.JobEntry.URL,job.getUrl());
        values.put(JobContract.JobEntry.APPLY_URL,job.getApplyUrl());
        values.put(JobContract.JobEntry.COMPANY_TAG_LINE,job.getCompanyTagLine());
        values.put(JobContract.JobEntry.TYPE,job.getJobType());
                getContentResolver().insert(JobContract.JobEntry.CONTENT_URI, values);
        // We brodcast the response to the fragment
        Intent saveIntent = new Intent(SAVE_JOB);
        saveIntent.putExtra(MESSAGE, "Job saved");
        getApplicationContext().sendBroadcast(saveIntent);
    }

    //Delete the job in the database
    private void deleteJob(Job job){
        if(job!=null) {
            getContentResolver().delete(JobContract.JobEntry.buildJobUri(Long.parseLong(job.getId())), null, null);
            // We brodcast the response to the fragment
            Intent intent = new Intent(DELETE_JOB);
            getApplicationContext().sendBroadcast(intent);
        }
    }

    public void broadcastJobsPerPage(ArrayList<Job> list, int pageNum){
        // We brodcast the response to the fragment
        Intent intent = new Intent(SERVICE_JOBS);
        intent.putParcelableArrayListExtra(JOB_LIST_TAG,list);
        // This is to make the diference on intents.
        // In this case, No matter how many broadcast I'm using, I will always have different intents.
        intent.putExtra(PAGE_TAG,pageNum);
        getApplicationContext().sendBroadcast(intent);
    }
}
