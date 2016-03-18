package com.princecoder.getajob.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.princecoder.getajob.R;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.utils.Utility;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    //Current element selected
    private int selectedItem=-1;

    // Click handler callback
    JobAdapterOnClickHandler mCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.job_list_item, viewGroup, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException(mContext.getString(R.string.recycler_binding_error));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //This is where we will bind data to the recyclerView
        mCursor.moveToPosition(position);
        holder.mTitle.setText(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.TITLE)));
        holder.mCompany.setText(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_NAME)));
        holder.mLocation.setText(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.LOCATION)));
        holder.mDate.setText(Utility.getDayDifference(mContext,mCursor.getInt(mCursor.getColumnIndex(JobContract.JobEntry.POST_DATE))));
        Glide.with(holder.mLogo.getContext())
                .load(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_LOGO)))
                .fitCenter()
                .into(holder.mLogo);
    }

    @Override
    public int getItemCount() {
//        return 0;
        if ( null == mCursor ) return 0;
        int count=mCursor.getCount();
        return count;
    }


    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }



    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mLocation;
        public TextView mCompany;
        public TextView mDate;
        public ImageView mLogo;


        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.job_title);
            mLocation = (TextView) view.findViewById(R.id.job_location);
            mCompany = (TextView) view.findViewById(R.id.company);
            mDate = (TextView) view.findViewById(R.id.job_posted_date);
            mLogo = (ImageView) view.findViewById(R.id.company_logo);
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //Handle the click on an element
            if(selectedItem!=-1){
                notifyItemChanged(selectedItem);
            }
            setSelectedItem(getAdapterPosition());
            mCursor.moveToPosition(getSelectedItem());

            mCallback.onClick(getCurrentJob(), this);
            notifyItemChanged(getSelectedItem());
        }
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

    /**
     * Get the selected Item Index
     * @return
     */
    public int getSelectedItem() {
        return selectedItem;
    }


    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }


    // Constructor
    public RecyclerViewCursorAdapter(Context context, JobAdapterOnClickHandler vh) {
        mContext = context;
        mCallback = vh;
    }

    // Interface to handle the Item Click listener on the View Holder
    public interface JobAdapterOnClickHandler {
        void onClick(Job job, ViewHolder vh);
    }

}
