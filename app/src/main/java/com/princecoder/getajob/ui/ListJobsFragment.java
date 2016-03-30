package com.princecoder.getajob.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.service.JobService;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListJobsFragment extends Fragment{

    //Number of page
    private static  int NUM_PAGE;

    private boolean isNumPageUpdated=false;

    private Job jobParam;

    //ViewPager
    private ViewPager mViewPager;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ArrayList<ImageView> dots;
    public MyAdapter mAdapter;

    //Selected Page
    private int mSelectedItemId=1;


    public ListJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mPageReceiver,
                new IntentFilter(JobsFragment.PAGE));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mPageReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_list_jobs, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        pager_indicator = (LinearLayout) rootView.findViewById(R.id.viewPagerCountDots);
        mAdapter=new MyAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mSelectedItemId = position + 1;

                for (int i = 0; i < dotsCount; i++) {
                    dots.get(i).setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots.get(position).setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        if (savedInstanceState != null) {
            mSelectedItemId=savedInstanceState.getInt("defaultpage",1);
            NUM_PAGE=savedInstanceState.getInt("numPages",1);
            isNumPageUpdated=savedInstanceState.getBoolean("isNumPageUpdated");

            if(NUM_PAGE>1){
                            //We only show indicator if we have at least 2 pages
                pager_indicator.setVisibility(View.VISIBLE);
                setUiPageViewController();
            }
            else{
                pager_indicator.setVisibility(View.GONE);
                if(NUM_PAGE==0)
                    Snackbar.make(getView(), "No job found ", Snackbar.LENGTH_LONG).show();
            }

        }
        else{
            NUM_PAGE=1;
            mAdapter.notifyDataSetChanged();
        }
        jobParam=getActivity().getIntent().getParcelableExtra(JobsFragment.JOB_TAG);
        return rootView;
    }


    private void setUiPageViewController() {

        dotsCount = NUM_PAGE;
        dots = new ArrayList<>();

        for (int i = 0; i < dotsCount; i++) {
            dots.add(new ImageView(getActivity()));
            dots.get(i).setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots.get(i), params);
        }

        dots.get(mSelectedItemId-1).setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("defaultpage", mSelectedItemId);
        outState.putInt("numPages", NUM_PAGE);
        outState.putBoolean("isNumPageUpdated",isNumPageUpdated);
    }


    public static class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_PAGE;
        }

        @Override
        public Fragment getItem(int position) {
            return JobsFragment.newInstance(position + 1);
        }
    }

    //Broadcast receiver use to handle recent searches
    private BroadcastReceiver mPageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int n=intent.getIntExtra("page",1);
            if(!isNumPageUpdated){ // We only get the number of page to display one time
                isNumPageUpdated=true;
                dotsCount=NUM_PAGE=n;
                if(NUM_PAGE>1){
                    //We only show indicator if we have at least 2 pages
                    pager_indicator.setVisibility(View.VISIBLE);
                    //We only show indicator if we have at least 2 pages
                    pager_indicator.removeAllViews();
                    setUiPageViewController();

                    //We save the recent search
                    saveRecentSearch();
                }
                else{
                    pager_indicator.setVisibility(View.GONE);
                    if(NUM_PAGE==0){
                        Snackbar.make(getView(), "No job found ", Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        //We save the recent search
                        saveRecentSearch();
                    }

                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    private void saveRecentSearch(){
        RecentSearch search=new RecentSearch();
        search.setTitle(jobParam.getTitle());
        search.setLocation(jobParam.getLocation());
        Intent searchIntent = new Intent(getActivity(), JobService.class);
        searchIntent.setAction(JobService.SAVE_RECENT_SEARCH);
        searchIntent.putExtra(JobService.RECENT_TAG, search);
        getActivity().startService(searchIntent);
    }
}
