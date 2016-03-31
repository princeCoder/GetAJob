package com.princecoder.getajob.ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.princecoder.getajob.JobApplication;
import com.princecoder.getajob.R;
import com.princecoder.getajob.adapter.RecentRecyclerViewAdapter;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.service.JobService;
import com.princecoder.getajob.utils.Utility;

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
    private Job mJob=new Job();


    private RecyclerView mRecyclerView;
    private RecentRecyclerViewAdapter mAdapter;

    //    private TextView mEmptyView;
    private static final int CURSOR_LOADER_ID = 0;

    // Log field
    private final String TAG=getClass().getSimpleName();

    //Listener
    OnSearchSelectedListener mListener;

    private Tracker mTracker;

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

        getActivity().registerReceiver(mDeleteRecentReceiver,
                new IntentFilter(JobService.DELETE_RECENT_SEARCH));

        // Obtain the shared Tracker instance.
        JobApplication application = (JobApplication) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_search, container, false);

        mTitleEdt= (EditText) rootView.findViewById(R.id.title_edt);
        mTitleEdt.setFocusable(true);

        //The Location Textview should not be editable
        mLocation=(EditText)rootView.findViewById(R.id.location_edt);
        mLocation.setTag(mLocation.getKeyListener());
        mLocation.setKeyListener(null);

        //You should click on the textView in order to enter your location
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display a Listview where ser can choose the location
                setLocations();
            }
        });

        mSearchBtn=(Button)rootView.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Let find the number of pages
                String title=mTitleEdt.getText().toString().trim();
                String location=mLocation.getText().toString().trim();
                if(title.isEmpty()&&location.isEmpty()){
                    //We display a snackBar
                    Snackbar.make(getView(), getActivity().getString(R.string.criteria_alert) +
                            "", Snackbar.LENGTH_LONG).show();
                }else{

                    mJob.setTitle(title);
                    mJob.setLocation(location);

                    Intent intent1 = new Intent(getActivity(), ListJobActivity.class);
                    intent1.putExtra(JobsFragment.JOB_TAG, mJob);
                    getActivity().startActivity(intent1);
                }

            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_recent_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new RecentRecyclerViewAdapter(getActivity(), new RecentRecyclerViewAdapter.RecentAdapterOnClickHandler() {
            @Override
            public void onClick(RecentSearch search, RecentRecyclerViewAdapter.ViewHolder vh) {

                //Create an instance of job
                Job job=new Job();
                job.setTitle(search.getTitle());
                job.setLocation(search.getLocation());

                Intent intent1 = new Intent(getActivity(), ListJobActivity.class);
                intent1.putExtra(JobsFragment.JOB_TAG, job);
                getActivity().startActivity(intent1);
            }

            @Override
            public void onDeleteRecentSearch(final RecentSearch search, RecentRecyclerViewAdapter.ViewHolder vh) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.delete_search_message));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getActivity(), JobService.class);
                        intent.setAction(JobService.DELETE_RECENT_SEARCH);
                        intent.putExtra(JobService.RECENT_TAG, search);
                        getActivity().startService(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        //Whenever I resume this fragment, I restart the loader
        restartLoader();

        //Track the screen
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mDeleteRecentReceiver);
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



    //Restart the loader
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


    //Fecth locations in the listiew of location displayed to the user depending of the searchstring
    public void fetchLocations(String searchString, ListView listview) {
        Iterator iterator= Utility.myLocations.keySet().iterator();
        ArrayList<String> list=new ArrayList<>(Utility.myLocations.values().size());
        while(iterator.hasNext()){
            String key=iterator.next().toString();
            if(key.toLowerCase().contains(searchString.trim().toLowerCase()))
                list.add(key);
        }
        Collections.sort(list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }

    //Set the list of locations use by the API
    public void setLocations() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.cities_listview);
        final ListView listView = (ListView) dialog.findViewById(R.id.list);
        final SearchView  searchText = (SearchView) dialog.findViewById(R.id.searchText);
        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getActivity().getString(R.string.search_text_hint));

        Iterator iterator=Utility.myLocations.keySet().iterator();
        final ArrayList<String> list=new ArrayList<>(Utility.myLocations.values().size());
        Collections.sort(list);
        while(iterator.hasNext()){
            list.add(iterator.next().toString());
        }

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

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String searchKeyword = searchText.getQuery().toString();
                if (!searchKeyword.isEmpty()) {
                    fetchLocations(searchKeyword, listView);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    listView.setAdapter(adapter);

                } else {
                    fetchLocations(s, listView);

                }
                return false;
            }
        });
        dialog.show();
    }

    //Bradcast receiver use to delete a recent search
    private BroadcastReceiver mDeleteRecentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.DELETE_RECENT_SEARCH.equals(intent.getAction())) {
                restartLoader();
            }
        }
    };


    //Handle the click on the recyclerview
    public interface OnSearchSelectedListener{
        void onSearchSelectedListener(RecentSearch job);
    }

}
