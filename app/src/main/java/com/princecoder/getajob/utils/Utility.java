package com.princecoder.getajob.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Prinzly Ngotoum on 3/13/16.
 */
public class Utility {

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "MMMM, dd yyyy";
    public static final String API_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";


    public static final Map<String, String> myLocations;
    static
    {
        myLocations =new TreeMap<>();
        myLocations.put("","");
        myLocations.put("Norfolk, VA","2cit");
        myLocations.put("Amsterdam","amsterdamnl");
        myLocations.put("Amsterdam, NL","amsterdamnlnl");
        myLocations.put("Annapolis, MD","annapolismdmdus");
        myLocations.put("Appleton, WI","appletonwiwius");
        myLocations.put("Arlington, VA","arlingtonvaraleighncncus");
        myLocations.put("Atlanta, GA","atlantagagaus");
        myLocations.put("Austin, TX","austintxtxus");
        myLocations.put("Baltimore, MD","baltimoremdmdus");
        myLocations.put("Belton, TX","beltontxtxus");
        myLocations.put("Berkeley, CA","berkeleycaus");
        myLocations.put("Boston, MA","bostonmamaus");
        myLocations.put("Lincoln, NE","bostonmaorlincolnnemaus");
        myLocations.put("Brighton, UK","brightongb");
        myLocations.put("Baltimore, MD","baltimoremdmdus");
        myLocations.put("Brooklyn, NY","brooklynnynyus");
        myLocations.put("Cambridge, MA","cambridgemamaus");
        myLocations.put("Cary, NC","caryncncus");
        myLocations.put("Charlotte, NC","charlottencncus");
        myLocations.put("Brighton, UK","brightongb");
        myLocations.put("Chicago, IL","chicagoililus");
        myLocations.put("Columbia, MD","columbiamdmdus");
        myLocations.put("Columbia, MO","columbiamoco");
        myLocations.put("Cupertino, CA","cupertinocacaus");
        myLocations.put("Dallas, TX","dallastxtxus");
        myLocations.put("Dayton, OH","daytonohohus");
        myLocations.put("Delray Beach, FL","delraybeachflflus");
        myLocations.put("Houston, TX", "houstontxtxus");
        myLocations.put("Iowa City, IA","iowacityiaaustintxtxus");
        myLocations.put("Austin, TX","iowacityiaaustintxtxus");
        myLocations.put("Irvine, CA","irvinecaca");
        myLocations.put("Jacksonville, FL", "jacksonvilleflflus");
        myLocations.put("Kennedale, TX", "kennedaletxtxus");
        myLocations.put("Kigali, Rwanda", "kigalirwandarw");
        myLocations.put("Lakeland, FL","lakelandflflus");
        myLocations.put("Lake St. Louis, MO","lakestlouismomous");
        myLocations.put("Lehi, UT","lehiututus");
        myLocations.put("London","londongb");
        myLocations.put("London, UK","londonukgb");
        myLocations.put("London, UK","londonunitedkingdomgb");
        myLocations.put("Los Angeles, CA","losangelescacaus");
        myLocations.put("San Diego, CA","losangelescaus");
        myLocations.put("Madison, WI","madisonwiwius");
        myLocations.put("Mahwah, NJ","mahwahnjnjus");
        myLocations.put("Manchester, CT","manchesterctgb");
        myLocations.put("Marietta, GA","mariettagagaus");
        myLocations.put("Mineola, NY","mineolanynyus");
        myLocations.put("Minneapolis, MN","minneapolismnmnus");
        myLocations.put("Mountain View, CA","mountainviewcaca");
        myLocations.put("Nashville, TN", "nashvilletntnus");
        myLocations.put("New York City, NY","newyorkcitynynyus");
        myLocations.put("New York City Area","newyorkcitynyus");
        myLocations.put("New York, NY","newyorknyanywherenyus");
        myLocations.put("Washington, D.C.","newyorknyorwashingtondcnyus");
        myLocations.put("New York","newyorknyus");
        myLocations.put("Oceanside, CA","oceansidecaca");
        myLocations.put("Orange, CA","orangecaca");
        myLocations.put("Ottawa, Ontario","ottawaontarioca" );
        myLocations.put("Palo Alto, Ca","paloaltocacaus");
        myLocations.put("Philadelphia, PA","philadelphiapapaus");
        myLocations.put("Pittsburgh, PA","pittsburghpaorremotepaus");
        myLocations.put("Portland, OR","portlandororpasadenacaorus");
        myLocations.put("Pasadena, CA","portlandororpasadenacaorus");
        myLocations.put("Portland, OR","portlandororus");
        myLocations.put("Providence, RI","providenceririus");
        myLocations.put("Randolph, NJ","randolphnjnjus");
        myLocations.put("Redwood City, CA","redwoodcitycaus");
        myLocations.put("San Antonio, Texas","sanantoniotexastxus");
        myLocations.put("San Diego, CA","sandiegocaco");
        myLocations.put("San Fracisco","sanfraciscoco");
        myLocations.put("San Francisco, CA","sanfranciscocaco");
        myLocations.put("Menlo Park, CA","sanfranciscocaorsunnyvalecacaus");
        myLocations.put("Sunnyvale CA","sanfranciscocaorsunnyvalecacaus");
        myLocations.put("San Bruno, CA","sanfranciscocaus");
        myLocations.put("San Francisco, Financial District","sanfranciscofinancialdistrictcaus");
        myLocations.put("San Luis Obispo, CA","sanluisobispocacaus");
        myLocations.put("San Mateo, CA","sanmateocaca");
        myLocations.put("Santa Monica, CA","santamonicacaca");
        myLocations.put("Savannah, GA","savannahgagaus");
        myLocations.put("Scottsdale, AZ","scottsdaleazazus");
        myLocations.put("Seattle, WA","seattlewaandorspokanewawaus");
        myLocations.put("Spokane, WA","seattlewaandorspokanewawaus");
        myLocations.put("Seattle Area, WA", "seattlewawaus");
        myLocations.put("Sicklerville, NJ","sicklervillenjnjus");
        myLocations.put("Stanford, CA","stanfordcaca");
        myLocations.put("St. Petersburg, FL","stpetersburgflflus");
        myLocations.put("Tempe, AZ","tempeazazus");
        myLocations.put("Philadelphia, PA","thecityofbrotherlylovephiladelphiapapaus");
        myLocations.put("The Colony, TX","thecolonytxtxus");
        myLocations.put("Toronto","torontoca");
        myLocations.put("Toronto, Canada","torontocanadaca");
        myLocations.put("Torrance, CA","torrancecaca");
        myLocations.put("Tucson, AZ","tucsonarizonaazus");
        myLocations.put("United Kingdom","unitedkingdomgb");
        myLocations.put("Utah","utahutus");
        myLocations.put("Vancouver, Canada","vancouverbccanadagastownca");
        myLocations.put("Virtual/USA","virtualusabr");
        myLocations.put("Walnut Creek, CA","walnutcreekcacaus");
        myLocations.put("Washington, DC","washingtondcdcus");
        myLocations.put("Watertown","watertownmaus");
        myLocations.put("Zürich, Switzerland","zrichswitzerlandch");
    }

    /**
     * Are we online?
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        return myLocations.get(place);
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "3 days ago".
     *
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayDifference(long dateInMillis) {
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
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "June, 24 2015 "
     */
    public static String getFormattedMonthDayYear(long dateInMillis ) {
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
