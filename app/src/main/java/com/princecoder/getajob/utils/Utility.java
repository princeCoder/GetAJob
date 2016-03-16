package com.princecoder.getajob.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.princecoder.getajob.model.JobModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class Utility {
    /**
     * Are we online?
     *
     * @return boolean
     *
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
            addresses = gcd.getFromLocation(lon,lat, 1);
            if (addresses.size() > 0){
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

}
