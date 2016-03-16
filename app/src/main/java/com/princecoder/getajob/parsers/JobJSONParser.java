package com.princecoder.getajob.parsers;

import android.content.Context;

import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.utils.L;
import com.princecoder.getajob.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prinzly Ngotoum on 3/15/16.
 */
public class JobJSONParser {

    //log Tag
    private final static String LOG_TAG = JobJSONParser.class.getSimpleName();

    public static List<Job> parseFeed(Context context, String content) {

        // Job information
        final String JOB_POST_ID = "id";
        final String JOB_TITLE = "title";
        final String JOB_DESCRIPTION = "description";
        final String JOB_PERKS = "perks";
        final String JOB_POSTDATE = "post_date";
        final String JOB_RELOCATION_ASSISTANCE = "relocation_assistance";
        final String JOB_LOCATION = "name";
        final String JOB_COMPANY_LOGO = "logo";
        final String JOB_COMPANY_NAME = "name";
        final String JOB_KEYWORDS = "keywords";
        final String JOB_URL = "url";
        final String JOB_APPLY_URL = "apply_url";
        final String JOB_COMPANY_TAG_LINE = "tagline";
        final String COMPANY = "company";
        final String LOCATION = "location";
        final String LON = "lng";
        final String LAT = "lat";
        final String TYPE = "type";
        final String TYPE_NAME = "name";


        JSONObject jobObject;
        String postId="";
        String title="";
        String description="";
        String perks="";
        String location="";
        String postdate="";
        int relocationAssistance=0;
        String keywords="";
        String applyUrl="";
        String url="";
        String companyName="";
        String companyLogo="";
        String tagLine="";
        String type="";

        try{
            JSONObject jobsJson = new JSONObject(content).getJSONObject("listings");
            JSONArray jobsArray = jobsJson.getJSONArray("listing");

            ArrayList<Job> jobArrayList = new ArrayList<Job>(jobsArray.length());


            for(int i = 0; i < jobsArray.length(); i++) {

                jobObject=jobsArray.getJSONObject(i);
                postId=jobObject.has(JOB_POST_ID) ? jobObject.getString(JOB_POST_ID):"";
                title=jobObject.has(JOB_TITLE)?jobObject.getString(JOB_TITLE):"";
                description=jobObject.has(JOB_DESCRIPTION)?jobObject.getString(JOB_DESCRIPTION):"";
                perks=jobObject.has(JOB_PERKS)?jobObject.getString(JOB_PERKS):"";
                postdate=jobObject.has(JOB_POSTDATE)?jobObject.getString(JOB_POSTDATE):"";
                relocationAssistance=jobObject.has(JOB_RELOCATION_ASSISTANCE)?jobObject.getInt(JOB_RELOCATION_ASSISTANCE):0;
                keywords=jobObject.has(JOB_KEYWORDS)?jobObject.getString(JOB_KEYWORDS):"";
                applyUrl=jobObject.has(JOB_APPLY_URL)?jobObject.getString(JOB_APPLY_URL):"";
                url=jobObject.has(JOB_URL)?jobObject.getString(JOB_URL):"";
                type=jobObject.has(TYPE)?jobObject.getJSONObject(TYPE).getString(TYPE_NAME):"Not Specified";

                //Company Object
                if(jobObject.has(COMPANY)){
                    JSONObject company=jobObject.getJSONObject(COMPANY);

                    if(company.has(LOCATION)){
                        JSONObject locationObject=company.getJSONObject(LOCATION);
                        String lat=locationObject.getString(LAT);
                        String lgn=locationObject.getString(LON);
                        location=Utility.getLocationFromLonLat(context,Double.parseDouble(lgn),Double.parseDouble(lat));
                    }
                    if(company.has(JOB_COMPANY_NAME)){
                        companyName=company.getString(JOB_COMPANY_NAME);
                    }
                    if(company.has(JOB_COMPANY_LOGO)){
                        companyLogo=company.getString(JOB_COMPANY_LOGO);
                    }
                    if(company.has(JOB_COMPANY_TAG_LINE)){
                        tagLine=company.getString(JOB_COMPANY_TAG_LINE);
                    }
                }


                Job job=new Job(postId,title,description
                        ,perks,postdate,relocationAssistance
                        ,location,companyName,companyLogo,keywords,url,applyUrl,tagLine,type);
                jobArrayList.add(job);
            }
            return jobArrayList;

        }catch (JSONException e) {
            L.m(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }

    }
}
