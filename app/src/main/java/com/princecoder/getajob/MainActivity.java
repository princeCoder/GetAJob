package com.princecoder.getajob;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[] mDrawerTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    //To get the default view index to show
    private int defaultViewIndex = 0;

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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        mTitle = mDrawerTitle = getTitle();
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
        };
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
        outState.putInt(STATE_SELECTED_POSITION,defaultViewIndex);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int selectedindex=-1;
        if (id == R.id.nav_search) {// Handle search actions
            selectedindex=0;
        } else if (id == R.id.nav_my_jobs) {// Handle the jobs action
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
            getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

        }else if(position==1){
            fragment = new SavedJobFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
        else if(position==2){
            fragment = new AboutFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
        else if(position==3){
            fragment = new FeedbackFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
        }
        else{
            SettingFragment fragmentPreference=new SettingFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,fragmentPreference).commit();
        }

        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

}
