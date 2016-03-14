package com.princecoder.getajob;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.princecoder.getajob.adapter.JobAdapterRecyclerView;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.sync.JobService;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListJobsFragment extends Fragment {

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

    private ArrayList<Job> mJobList;

    public ListJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView=  inflater.inflate(R.layout.fragment_list_jobs, container, false);
        emptyView=(TextView)myView.findViewById(R.id.empty_view);

        mRecyclerView=(RecyclerView)myView.findViewById(R.id.recyclerview_job);
        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(SELECTED_KEY)){
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            if(savedInstanceState.containsKey(LIST_TAG)){
                mJobList = savedInstanceState.getParcelableArrayList(LIST_TAG);
            }
        }
        else{
            mJobList=getActivity().getIntent().getParcelableArrayListExtra(JobService.JOB_TAG);
        }

        mAdapter=new JobAdapterRecyclerView(getContext(), mJobList,new JobAdapterRecyclerView.ViewHolderOnClickHandler() {
            @Override
            public void onClick(int id, JobAdapterRecyclerView.ViewHolder vh) {
                //@Todo display job details
            }
        });


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
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition!= ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        outState.putParcelableArrayList(LIST_TAG,mJobList);
        super.onSaveInstanceState(outState);
    }

}
