package com.princecoder.getajob;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.sync.JobService;

import java.util.ArrayList;


/**
 * create an instance of fragment to make search
 */
public class SearchFragment extends Fragment {

    private EditText mTitleEdt;
    private EditText mLocation;
    private Button mSearchBtn;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_search, container, false);
        mTitleEdt= (EditText) rootView.findViewById(R.id.title_edt);
        mLocation=(EditText)rootView.findViewById(R.id.location_edt);
        mSearchBtn=(Button)rootView.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Let find jobs from internet
                //@Todo make sur at least one field is filled out
                searchJobs();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //register the receiver
        getActivity().registerReceiver(mServiceJobsReceiver,
                new IntentFilter(JobService.SERVICE_JOBS));
    }

    @Override
    public void onStop() {
        super.onStop();

        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mServiceJobsReceiver);
    }


    //Search Jobs

    public void searchJobs(){
        String title=mTitleEdt.getText().toString();
        String location=mLocation.getText().toString();

        //Create job object
        Job job=new Job();
        job.setTitle(title);
        job.setLocation(location);

        Intent intent=new Intent(getActivity(),JobService.class);
        intent.setAction(JobService.FETCH_JOB_FROM_INTERNET);
        intent.putExtra(JobService.JOB_TAG, job);

        //Send intent via then startService Method
        getActivity().startService(intent);
    }

    private BroadcastReceiver mServiceJobsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SERVICE_JOBS.equals(intent.getAction())) {
                ArrayList<Job> jobArrayList=intent.getParcelableArrayListExtra(JobService.JOB_TAG);

                //Start new activity
                Intent intent1=new Intent(getActivity(),ListJobActivity.class);
                intent1.putExtra(JobService.JOB_TAG,jobArrayList);
                getActivity().startActivity(intent1);
            }
        }
    };

}
