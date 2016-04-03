package com.princecoder.getajob.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;

public class JobDetailActivity extends AppCompatActivity{

    public Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {

            //We create the Top Track fragment and add it to the activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new JobDetailFragment())
                    .commit();
        }

        // Postpone the shared element enter transition.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Job job=getIntent().getParcelableExtra(JobDetailFragment.CURRENT_JOB);
        //Set the title of the actionBar
        getSupportActionBar().setTitle(job.getCompanyName());
        if(job!=null){
            getSupportActionBar().setSubtitle(job.getTitle());
        }
    }

}
