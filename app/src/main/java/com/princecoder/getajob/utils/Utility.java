package com.princecoder.getajob.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;

import com.princecoder.getajob.model.JobModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class Utility {

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "MMMM, dd yyyy";
    public static final String API_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";


    /**
     * Are we online?
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getData(HttpURLConnection con) {
        BufferedReader reader = null;

        try {
            con.connect();
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    //Get the Location Using lon and lat coords
    public static String getLocationFromLonLat(Context context, Double lon, Double lat){
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat,lon, 1);
            if (addresses.size() > 0){
                String cit=addresses.get(0).getLocality();
                String coun=addresses.get(0).getAdminArea();

                String city=addresses.get(0).getLocality()!=null?addresses.get(0).getLocality()+", ":"";
                String local=city+((addresses.get(0).getAdminArea()!=null)?addresses.get(0).getAdminArea():addresses.get(0).getCountryName());
                return local;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocationId(String place){
        return JobModel.myLocations.get(place);
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "3 days ago".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayDifference(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return "Today";
        } else if ( julianDay == currentJulianDay -1 ) {
            return "yesterday";
        } else {
            int dayDiff=currentJulianDay-julianDay;
            return dayDiff+" days ago";
        }
    }

    /**
     * Converts  date format to the format "Month, day year", e.g "June, 24 2015".
     * @param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "June, 24 2015 "
     */
    public static String getFormattedMonthDayYear(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat monthDayFormat = new SimpleDateFormat(DATE_FORMAT);
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    /**
     * Converts string to date milliseconds
     * @param date
     * @return date in milliseconds
     */
    public static long convertStringToDateMilliseconds(String date){
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.API_DATE_FORMAT);
        long lg=0;
        try {
            Date d = sdf.parse(date);
            lg=d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return lg;
    }

}
