package com.princecoder.getajob;


import android.app.Activity;
import android.content.Intent;
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

import com.princecoder.getajob.adapter.RecentRecyclerViewAdapter;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.sync.JobService;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private RecentRecyclerViewAdapter mAdapter;
    //    private TextView mEmptyView;
    private static final int CURSOR_LOADER_ID = 0;

    //Listener
    OnSearchSelectedListener mListener;

    // Log field
    private final String TAG=getClass().getSimpleName();

    // newInstance constructor for creating fragment with arguments
    public static RecentSearchFragment newInstance() {
        RecentSearchFragment f = new RecentSearchFragment();
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
            mListener = (OnSearchSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(TAG + activity.getString(R.string.job_selected_listener_error));
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

//        mEmptyView=(TextView)rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recent_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new RecentRecyclerViewAdapter(getActivity(), new RecentRecyclerViewAdapter.RecentAdapterOnClickHandler() {
            @Override
            public void onClick(RecentSearch search, RecentRecyclerViewAdapter.ViewHolder vh) {
//                mListener.onSearchSelectedListener(search);
                Intent intent = new Intent(getActivity(), JobService.class);
                intent.setAction(JobService.FETCH_PAGES_FROM_INTERNET);
                Job job=new Job();
                job.setTitle(search.getTitle());
                job.setLocation(search.getLocation());
                intent.putExtra(JobsFragment.JOB_TAG, job);
                getActivity().startService(intent);

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
                JobContract.RecentEntry.CONTENT_URI,
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

    public interface OnSearchSelectedListener{
        void onSearchSelectedListener(RecentSearch job);
    }

}
