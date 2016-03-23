package com.princecoder.getajob.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.princecoder.getajob.BuildConfig;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.parsers.JobJSONParser;
import com.princecoder.getajob.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobService extends IntentService{

    //log Tag
    private final String LOG_TAG = JobService.class.getSimpleName();

    public static final String FETCH_JOB_FROM_INTERNET = "com.princecoder.sync.action.FETCH_JOB_FROM_INTERNET";
    public static final String FETCH_PAGES_FROM_INTERNET = "com.princecoder.sync.action.FETCH_PAGES_FROM_INTERNET";
    public static final String FETCH_SAVED_JOB = "com.princecoder.sync.action.FETCH_SAVED_JOB";
    public static final String DELETE_JOB = "com.princecoder.sync.action.DELETE_JOB";
    public static final String SAVE_JOB = "com.princecoder.sync.action.SAVE_JOB";
    public static final String DOES_JOB_EXIST = "com.princecoder.sync.action.DOES_JOB_EXIST";
    public static final String SAVE_RECENT_SEARCH = "com.princecoder.sync.action.SAVE_RECENT_SEARCH";
    public static final String SERVICE_JOBS = "SERVICE_JOBS";
    public static final String SERVICE_PAGES = "SERVICE_PAGES";

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


    private final String AUTHENTIC_JOBS_BASE_URL =
            "https://authenticjobs.com/api/?";
    private final String APP_ID_PARAM = "api_key";
    private final String METHOD_PARAM = "aj.jobs.search";
    private final String PER_PAGE="25";
    private final String FORMAT="json";
    private final String PAGE="page";

    public ArrayList<Job> jobList=new ArrayList<>();


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
            if (FETCH_JOB_FROM_INTERNET.equals(action)) {
                // Since the API my return many pages to display job depending how many job we display per page, these are the steps:
                // 1. Get the page number
                //2. Fetch jobs on that page
                int numPage=intent.getIntExtra(PAGE_TAG,1);

                //Fetch the Job from Internet
                fetchJobFromInternet(job, numPage);

            }else if (FETCH_PAGES_FROM_INTERNET.equals(action)) {

                //Fetch the Job from Internet
                getNumberOfPageFromInternet(job);

            }else if (DOES_JOB_EXIST.equals(action)) {
                Intent intent1 = new Intent(DOES_JOB_EXIST);
                intent1.putExtra(DOES_JOB_EXIST,isJobFound(job.getId()));
                getApplicationContext().sendBroadcast(intent1);

            }
            else if (FETCH_SAVED_JOB.equals(action)) {

                //We fetch job from the database
                fetchSavedJob(job);
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
            }
            else if (SAVE_RECENT_SEARCH.equals(action)){

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

            // We broadcast the response to the fragment
            Intent saveIntent = new Intent(SAVE_RECENT_SEARCH);
            saveIntent.putExtra(MESSAGE, "Save recent search");
            getApplicationContext().sendBroadcast(saveIntent);
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

    /**
     *
     * Fetch jobs from Intenet
     * @param job Basically has the job title and the job location
     * @param pageNumber The page from which we wants to get available jobs
     *
     *  We construct the API
     *  We get data from the server
     */
    public synchronized void fetchJobFromInternet(Job job, int pageNumber) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jobsJsonStr = null;

        // Check for Internet
        if(Utility.isOnline(getApplicationContext())){
            try{

                //We construct the API Url

                Uri builtUri=null;
                String locationid=Utility.getLocationId(job.getLocation());
                if((locationid!=null)&&(!job.getTitle().isEmpty())){
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY) //This is how I get the API-Key from gradle
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("keywords", job.getTitle())
                            .appendQueryParameter("location", locationid)
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter(PAGE, ""+pageNumber)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                else if(locationid==null) {
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("keywords", job.getTitle())
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter(PAGE, ""+pageNumber)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                else if (job.getTitle().isEmpty()) {
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("location", locationid)
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter(PAGE, ""+pageNumber)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                URL url = null;
                url = new URL(builtUri.toString());

                System.out.println("------- Url;   "+url);

                // Create the request to AuthenticJob, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                //We retreive the JSON string of data
                jobsJsonStr = Utility.getData(urlConnection);
                if((jobsJsonStr== null)||(jobsJsonStr.length()==0)){
                    return;
                }

                //This is the list of job found from that page
                jobList= (ArrayList<Job>) JobJSONParser.parseFeed(getApplicationContext(),jobsJsonStr);

                //broadcast the list of job found
                broadcastJobsPerPage(jobList, pageNumber);

            }catch (ProtocolException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            //Todo I send a broadcast for not having a connection
//            Intent broadcastIntent=new Intent(HANDLE_MESSAGE);
//            broadcastIntent.putExtra(MESSAGE,NO_INTERNET);
//            getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }



    //Fetch jobs from Intenet
    private synchronized void getNumberOfPageFromInternet(Job job) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jobsJsonStr = null;

        // Check for Internet
        if(Utility.isOnline(getApplicationContext())){
            try{
                Uri builtUri=null;
                String locationid=Utility.getLocationId(job.getLocation());
                if((locationid!=null)&&(!job.getTitle().isEmpty())){
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY) //This is how I get the API-Key from gradle
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("keywords", job.getTitle())
                            .appendQueryParameter("location", locationid)
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                else if(locationid==null) {
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("keywords", job.getTitle())
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                else if (job.getTitle().isEmpty()) {
                    builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                            .appendQueryParameter("method", METHOD_PARAM)
                            .appendQueryParameter("location", locationid)
                            .appendQueryParameter("perpage", PER_PAGE)
                            .appendQueryParameter("format", FORMAT)
                            .build();
                }
                URL url = null;
                url = new URL(builtUri.toString());

                System.out.println("------- Url;   "+url);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                jobsJsonStr = Utility.getData(urlConnection);
                if((jobsJsonStr== null)||(jobsJsonStr.length()==0)){
                    return;
                }

                int pages= JobJSONParser.getPages(getApplicationContext(), jobsJsonStr);

                // We brodcast the response to the fragment
                Intent intent = new Intent(SERVICE_PAGES);
                intent.putExtra(PAGE_TAG,pages);
                intent.putExtra(JOB_TAG,job);
                getApplicationContext().sendBroadcast(intent);


            }catch (ProtocolException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            //Todo I send a broadcast for not having a connection
//            Intent broadcastIntent=new Intent(HANDLE_MESSAGE);
//            broadcastIntent.putExtra(MESSAGE,NO_INTERNET);
//            getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }


    //Fetch jobs from Intenet
    private void fetchSavedJob(Job job) {

    }
}
