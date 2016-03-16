package com.princecoder.getajob.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.princecoder.getajob.BuildConfig;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.parsers.JobJSONParser;
import com.princecoder.getajob.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobService extends IntentService{

    //log Tag
    private final String LOG_TAG = JobService.class.getSimpleName();

    public static final String FETCH_JOB_FROM_INTERNET = "com.princecoder.sync.action.FETCH_JOB_FROM_INTERNET";
    public static final String FETCH_SAVED_JOB = "com.princecoder.sync.action.FETCH_SAVED_JOB";
    public static final String DELETE_JOB = "com.princecoder.sync.action.DELETE_JOB";
    public static final String SAVE_JOB = "com.princecoder.sync.action.SAVE_JOB";
    public static final String SERVICE_JOBS = "SERVICE_JOBS";
    public static final String JOB_TAG = "JOB_TAG";

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

                //Fetch the Job from Internet
                fetchJobFromInternet(job);

            } else if (FETCH_SAVED_JOB.equals(action)) {

                //We fetch job from the database
                fetchSavedJob(job);
            } else if (DELETE_JOB.equals(action)) {

                //We delete the job
                deleteJob(job);
            } else if (SAVE_JOB.equals(action)){

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

    //Fetch jobs from Intenet
    private void fetchJobFromInternet(Job job) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jobsJsonStr = null;

        // Check for Internet
        if(Utility.isOnline(getApplicationContext())){
            try{
                final String AUTHENTIC_JOBS_BASE_URL =
                        "https://authenticjobs.com/api/?";
                final String APP_ID_PARAM = "api_key";
                final String METHOD_PARAM = "aj.jobs.search";
                final String PER_PAGE="50";
                final String FORMAT="json";

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
                URL url = new URL(builtUri.toString());

                System.out.println("------- Url;   "+url);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
                    return;
                }
                jobsJsonStr = buffer.toString();
                jobList= (ArrayList<Job>) JobJSONParser.parseFeed(jobsJsonStr);

                // We brodcast the response to the fragment
                Intent intent = new Intent(SERVICE_JOBS);
                intent.putParcelableArrayListExtra(JOB_TAG,jobList);
                getApplicationContext().sendBroadcast(intent);

            }catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                //setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
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
