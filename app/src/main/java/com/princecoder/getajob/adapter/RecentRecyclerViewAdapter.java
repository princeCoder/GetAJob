package com.princecoder.getajob.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.princecoder.getajob.R;
import com.princecoder.getajob.data.JobContract;
import com.princecoder.getajob.model.RecentSearch;

/**
 * Created by Prinzly Ngotoum on 3/17/16.
 */
public class RecentRecyclerViewAdapter extends RecyclerView.Adapter<RecentRecyclerViewAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    // Click handler callback
    RecentAdapterOnClickHandler mCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_list_item, viewGroup, false);
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
        holder.mLocation.setText(mCursor.getString(mCursor.getColumnIndex(JobContract.JobEntry.LOCATION)));
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
        public ImageButton mAction;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.job_title);
            mLocation = (TextView) view.findViewById(R.id.job_location);
            mAction = (ImageButton) view.findViewById(R.id.action_button);
            view.setClickable(true);
            view.setOnClickListener(this);
            mAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.action_button){
                mCursor.moveToPosition(getAdapterPosition());
                mCallback.onDeleteRecentSearch(getCurrentSearch(), this);
            }
            else{
                //Handle the click on an element
                mCursor.moveToPosition(getAdapterPosition());

                mCallback.onClick(getCurrentSearch(), this);
            }

        }
    }

    private RecentSearch getCurrentSearch() {
        RecentSearch search=new RecentSearch();
        search.setId(mCursor.getInt(mCursor.getColumnIndex(JobContract.RecentEntry._ID)));
        search.setTitle(mCursor.getString(mCursor.getColumnIndex(JobContract.RecentEntry.TITLE)));
        search.setLocation(mCursor.getString(mCursor.getColumnIndex(JobContract.RecentEntry.LOCATION)));
        return search;
    }

    // Constructor
    public RecentRecyclerViewAdapter(Context context, RecentAdapterOnClickHandler vh) {
        mContext = context;
        mCallback = vh;
    }

    // Interface to handle the Item Click listener on the View Holder
    public interface RecentAdapterOnClickHandler {
        void onClick(RecentSearch search, ViewHolder vh);
        void onDeleteRecentSearch(RecentSearch search, ViewHolder vh);
    }

}