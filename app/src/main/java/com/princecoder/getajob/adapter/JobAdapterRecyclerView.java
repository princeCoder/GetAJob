package com.princecoder.getajob.adapter;

import android.annotation.TargetApi;
import android.content.Context;
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
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobAdapterRecyclerView extends RecyclerView.Adapter<JobAdapterRecyclerView.ViewHolder>{
    // Context
    Context mContext;

    /**
     * List of elements
     * I call elements (Artists or Tracks).
     *
     */
    ArrayList<Job> mElements;
    final ViewHolderOnClickHandler mCallback;

    //Current element selected
    private int selectedItem=-1;

    public JobAdapterRecyclerView(Context c,ViewHolderOnClickHandler vh) {
        mContext = c;
        mCallback=vh;
    }


    //View holder class which help to recycle row view elements
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;
        public TextView mLocation;
        public TextView mCompany;
        public TextView mDate;
        public ImageView mLogo;
        public ImageButton mAction;

        //The row index
        public int position;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.job_title);
            mLocation = (TextView) view.findViewById(R.id.job_location);
            mCompany = (TextView) view.findViewById(R.id.company);
            mDate = (TextView) view.findViewById(R.id.job_posted_date);
            mLogo = (ImageView) view.findViewById(R.id.company_logo);
            mAction = (ImageButton) view.findViewById(R.id.action_button);
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

            mCallback.onClick(getLayoutPosition(), this);
            notifyItemChanged(getSelectedItem());
        }


    }
    public Job getItem(int position) {
        // TODO Auto-generated method stub
        return mElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(parent instanceof RecyclerView){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_list_item, parent, false);
            view.setFocusable(true);
            final ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        else {
            throw new RuntimeException(mContext.getString(R.string.recycler_binding_error));
        }
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


    public ArrayList<Job> getElements() {
        return mElements;
    }

    public void setElements(ArrayList<Job> mElements) {
        this.mElements = mElements;
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitle.setText(mElements.get(position).getTitle());
        holder.mCompany.setText(mElements.get(position).getCompanyName());
        holder.mLocation.setText(mElements.get(position).getLocation());
        holder.mDate.setText(Utility.getDayDifference(mElements.get(position).getPostDate()));
        Glide.with(holder.mLogo.getContext())
                .load(mElements.get(position).getCompanyLogo())
                .fitCenter()
                .into(holder.mLogo);
        holder.itemView.setSelected(getSelectedItem() == position);
    }


    @Override
    public int getItemCount() {
        return mElements!=null?mElements.size():0;
    }

    //Swap element inside the Adapter
    public void swapElements(ArrayList<Job> list) {
        mElements = list;
        notifyDataSetChanged();
    }

    public interface ViewHolderOnClickHandler{
        void onClick(int id, ViewHolder vh);
        void onSaveJob(int id, ViewHolder vh);
    }
}

