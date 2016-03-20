package com.princecoder.getajob;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.princecoder.getajob.adapter.JobAdapterRecyclerView;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.sync.JobService;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobsFragment extends Fragment {

    private TextView emptyView;

    // List of Artists
    private ArrayList<Job> mListOfArtist=new ArrayList<>();

    // My adapter
    private JobAdapterRecyclerView mAdapter;

    // My recyclerView
    private RecyclerView mRecyclerView;

    //Position
    private int mPosition;

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

        //register the receiver
        getActivity().registerReceiver(mServiceJobsReceiver,
                new IntentFilter(JobService.SERVICE_JOBS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView=  inflater.inflate(R.layout.fragment_jobs, container, false);
        emptyView=(TextView)myView.findViewById(R.id.empty_view);

        mRecyclerView=(RecyclerView)myView.findViewById(R.id.recyclerview_job);
        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar=(ProgressBar) myView.findViewById(R.id.progressImage);

        mAdapter=new JobAdapterRecyclerView(getContext(), new JobAdapterRecyclerView.ViewHolderOnClickHandler() {
            @Override
            public void onClick(int id, JobAdapterRecyclerView.ViewHolder vh) {
                //@Todo display job details
                Job job=mAdapter.getItem(id);
                mListener.onJobSelectedListener(job);
//                Intent intent=new Intent(getActivity(),JobDetailActivity.class);
//                getActivity().startActivity(intent);
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
                mAdapter.swapElements(mJobList);
            }
        }
        else{
//            mJobList=getActivity().getIntent().getParcelableArrayListExtra(JobService.JOB_TAG);
            jobParam=getActivity().getIntent().getParcelableExtra(JOB_TAG);
            Intent intent=new Intent(getActivity(),JobService.class);
            intent.setAction(JobService.FETCH_JOB_FROM_INTERNET);
            intent.putExtra(JobService.JOB_TAG, jobParam);
            intent.putExtra(JobService.PAGE_TAG,mNumPage);

            mProgressBar.setVisibility(View.VISIBLE);

            //Send intent via then startService Method
            getActivity().startService(intent);

        }


        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
            mAdapter.setSelectedItem(mPosition);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

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
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mServiceJobsReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition!= ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        outState.putParcelableArrayList(LIST_TAG,mJobList);
        super.onSaveInstanceState(outState);
    }

    private BroadcastReceiver mServiceJobsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SERVICE_JOBS.equals(intent.getAction())) {
                int pageNumber=intent.getIntExtra(JobService.PAGE_TAG,1);
                //Since the StatePagerAdapter create the current fragment and the next one,
                //In my case since I populate the view using the broadcast, I might have two instances running one after one.
                //So I use this extra to create different intents
                if(mNumPage==pageNumber){
                    mJobList=intent.getParcelableArrayListExtra(JobService.JOB_LIST_TAG);
                    mProgressBar.setVisibility(View.GONE);
                    mAdapter.swapElements(mJobList);
                    if(mJobList!=null && mJobList.size()==0){ //We display a message in the snackBar
                        Snackbar.make(getView(), "No job found", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    public interface OnJobSelectedListener{
        void onJobSelectedListener(Job job);
    }


}
