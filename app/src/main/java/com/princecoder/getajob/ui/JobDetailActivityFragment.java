package com.princecoder.getajob.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.princecoder.getajob.JobApplication;
import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.service.JobService;
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

    private Tracker mTracker;

    //Job Tag
    public static final String CURRENT_JOB="CURRENT_JOB";

    //Current job
    private Job mCurrentJob;

    //Share action provider
    ShareActionProvider mShareActionProvider;

    MenuItem item;

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
        setHasOptionsMenu(true);

        // Obtain the shared Tracker instance.
        JobApplication application = (JobApplication) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_job_detail, menu);
        item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = new ShareActionProvider(getActivity());
        mShareActionProvider.setShareIntent(createShareIntent(mCurrentJob.getUrl()));
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
    }


    private Intent createShareIntent(String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                message);
        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_job_detail, container, false);

//        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCompanylogo.setTransitionName(getString(R.string.transition_image) + String.valueOf(mCurrentJob.getId()));
        }
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
        mKeywords = (TextView) rootView.findViewById(R.id.keyword);

        bindData();
        // Apply the content transition
        bodyTextAnimation();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Track the screen
        mTracker.setScreenName("Detail_Fragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        outState.putParcelable(CURRENT_JOB, mCurrentJob);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleStartPostponedTransition(mCompanylogo);
        }

    }

    /**
     * Set linear animation for the text
     * This is a content animation
     */
    private void bodyTextAnimation() {
        Slide slide = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.description);
            slide.excludeTarget(R.id.detail_container, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                slide.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear));
            }
            //Set the duration just right
            slide.setDuration(300);
            //Set the enter transition
            getActivity().getWindow().setEnterTransition(slide);
        }
    }

    //Postpone the transition on a sharedElement
    //In my case it will be the imageView in the collapsedToolbarlayout
    private void scheduleStartPostponedTransition(final View sharedElement) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElement.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getActivity().startPostponedEnterTransition();
                            }
                            return true;
                        }
                    });
        }
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
