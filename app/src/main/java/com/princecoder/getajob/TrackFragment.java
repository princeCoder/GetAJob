package com.princecoder.getajob;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackFragment extends Fragment {

    private ViewPager mViewPager;

    public TrackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_track, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(1);
        return rootView;
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "Saved", "Recent"};
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
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return RecentFragment.newInstance();
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
