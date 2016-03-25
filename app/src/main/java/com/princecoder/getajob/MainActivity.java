package com.princecoder.getajob;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.model.RecentSearch;
import com.princecoder.getajob.service.JobService;
import com.princecoder.getajob.sync.JobSyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SavedJobFragment.OnJobSelectedListener, SearchFragment.OnSearchSelectedListener{

    private String[] mDrawerTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    //To get the default view index to show
    private int defaultViewIndex = 0;
    private Tracker mTracker;

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerTitles = getResources().getStringArray(R.array.nav_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            defaultViewIndex = savedInstanceState.getInt(STATE_SELECTED_POSITION);

        }else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            defaultViewIndex = Integer.parseInt(prefs.getString("pref_startFragment","0"));
            //Set the view to display by default
            selectItem(defaultViewIndex);
        }
        // Obtain the shared Tracker instance.
        JobApplication application = (JobApplication) getApplication();
        mTracker = application.getDefaultTracker();


        JobSyncAdapter.initializeSyncAdapter(this);

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //There is nothing to show
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // Save the default view index
        outState.putInt(STATE_SELECTED_POSITION, defaultViewIndex);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int selectedindex=-1;
        if (id == R.id.nav_search) {// Handle search actions
            selectedindex=0;
        } else if (id == R.id.nav_myJobs) {// Handle saved jobs action
            selectedindex=1;
        }
        else if (id == R.id.nav_about) { // Handle the about action
            selectedindex=2;
        } else if (id == R.id.nav_feedback) {// Handle feedback actions
            selectedindex=3;
        }
        else if (id == R.id.nav_setting) {// Handle settings actions
            selectedindex=4;
        }
        selectItem(selectedindex);
        return true;
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment ;
        if(position==0){

            fragment= new SearchFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

        }else if(position==1){

            fragment = new SavedJobFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
        else if(position==2){
            fragment = new AboutFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
        else if(position==3){
            fragment = new FeedbackFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
//        else{
//            SettingFragment fragmentPreference=new SettingFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentPreference).commit();
//        }

        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onJobSelectedListener(Job job) {
        if(job!=null){
            if(getResources().getBoolean(R.bool.muilti_columns)){//This is a Tablet
                Bundle args = new Bundle();
                args.putParcelable(JobDetailActivityFragment.CURRENT_JOB, job);

                JobDetailActivityFragment fragment = new JobDetailActivityFragment();
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.left_container, fragment, "Detail_fragment").commit();

            }
            else{ //This is a phone
                Intent intent = new Intent(this, JobDetailActivity.class)
                        .putExtra(JobDetailActivityFragment.CURRENT_JOB, job);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDeleteJobListener(Job job) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you realy want to remove it from your saved jobs?");
        final Job j=job;
        final Intent intent = new Intent(this, JobService.class);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                intent.setAction(JobService.DELETE_JOB);
                intent.putExtra(JobsFragment.JOB_TAG, j);
                startService(intent);

                if(getResources().getBoolean(R.bool.muilti_columns)){
                    JobDetailActivityFragment fragment = (JobDetailActivityFragment)getSupportFragmentManager().findFragmentByTag("Detail_fragment");
                    if(fragment!=null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
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

    @Override
    public void onSearchSelectedListener(RecentSearch search) {
        // We question the Service to get the number of pages
        Intent intent = new Intent(this, JobService.class);
        intent.setAction(JobService.FETCH_PAGES_FROM_INTERNET);
        Job job=new Job();
        job.setTitle(search.getTitle());
        job.setLocation(search.getLocation());
        intent.putExtra(JobsFragment.JOB_TAG, job);
        startService(intent);
    }

    public void updateWidgets() {
        Context context = getApplicationContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(JobSyncAdapter.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Main Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}


