package com.princecoder.getajob.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;

import java.util.ArrayList;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class JobAdapterRecyclerView extends RecyclerView.Adapter<JobAdapterRecyclerView.ViewHolder> {
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
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public TextView mLocation;
        public TextView mCompany;
        public ImageView mLogo;

        //The row index
        public int position;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.job_title);
            mLocation = (TextView) view.findViewById(R.id.job_location);
            mCompany = (TextView) view.findViewById(R.id.company);
            mLogo = (ImageView) view.findViewById(R.id.company_logo);
            view.setClickable(true);

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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Handle the click on an element
                    if(selectedItem!=-1){
                        notifyItemChanged(selectedItem);
                    }
                    setSelectedItem(vh.getAdapterPosition());
                    mCallback.onClick(vh.getAdapterPosition(), vh);
                    notifyItemChanged(getSelectedItem());
                }
            });
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mElements.get(position).getTitle());
        holder.mCompany.setText(mElements.get(position).getCompanyName());
        holder.mLocation.setText(mElements.get(position).getLocation());
        Glide.with(holder.mLogo.getContext())
                .load(mElements.get(position).getCompanyLogo())
                .fitCenter()
                .into(holder.mLogo);
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
    }
}

