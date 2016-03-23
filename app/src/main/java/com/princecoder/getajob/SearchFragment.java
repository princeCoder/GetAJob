package com.princecoder.getajob;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.princecoder.getajob.adapter.RecentRecyclerViewAdapter;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.JobModel;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.sync.JobService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * create an instance of fragment to make search
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mTitleEdt;
    private EditText mLocation;
    private Button mSearchBtn;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Toolbar mToolbar;
    private int mNumPage;
    private Job mJob=new Job();


    private RecyclerView mRecyclerView;
    private RecentRecyclerViewAdapter mAdapter;
    //    private TextView mEmptyView;
    private static final int CURSOR_LOADER_ID = 0;

    // Log field
    private final String TAG=getClass().getSimpleName();

    //Listener
    OnSearchSelectedListener mListener;

    public SearchFragment() {
        // Required empty public constructor
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //register the receiver
        getActivity().registerReceiver(mServicePagesReceiver,
                new IntentFilter(JobService.SERVICE_PAGES));

        getActivity().registerReceiver(mRecentSearchReceiver,
                new IntentFilter(JobService.SAVE_RECENT_SEARCH));
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
                //Let find the number of pages
                getNumberOfPage(mTitleEdt.getText().toString(), mLocation.getText().toString());
            }
        });

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

    //Handle the click on the recyclerview
    public interface OnSearchSelectedListener{
        void onSearchSelectedListener(RecentSearch job);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mServicePagesReceiver);
        getActivity().unregisterReceiver(mRecentSearchReceiver);
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

//    get the number of page for the ViewPager

    private void getNumberOfPage(String title, String location) {
        mJob.setTitle(title);
        mJob.setLocation(location);
        // We question the Service to get the number of pages
        Intent intent = new Intent(getActivity(), JobService.class);
        intent.setAction(JobService.FETCH_PAGES_FROM_INTERNET);
        intent.putExtra(JobsFragment.JOB_TAG, mJob);
        getActivity().startService(intent);
    }



    //Search Jobs

    public void searchJobs(Job job) {

    }

    //Number of pages needed to display all the jobs
    private BroadcastReceiver mServicePagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SERVICE_PAGES.equals(intent.getAction())) {
                //Once we retreive the number of page need to display jobs in the ViewPager,
                // we can go get jobs from the server
                mNumPage=intent.getIntExtra(JobService.PAGE_TAG,1);
                mJob=intent.getParcelableExtra(JobService.JOB_TAG);

                //Start new activity
                Intent intent1 = new Intent(getActivity(), ListJobActivity.class);
                intent1.putExtra(JobsFragment.JOB_TAG, mJob);
                intent1.putExtra(ListJobsFragment.NUM_PAGE_TAG, mNumPage);
                getActivity().startActivity(intent1);

                if(mNumPage>0){ //We found jobs. So we need to save that recent search

                    RecentSearch search=new RecentSearch();
                    search.setTitle(mJob.getTitle());
                    search.setLocation(mJob.getLocation());

                    //We send an Intent to the service to save this recent search
                    Intent searchIntent = new Intent(getActivity(), JobService.class);
                    searchIntent.setAction(JobService.SAVE_RECENT_SEARCH);
                    searchIntent.putExtra(JobService.RECENT_TAG, search);
                    getActivity().startService(searchIntent);
                }

            }
        }
    };


    private BroadcastReceiver mRecentSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SAVE_RECENT_SEARCH.equals(intent.getAction())) {
                String message = intent.getStringExtra(JobService.MESSAGE);
                Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
            }
        }
    };

}
