package com.princecoder.getajob;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.princecoder.getajob.sync.JobService;

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
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapterViewPager;
    private Toolbar mToolbar;
    private int mNumPage;
    private Job mJob;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //register the receiver
        getActivity().registerReceiver(mServicePagesReceiver,
                new IntentFilter(JobService.SERVICE_PAGES));
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

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mAdapterViewPager = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(new MyPagerAdapter(getFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mServicePagesReceiver);
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


    //Search Jobs

    public void searchJobs() {

        //Get input data
        String title = mTitleEdt.getText().toString();
        String location = mLocation.getText().toString();

        //Create job object
        mJob = new Job();
        mJob.setTitle(title);
        mJob.setLocation(location);

        // We question the Service to get the number of pages
        Intent intent = new Intent(getActivity(), JobService.class);
        intent.setAction(JobService.FETCH_PAGES_FROM_INTERNET);
        intent.putExtra(JobsFragment.JOB_TAG, mJob);
        getActivity().startService(intent);

    }


    private BroadcastReceiver mServicePagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SERVICE_PAGES.equals(intent.getAction())) {
                mNumPage=intent.getIntExtra(JobService.PAGE_TAG,1);
                //Start new activity
                Intent intent1 = new Intent(getActivity(), ListJobActivity.class);
                intent1.putExtra(JobsFragment.JOB_TAG, mJob);
                intent1.putExtra(ListJobsFragment.NUM_PAGE_TAG, mNumPage);
                getActivity().startActivity(intent1);

            }
        }
    };


    public static class MyPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 1;
        private String tabTitles[] = new String[] { "Saved"};
        private Context context;

        public MyPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return SavedJobFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];
        }

    }


}
