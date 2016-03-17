package com.princecoder.getajob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.princecoder.getajob.model.Job;

public class JobDetailActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {

            //We create the Top Track fragment and add it to the activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new JobDetailActivityFragment())
                    .commit();
        }
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Job job=getIntent().getParcelableExtra(JobDetailActivityFragment.CURRENT_JOB);
        //Set the title of the actionBar
        getSupportActionBar().setTitle(job.getCompanyName());
        if(job!=null){
            getSupportActionBar().setSubtitle(job.getTitle()+" Job detail");
        }
    }
}
