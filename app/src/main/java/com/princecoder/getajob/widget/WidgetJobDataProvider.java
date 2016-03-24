package com.princecoder.getajob.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.princecoder.getajob.R;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.utils.Utility;

import java.util.concurrent.ExecutionException;

/**
 * Created by Prinzly Ngotoum on 03/24/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetJobDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Cursor mCursor = null;

    Context mContext = null;

    public WidgetJobDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position))
            return mCursor.getLong(mCursor.getColumnIndex(JobContract.JobEntry._ID));
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.job_widget_item);
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.job_widget_item);
        if(mCursor!=null){

                mView.setTextViewText(R.id.job_title, mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.TITLE)));
                mView.setTextViewText(R.id.company, mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_NAME)));
                mView.setTextViewText(R.id.job_location,mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.LOCATION)));
                mView.setTextViewText(R.id.job_posted_date,Utility.getDayDifference(mCursor.getLong(mCursor.getColumnIndex(JobContract.JobEntry.POST_DATE))));
                Bitmap companyLogo = null;
                try {
                    companyLogo = Glide.with(mContext)
                            .load(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_LOGO)))
                            .asBitmap()
                            .error(R.mipmap.ic_launcher)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                } catch (InterruptedException | ExecutionException e) {
                   // Log.e(LOG_TAG, "Error retrieving large icon from " + homeCrestUrl, e);
                }

                if (companyLogo != null) {
                    mView.setImageViewBitmap(R.id.company_logo, companyLogo);
                }

                //Handle the click event

            Intent fillInIntent=new Intent();
                fillInIntent.setAction(WidgetProvider.ACTION_START_ATIVITY);
                mView.setOnClickFillInIntent(R.id.main_container, fillInIntent);

        }

        return mView;
    }


    private Job getCurrentJob() {
        Job job=new Job();
        job.setId(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry._ID)));
        job.setTitle(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.TITLE)));
        job.setLocation(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.LOCATION)));
        job.setCompanyName(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_NAME)));
        job.setCompanyLogo(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_LOGO)));
        job.setDescription(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.DESC)));
        job.setApplyUrl(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.APPLY_URL)));
        job.setUrl(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.URL)));
        job.setKeywords(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.KEYWORDS)));
        job.setJobType(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.TYPE)));
        job.setPerks(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.PERKS)));
        job.setRelocationAssistance(mCursor.getInt(mCursor.getColumnIndex(JobContract.JobEntry.RELOCATION_ASSISTANCE)));
        job.setPostDate(mCursor.getLong(mCursor.getColumnIndex(JobContract.JobEntry.POST_DATE)));
        job.setCompanyTagLine(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_TAG_LINE)));
        return job;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {

        if (mCursor != null) {
            mCursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        initData();
        Binder.restoreCallingIdentity(identityToken);
    }

    private void initData() {
        //We initialize the cursor
        mCursor = mContext.getContentResolver().query(JobContract.JobEntry.CONTENT_URI, null, null,null , null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

}