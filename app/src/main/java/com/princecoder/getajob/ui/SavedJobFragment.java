package com.princecoder.getajob.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.princecoder.getajob.R;
import com.princecoder.getajob.adapter.SavedJobRecyclerViewAdapter;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.service.JobService;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedJobFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private SavedJobRecyclerViewAdapter mAdapter;
//    private TextView mEmptyView;
    private static final int CURSOR_LOADER_ID = 0;

    //Position
    private int mPosition=-1;

    //Selected item
    private final String SELECTED_KEY="Selected_key";

    //Listener
    OnJobSelectedListener mListener;

    // Log field
    private final String TAG=getClass().getSimpleName();

    // newInstance constructor for creating fragment with arguments
    public static SavedJobFragment newInstance() {
        SavedJobFragment f = new SavedJobFragment();
        return f;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //register the receiver
        getActivity().registerReceiver(mDeleteJobReceiver,
                new IntentFilter(JobService.DELETE_JOB));

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

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved_jobs, container, false);

//        mEmptyView=(TextView)rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_saved_job);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new SavedJobRecyclerViewAdapter(getActivity(), new SavedJobRecyclerViewAdapter.JobAdapterOnClickHandler() {
            @Override
            public void onClick(Job job, SavedJobRecyclerViewAdapter.ViewHolder vh) {
                mListener.onJobSelectedListener(job);
                mPosition=vh.getAdapterPosition();
            }

            @Override
            public void onDeleteJob(Job job, SavedJobRecyclerViewAdapter.ViewHolder vh) {
                mListener.onDeleteJobListener(job);
            }
        });

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(SELECTED_KEY)){
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
        }

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
            mAdapter.setSelectedItem(mPosition);
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        //Whenever I resume this fragment, I restart the loader
        restartLoader();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to RecyclerView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mDeleteJobReceiver);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                JobContract.JobEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private void restartLoader(){

        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor cursor=data;
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public interface OnJobSelectedListener{
        void onJobSelectedListener(Job job);
        void onDeleteJobListener(Job job);
    }

    private BroadcastReceiver mDeleteJobReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.DELETE_JOB.equals(intent.getAction())) {
                mPosition=-1;
                mAdapter.setSelectedItem(mPosition);
               restartLoader();
            }
        }
    };

}
