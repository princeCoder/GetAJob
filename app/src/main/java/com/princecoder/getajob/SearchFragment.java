package com.princecoder.getajob;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.JobModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


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
        mTitleEdt.setFocusable(true);
        mLocation=(EditText)rootView.findViewById(R.id.location_edt);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCitiesList();
            }
        });
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


    public void searchCitiesList() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.cities_listview);
        final ListView listView = (ListView) dialog.findViewById(R.id.list);
        Iterator iterator=JobModel.myLocations.keySet().iterator();
        ArrayList<String> list=new ArrayList<>(JobModel.myLocations.values().size());
        Collections.sort(list);
        while(iterator.hasNext()){
            list.add(iterator.next().toString());
        }
        dialog.show();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                mLocation.setText(itemValue);
                dialog.cancel();

            }

        });

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        //register the receiver
//        getActivity().registerReceiver(mServiceJobsReceiver,
//                new IntentFilter(JobService.SERVICE_JOBS));
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        //Unregister the brodcast receiver
//        getActivity().unregisterReceiver(mServiceJobsReceiver);
//    }


    //Search Jobs

    public void searchJobs(){
        String title=mTitleEdt.getText().toString();
        String location=mLocation.getText().toString();

        //Create job object
        Job job=new Job();
        job.setTitle(title);
        job.setLocation(location);


        //Start new activity
        Intent intent1=new Intent(getActivity(),ListJobActivity.class);
        intent1.putExtra(ListJobsFragment.JOB_TAG, job);
        getActivity().startActivity(intent1);

//        Intent intent=new Intent(getActivity(),JobService.class);
//        intent.setAction(JobService.FETCH_JOB_FROM_INTERNET);
//        intent.putExtra(JobService.JOB_TAG, job);
//
//        //Send intent via then startService Method
//        getActivity().startService(intent);
    }

//    private BroadcastReceiver mServiceJobsReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (JobService.SERVICE_JOBS.equals(intent.getAction())) {
//                ArrayList<Job> jobArrayList=intent.getParcelableArrayListExtra(JobService.JOB_TAG);
//
//                //Start new activity
//                Intent intent1=new Intent(getActivity(),ListJobActivity.class);
//                intent1.putExtra(JobService.JOB_TAG,jobArrayList);
//                getActivity().startActivity(intent1);
//            }
//        }
//    };

}
