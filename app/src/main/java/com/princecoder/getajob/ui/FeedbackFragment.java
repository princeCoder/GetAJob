package com.princecoder.getajob.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.princecoder.getajob.JobApplication;
import com.princecoder.getajob.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    private Button mSendButton;
    private EditText mComment;
    private Tracker mTracker;
    private final String TAG=getClass().getSimpleName();
    
    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        JobApplication application = (JobApplication) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_feedback, container, false);
        mComment= (EditText) rootView.findViewById(R.id.comment_edt);
        mSendButton=(Button)rootView.findViewById(R.id.send_btn);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mComment.getText().toString().isEmpty()) {
                    //They send me an email adress
                    sendEmailIntent(mComment.getText().toString());
                }

            }
        });
        return rootView;
    }

    private void sendEmailIntent(String comment){
        String to = getActivity().getString(R.string.default_email);
        String subject = getActivity().getString(R.string.email_subject);
        String message = comment;

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        // need this to prompts email client only
        email.setType(getActivity().getString(R.string.email_message_type));

        startActivity(Intent.createChooser(email, getActivity().getString(R.string.chooser_message)));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Track the screen
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
