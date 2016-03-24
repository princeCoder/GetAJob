package com.princecoder.getajob;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListJobsFragment extends Fragment{

    //Number of page
    private static  int NUM_PAGE=1;

    //Num Page Tag
    public static String NUM_PAGE_TAG="NUM_PAGE";

    //ViewPager
    private ViewPager mViewPager;

    private ImageButton btnNext, btnFinish;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;

    //Selected Page
    private int mSelectedItemId=2;


    public ListJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_list_jobs, container, false);

        //Set up the number of pages
        NUM_PAGE=getActivity().getIntent().getIntExtra(NUM_PAGE_TAG,1);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        pager_indicator = (LinearLayout) rootView.findViewById(R.id.viewPagerCountDots);

        mViewPager.setAdapter(new MyAdapter(getFragmentManager()));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedItemId = position + 1;

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        if (savedInstanceState == null) {
                mSelectedItemId = 1;

        }
        else {
            mSelectedItemId=savedInstanceState.getInt("defaultpage",1);
        }
        if(NUM_PAGE>1) //We only show indicator if we have at least 2 pages
        setUiPageViewController();
        return rootView;
    }


    private void setUiPageViewController() {

        dotsCount = NUM_PAGE;
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(NUM_PAGE==0){
            Snackbar.make(getView(), "No job found ", Snackbar.LENGTH_LONG).show();
        }
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("defaultpage",mSelectedItemId);
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
            return JobsFragment.newInstance(position+1);
        }
    }


}
