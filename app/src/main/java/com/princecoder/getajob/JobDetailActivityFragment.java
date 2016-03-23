package com.princecoder.getajob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.sync.JobService;
import com.princecoder.getajob.utils.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class JobDetailActivityFragment extends Fragment {


    private ImageView mCompanylogo;
    private TextView mJobTitle;
    private TextView mLocation;
    private TextView mCompanyName;
    private Button mSaveButton;
    private Button mApplyButton;

    private TextView mJobType;
    private TextView mRelocationAssiatance;
    private TextView mDescription;
    private TextView mPostedDate;
    private TextView mKeywords;

    //Job Tag
    public static final String CURRENT_JOB="CURRENT_JOB";

    //Current job
    private Job mCurrentJob;

    public JobDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //register the receiver
        getActivity().registerReceiver(mServiceSaveJobReceiver,
                new IntentFilter(JobService.SAVE_JOB));

        getActivity().registerReceiver(mServiceJobExistReceiver,
                new IntentFilter(JobService.DOES_JOB_EXIST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_job_detail, container, false);

        if(savedInstanceState==null){
            Intent intent=getActivity().getIntent();
            if((intent!=null)&&(intent.hasExtra(CURRENT_JOB))){ //Phones
                mCurrentJob=getActivity().getIntent().getParcelableExtra(CURRENT_JOB);
            }
            else{ //Tablet
                Bundle arguments = getArguments();
                if (arguments != null) {
                    mCurrentJob = arguments.getParcelable(CURRENT_JOB);
                }
            }

        }
        else{
            if(savedInstanceState.containsKey(CURRENT_JOB)){
                mCurrentJob=savedInstanceState.getParcelable(CURRENT_JOB);
            }
        }

        mCompanylogo= (ImageView) rootView.findViewById(R.id.company_logo);
        mJobTitle=(TextView)rootView.findViewById(R.id.job_title);
        mCompanyName=(TextView)rootView.findViewById(R.id.company);
        mLocation=(TextView)rootView.findViewById(R.id.job_location);
        mSaveButton=(Button)rootView.findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJob(mCurrentJob);
            }
        });
        mApplyButton=(Button)rootView.findViewById(R.id.apply_btn);
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mCurrentJob.getApplyUrl()));
                startActivity(i);
            }
        });
        mDescription=(TextView)rootView.findViewById(R.id.description);
        mJobType=(TextView)rootView.findViewById(R.id.type);
        mRelocationAssiatance=(TextView)rootView.findViewById(R.id.relocation);
        mPostedDate=(TextView)rootView.findViewById(R.id.posted);
        mKeywords=(TextView)rootView.findViewById(R.id.keyword);
        bindData();

        return rootView;
    }

    //Save the current job in the database
    private void saveJob(Job job) {
        Intent jobIntent = new Intent(getActivity(), JobService.class);
        jobIntent.putExtra(JobService.JOB_TAG, job);
        jobIntent.setAction(JobService.SAVE_JOB);
        getActivity().startService(jobIntent);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the brodcast receiver
        getActivity().unregisterReceiver(mServiceSaveJobReceiver);
        getActivity().unregisterReceiver(mServiceJobExistReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent=new Intent(getActivity(),JobService.class);
        intent.setAction(JobService.DOES_JOB_EXIST);
        intent.putExtra(JobService.JOB_TAG, mCurrentJob);
        getActivity().startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_JOB,mCurrentJob);

    }

    public void bindData(){
        Glide.with(getActivity())
                .load(mCurrentJob.getCompanyLogo())
                .fitCenter()
                .into(mCompanylogo);
        mJobTitle.setText(mCurrentJob.getTitle());
        mCompanyName.setText(mCurrentJob.getCompanyName());
        mLocation.setText(mCurrentJob.getLocation());
        mDescription.setText(Html.fromHtml(mCurrentJob.getDescription()));
        mJobType.setText(mCurrentJob.getJobType());
        mRelocationAssiatance.setText(mCurrentJob.getRelocationAssistance()==0?"No":"Yes");
        mPostedDate.setText(Utility.getFormattedMonthDayYear(mCurrentJob.getPostDate()));
        mKeywords.setText(mCurrentJob.getKeywords());


    }

    private BroadcastReceiver mServiceSaveJobReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.SAVE_JOB.equals(intent.getAction())) {
                String message = intent.getStringExtra(JobService.MESSAGE);
                Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                mSaveButton.setVisibility(View.GONE);
            }
        }
    };

    private BroadcastReceiver mServiceJobExistReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (JobService.DOES_JOB_EXIST.equals(intent.getAction())) {
                boolean message = intent.getBooleanExtra(JobService.DOES_JOB_EXIST, false);
                mSaveButton.setVisibility(message?View.GONE:View.VISIBLE);
            }
        }
    };

}
