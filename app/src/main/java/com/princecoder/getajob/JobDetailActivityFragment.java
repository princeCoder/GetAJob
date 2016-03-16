package com.princecoder.getajob;

import android.os.Bundle;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class JobDetailActivityFragment extends Fragment {

    private Job mCurrentJob;
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

    //Job Tag
    public static final String CURRENT_JOB="CURRENT_JOB";


    public JobDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_job_detail, container, false);
        mCompanylogo= (ImageView) rootView.findViewById(R.id.company_logo);
        mJobTitle=(TextView)rootView.findViewById(R.id.job_title);
        mCompanyName=(TextView)rootView.findViewById(R.id.company);
        mLocation=(TextView)rootView.findViewById(R.id.job_location);
        mSaveButton=(Button)rootView.findViewById(R.id.save_btn);
        mApplyButton=(Button)rootView.findViewById(R.id.apply_btn_btn);
        mDescription=(TextView)rootView.findViewById(R.id.description);
        mJobType=(TextView)rootView.findViewById(R.id.type);
        mRelocationAssiatance=(TextView)rootView.findViewById(R.id.relocation);
        mPostedDate=(TextView)rootView.findViewById(R.id.posted);


        if(savedInstanceState==null){
            mCurrentJob=getActivity().getIntent().getParcelableExtra("Job");
        }
        else{
            if(savedInstanceState.containsKey(CURRENT_JOB)){
                mCurrentJob=savedInstanceState.getParcelable(CURRENT_JOB);
            }
        }

        bindData();

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_JOB,mCurrentJob);

    }

    public void bindData(){
        mJobTitle.setText(mCurrentJob.getTitle());
        mCompanyName.setText(mCurrentJob.getCompanyName());
        mLocation.setText(mCurrentJob.getLocation());
        mDescription.setText(Html.fromHtml(mCurrentJob.getDescription()));
        mJobType.setText(mCurrentJob.getJobType());
        mRelocationAssiatance.setText(mCurrentJob.getRelocationAssistance()==0?"No":"Yes");
        mPostedDate.setText(mCurrentJob.getPostDate());
        Glide.with(getActivity())
                .load(mCurrentJob.getCompanyLogo())
                .fitCenter()
                .into(mCompanylogo);
    }
}
