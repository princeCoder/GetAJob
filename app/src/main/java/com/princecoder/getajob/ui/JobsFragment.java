package com.princecoder.getajob.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.princecoder.getajob.BuildConfig;
import com.princecoder.getajob.R;
import com.princecoder.getajob.adapter.SearchJobRecyclerViewAdapter;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.parsers.JobJSONParser;
import com.princecoder.getajob.service.JobService;
import com.princecoder.getajob.utils.L;
import com.princecoder.getajob.utils.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobsFragment extends Fragment {

    // My adapter
    private SearchJobRecyclerViewAdapter mAdapter;

    // My recyclerView
    private RecyclerView mRecyclerView;

    //Position
    private int mPosition=-1;

    //Selected item
    private final String SELECTED_KEY="Selected_key";

    //List_TAG
    private final String LIST_TAG="List";

    // Log field
    private final String TAG=getClass().getSimpleName();

    //List of Jobs
    private ArrayList<Job> mJobList;

    //Job
    private Job jobParam;

    private ProgressBar mProgressBar;

    //Job tag
    public static String JOB_TAG="JOB_TAG";

    //Num Page
    private int mNumPage;

    public static final String PAGE="PAGE";

    //Tag
    public static final String CURRENT_SEARCH="CURRENT_SEARCH";
    
    //Listener
    OnJobSelectedListener mListener;

    static JobsFragment newInstance(int numPage) {
        JobsFragment f = new JobsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(JobService.PAGE_TAG, numPage);
        f.setArguments(args);

        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNumPage = getArguments() != null ? getArguments().getInt(JobService.PAGE_TAG) : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView=  inflater.inflate(R.layout.fragment_jobs, container, false);
        mRecyclerView=(RecyclerView)myView.findViewById(R.id.recyclerview_job);
        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar=(ProgressBar) myView.findViewById(R.id.progressImage);

        mAdapter=new SearchJobRecyclerViewAdapter(getContext(), new SearchJobRecyclerViewAdapter.ViewHolderOnClickHandler() {
            @Override
            public void onClick(int id, SearchJobRecyclerViewAdapter.ViewHolder vh) {
                //@Todo display job details
                Job job=mAdapter.getItem(id);
                mListener.onJobSelectedListener(job,vh);
                mPosition=id;
            }

            @Override
            public void onSaveJob(int id, SearchJobRecyclerViewAdapter.ViewHolder vh) {
                Job job=mAdapter.getItem(id);
                mListener.onJobSavedListener(job);
            }
        });

        if(savedInstanceState!=null) {
            if (mProgressBar.isShown()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            if (savedInstanceState.containsKey(LIST_TAG)) {
                mJobList = savedInstanceState.getParcelableArrayList(LIST_TAG);
                if(mJobList==null){
                    jobParam=savedInstanceState.getParcelable(CURRENT_SEARCH);
                    mNumPage=savedInstanceState.getInt(PAGE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    requestData(jobParam,mNumPage);
                }
                else
                    mAdapter.swapElements(mJobList);
            }
        }
        else{
            jobParam=getActivity().getIntent().getParcelableExtra(JOB_TAG);
            mProgressBar.setVisibility(View.VISIBLE);
            requestData(jobParam,mNumPage);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        if (mPosition != RecyclerView.NO_POSITION) {

            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mAdapter.notifyItemChanged(mPosition);
            mAdapter.setSelectedItem(mPosition);
            mAdapter.notifyItemChanged(mPosition);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    //Align selection to top
                    mRecyclerView.smoothScrollToPosition(mPosition);
                }
            });


        }
        setRetainInstance(true);
        return myView;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnJobSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(TAG + activity.getString(R.string.job_selected_listener_error));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        outState.putParcelableArrayList(LIST_TAG, mJobList);
        outState.putParcelable(CURRENT_SEARCH, jobParam);
        outState.putInt(PAGE, mNumPage);
        super.onSaveInstanceState(outState);
    }

    private void requestData(Job job, int pageNumber) {
        if(Utility.isOnline(getActivity())){
            // instantiate the RequestQueue
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url =buildURL(job,pageNumber);
            // request a string response asynchronously from the provided URL
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mJobList = (ArrayList<Job>) JobJSONParser.parseFeed(getActivity(), response);
                            mProgressBar.setVisibility(View.GONE);
                            mAdapter.swapElements(mJobList);
                            if(mJobList!=null && mJobList.size()==0){ //We display a message in the snackBar
                                Snackbar.make(getView(), R.string.no_job_found_message, Snackbar.LENGTH_LONG).show();
                            }
                            else if(getActivity()!=null && !Utility.isOnline(getActivity())){ //We display a message in the snackBar
                                Snackbar.make(getView(), R.string.no_internet_message, Snackbar.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.GONE);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(getActivity()!=null && !Utility.isOnline(getActivity())){ //We display a message in the snackBar
                                Snackbar.make(getView(), R.string.no_internet_message, Snackbar.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                            else{
                                if(getView()!=null)
                                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.GONE);
                            }

                        }
            });

            // add the request to the RequestQueue
            queue.add(stringRequest);
        } else {
            if(getActivity()!=null)
            L.toast(getActivity(), getResources().getString(R.string.no_internet_message));
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private String buildURL(Job job, int pageNumber) {
        final String AUTHENTIC_JOBS_BASE_URL = "https://authenticjobs.com/api/?";
        final String APP_ID_PARAM = "api_key";
        final String METHOD_PARAM = "aj.jobs.search";
        final String PER_PAGE = "25";
        final String FORMAT = "json";
        final String PAGE = "page";
            //We construct the API Url
            Uri builtUri = null;
            String locationid = Utility.getLocationId(job.getLocation());
            if ((locationid != null) && (!job.getTitle().isEmpty())) {
                builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY) //This is how I get the API-Key from gradle
                        .appendQueryParameter("method", METHOD_PARAM)
                        .appendQueryParameter("keywords", job.getTitle())
                        .appendQueryParameter("location", locationid)
                        .appendQueryParameter("perpage", PER_PAGE)
                        .appendQueryParameter(PAGE, "" + pageNumber)
                        .appendQueryParameter("format", FORMAT)
                        .build();
            } else if (locationid == null) {
                builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                        .appendQueryParameter("method", METHOD_PARAM)
                        .appendQueryParameter("keywords", job.getTitle())
                        .appendQueryParameter("perpage", PER_PAGE)
                        .appendQueryParameter(PAGE, "" + pageNumber)
                        .appendQueryParameter("format", FORMAT)
                        .build();
            } else if (job.getTitle().isEmpty()) {
                builtUri = Uri.parse(AUTHENTIC_JOBS_BASE_URL).buildUpon()
                        .appendQueryParameter(APP_ID_PARAM, BuildConfig.AUTHENTIC_JOBS__API_KEY)
                        .appendQueryParameter("method", METHOD_PARAM)
                        .appendQueryParameter("location", locationid)
                        .appendQueryParameter("perpage", PER_PAGE)
                        .appendQueryParameter(PAGE, "" + pageNumber)
                        .appendQueryParameter("format", FORMAT)
                        .build();
            }
            return builtUri.toString();
    }

    public interface OnJobSelectedListener{
        void onJobSelectedListener(Job job, SearchJobRecyclerViewAdapter.ViewHolder vh);
        void onJobSavedListener(Job job);
    }

}
