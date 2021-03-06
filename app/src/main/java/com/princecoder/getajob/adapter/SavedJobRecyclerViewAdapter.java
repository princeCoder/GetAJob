package com.princecoder.getajob.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.princecoder.getajob.R;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.utils.Utility;

/** recyclerView for Saved Job
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class SavedJobRecyclerViewAdapter extends RecyclerView.Adapter<SavedJobRecyclerViewAdapter.ViewHolder> {

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
        holder.mDate.setText(Utility.getDayDifference(mCursor.getLong(mCursor.getColumnIndex(JobContract.JobEntry.POST_DATE))));
        Glide.with(holder.mLogo.getContext())
                .load(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.COMPANY_LOGO)))
                .fitCenter()
                .into(holder.mLogo);
        holder.itemView.setSelected(selectedItem == position);
    }

    @Override
    public int getItemCount() {
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
        public ImageButton mAction;


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.job_title);
            mLocation = (TextView) view.findViewById(R.id.job_location);
            mCompany = (TextView) view.findViewById(R.id.company);
            mDate = (TextView) view.findViewById(R.id.job_posted_date);
            mLogo = (ImageView) view.findViewById(R.id.company_logo);
            mAction = (ImageButton) view.findViewById(R.id.action_button);
            mAction.setVisibility(View.VISIBLE);
            view.setClickable(true);
            view.setOnClickListener(this);
            mAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.action_button){
                mCallback.onDeleteJob(getCurrentJob(), this);
            }
            else{
                if(selectedItem!=-1){
                    notifyItemChanged(selectedItem);
                }
                setSelectedItem(getLayoutPosition());
                mCursor.moveToPosition(getSelectedItem());

                mCallback.onClick(getCurrentJob(), this);
                notifyItemChanged(getSelectedItem());
            }

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
    public SavedJobRecyclerViewAdapter(Context context, JobAdapterOnClickHandler vh) {
        mContext = context;
        mCallback = vh;
    }

    // Interface to handle the Item Click listener on the View Holder
    public interface JobAdapterOnClickHandler {
        void onClick(Job job, ViewHolder vh);
        void onDeleteJob(Job job, ViewHolder vh);
    }

}
