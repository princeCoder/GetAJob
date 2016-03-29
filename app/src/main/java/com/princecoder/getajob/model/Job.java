package com.princecoder.getajob.model;

import android.os.Parcel;
import android.os.Parcelable;

/** Job POJO
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class Job implements Parcelable{
    private String id;
    private String title;
    private String description;
    private String perks;
    private long postDate;
    private int relocationAssistance;
    private String location;
    private String companyName;
    private String companyLogo;
    private String keywords;
    private String url;
    private String applyUrl;
    private String companyTagLine;
    private String jobType;
    public Job(String id, String title, String description, String perks, long postDate, int relocationAssistance, String location, String companyName, String companyLogo, String keywords, String url, String applyUrl, String companyTagLine, String jobType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.perks = perks;
        this.postDate = postDate;
        this.relocationAssistance = relocationAssistance;
        this.location = location;
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.keywords = keywords;
        this.url = url;
        this.applyUrl = applyUrl;
        this.companyTagLine = companyTagLine;
        this.jobType=jobType;
    }



    public Job(){
        this.id = null;
        this.title = null;
        this.description = null;
        this.perks = null;
        this.postDate=0;
        this.relocationAssistance = 0;
        this.location = null;
        this.companyName = null;
        this.companyLogo = null;
        this.keywords = null;
        this.url = null;
        this.applyUrl = null;
        this.companyTagLine = null;
        this.jobType=null;
    }


    protected Job(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        perks = in.readString();
        postDate = in.readLong();
        relocationAssistance = in.readInt();
        location = in.readString();
        companyName = in.readString();
        companyLogo = in.readString();
        keywords = in.readString();
        url = in.readString();
        applyUrl = in.readString();
        companyTagLine = in.readString();
        jobType=in.readString();;
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerks() {
        return perks;
    }

    public void setPerks(String perks) {
        this.perks = perks;
    }

    public long getPostDate() {
        return postDate;
    }

    public void setPostDate(long postDate) {
        this.postDate = postDate;
    }

    public int getRelocationAssistance() {
        return relocationAssistance;
    }

    public void setRelocationAssistance(int relocationAssistance) {
        this.relocationAssistance = relocationAssistance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getCompanyTagLine() {
        return companyTagLine;
    }

    public void setCompanyTagLine(String companyTagLine) {
        this.companyTagLine = companyTagLine;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(perks);
        parcel.writeLong(postDate);
        parcel.writeInt(relocationAssistance);
        parcel.writeString(location);
        parcel.writeString(companyName);
        parcel.writeString(companyLogo);
        parcel.writeString(keywords);
        parcel.writeString(url);
        parcel.writeString(applyUrl);
        parcel.writeString(companyTagLine);
        parcel.writeString(jobType);
    }
}
