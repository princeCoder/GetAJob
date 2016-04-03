package com.princecoder.getajob.parsers;

import android.content.Context;
import android.content.Intent;

import com.princecoder.getajob.R;
import com.princecoder.getajob.model.Job;
import com.princecoder.getajob.ui.JobsFragment;
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

    public static synchronized List<Job> parseFeed(Context context, String content) {

        // Job information
        final String JOB_POST_ID = "id",JOB_TITLE = "title",JOB_DESCRIPTION = "description",JOB_PERKS = "perks", JOB_POSTDATE = "post_date",JOB_RELOCATION_ASSISTANCE = "relocation_assistance",JOB_LOCATION = "name",
              JOB_COMPANY_LOGO = "logo",JOB_COMPANY_NAME = "name",JOB_KEYWORDS = "keywords",JOB_URL = "url",JOB_APPLY_URL = "apply_url",JOB_COMPANY_TAG_LINE = "tagline",COMPANY = "company",LOCATION = "location",
              LON = "lng",LAT = "lat",TYPE = "type",TYPE_NAME = "name",PAGES = "pages";

        String postId="",title="",description="",perks="",location="",keywords="",applyUrl="",url="",companyName="",companyLogo="",tagLine="",type="";
        long postdate=0;
        int relocationAssistance=0;
        JSONObject jobObject;

        try{
            JSONObject jobsJson = new JSONObject(content).getJSONObject("listings");
            if(jobsJson.has(PAGES)){
                int pages= jobsJson.getInt(PAGES);
                // We brodcast the response to the fragment
                Intent intent = new Intent(JobsFragment.PAGE);
                if(context!=null){
                    intent.putExtra(context.getString(R.string.num_page_tag),pages);
                    context.sendBroadcast(intent);
                }
            }

            JSONArray jobsArray = jobsJson.getJSONArray("listing");

            ArrayList<Job> jobArrayList = new ArrayList<>(jobsArray.length());


            for(int i = 0; i < jobsArray.length(); i++) {

                jobObject=jobsArray.getJSONObject(i);
                postId=jobObject.has(JOB_POST_ID) ? jobObject.getString(JOB_POST_ID):"";
                title=jobObject.has(JOB_TITLE)?jobObject.getString(JOB_TITLE):"";
                description=jobObject.has(JOB_DESCRIPTION)?jobObject.getString(JOB_DESCRIPTION):"";
                perks=jobObject.has(JOB_PERKS)?jobObject.getString(JOB_PERKS):"";
                String date=jobObject.has(JOB_POSTDATE)?jobObject.getString(JOB_POSTDATE):"";
                //convert the date to long
                postdate=Utility.convertStringToDateMilliseconds(date);
                relocationAssistance=jobObject.has(JOB_RELOCATION_ASSISTANCE)?jobObject.getInt(JOB_RELOCATION_ASSISTANCE):0;
                keywords=jobObject.has(JOB_KEYWORDS)?jobObject.getString(JOB_KEYWORDS):"";
                applyUrl=jobObject.has(JOB_APPLY_URL)?jobObject.getString(JOB_APPLY_URL):"";
                url=jobObject.has(JOB_URL)?jobObject.getString(JOB_URL):"";
                type=jobObject.has(TYPE)?jobObject.getJSONObject(TYPE).getString(TYPE_NAME):context.getString(R.string.type_not_specified);

                //Company Object
                if(jobObject.has(COMPANY)){
                    JSONObject company=jobObject.getJSONObject(COMPANY);

                    if(company.has(LOCATION)){
                        JSONObject locationObject=company.getJSONObject(LOCATION);
                        location=locationObject.getString(JOB_LOCATION);
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
