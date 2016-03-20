package com.princecoder.getajob.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prinzly Ngotoum on 3/17/16.
 */
public class RecentSearch implements Parcelable{
    int id;
    String title;
    String location;

    public RecentSearch(int id, String title, String location) {
        this.id = id;
        this.title = title;
        this.location = location;
    }

    public RecentSearch() {
        this.title = null;
        this.location = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    protected RecentSearch(Parcel in) {
        id = in.readInt();
        title = in.readString();
        location = in.readString();
    }

    public static final Creator<RecentSearch> CREATOR = new Creator<RecentSearch>() {
        @Override
        public RecentSearch createFromParcel(Parcel in) {
            return new RecentSearch(in);
        }

        @Override
        public RecentSearch[] newArray(int size) {
            return new RecentSearch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(location);
    }
}
