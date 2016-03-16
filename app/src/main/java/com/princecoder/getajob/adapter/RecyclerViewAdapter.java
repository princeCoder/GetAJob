package com.princecoder.getajob.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    //Current element selected
    private int selectedItem=-1;

    // Click handler callback
    BookAdapterOnClickHandler mCallback;

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
    }

    @Override
    public int getItemCount() {
//        return 0;
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
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
        public final TextView jobTitle;
        public final TextView jobLocation;


        public ViewHolder(View view) {
            super(view);
            jobTitle = (TextView) view.findViewById(R.id.job_title);
            jobLocation = (TextView) view.findViewById(R.id.job_location);

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
    public RecyclerViewAdapter(Context context, BookAdapterOnClickHandler vh) {
        mContext = context;
        mCallback = vh;
    }

    // Interface to handle the Item Click listener on the View Holder
    public interface BookAdapterOnClickHandler {
        void onClick(Job book, ViewHolder vh);
    }

}
