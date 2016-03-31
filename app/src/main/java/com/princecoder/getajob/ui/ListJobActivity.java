package com.princecoder.getajob.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.princecoder.getajob.R;
import com.princecoder.getajob.adapter.SearchJobRecyclerViewAdapter;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.service.JobService;

public class ListJobActivity extends AppCompatActivity implements JobsFragment.OnJobSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_jobs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onJobSelectedListener(Job job, SearchJobRecyclerViewAdapter.ViewHolder vh) { //We display details of this job
        if(job!=null){


            // shared element transition
            Bundle args = new Bundle();
            View sharedView=vh.mLogo;
            String tName= getString(R.string.transition_image) + String.valueOf(job.getId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sharedView.setTransitionName(tName);
                args = ActivityOptions.makeSceneTransitionAnimation(this, sharedView, tName).toBundle();
            }

            if(getResources().getBoolean(R.bool.muilti_columns)){//This is a Tablet
//                Bundle args = new Bundle();
                args.putParcelable(JobDetailFragment.CURRENT_JOB, job);

                JobDetailFragment fragment = new JobDetailFragment();
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.left_container, fragment, JobDetailFragment.DETAIL_TAG).commit();

            }
            else{ //This is a phone

                Intent intent = new Intent(this, JobDetailActivity.class)
                        .putExtra(JobDetailFragment.CURRENT_JOB, job);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent,args);
                }
                else{
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public void onJobSavedListener(Job job) { //We save this job
        if(job!=null){
            Intent bookIntent = new Intent(this, JobService.class);
            bookIntent.putExtra(JobService.JOB_TAG, job);
            bookIntent.setAction(JobService.SAVE_JOB);
            startService(bookIntent);
        }
    }
}
