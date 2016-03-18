package com.princecoder.getajob;


import android.app.Activity;
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

import com.princecoder.getajob.adapter.RecyclerViewCursorAdapter;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedJobFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private RecyclerViewCursorAdapter mAdapter;
//    private TextView mEmptyView;
    private static final int CURSOR_LOADER_ID = 0;

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

        mAdapter=new RecyclerViewCursorAdapter(getActivity(), new RecyclerViewCursorAdapter.JobAdapterOnClickHandler() {
            @Override
            public void onClick(Job job, RecyclerViewCursorAdapter.ViewHolder vh) {
                mListener.onJobSelectedListener(job);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onResume() {
        super.onResume();

        //Whenever I resume this fragment, I restart the loader
        restartLoader();
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
    }

}
