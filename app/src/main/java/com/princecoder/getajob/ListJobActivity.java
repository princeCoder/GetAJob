package com.princecoder.getajob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.sync.JobService;

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
    public void onJobSelectedListener(Job job) { //We display details of this job
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
    public void onJobSavedListener(Job job) { //We save this job
        if(job!=null){
            Intent bookIntent = new Intent(this, JobService.class);
            bookIntent.putExtra(JobService.JOB_TAG, job);
            bookIntent.setAction(JobService.SAVE_JOB);
            startService(bookIntent);
        }
    }
}
