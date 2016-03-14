package com.princecoder.getajob.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Prinzly Ngotoum on 3/12/16.
 */
public class JobContract {

    public static final String CONTENT_AUTHORITY = "com.princecoder.getajob";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class JobEntry implements BaseColumns {

        //Table name
        public static final String TABLE_JOBS = "job";
        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_JOBS).build();

        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_JOBS;

        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_JOBS;

        public static final String TABLE_NAME = "jobs";

        public static final String POST_ID = "id";

        public static final String TITLE = "title";

        public static final String COMPANY_LOGO = "imgurl";

        public static final String LOCATION = "location";

        public static final String DESC = "description";

        public static final String PERKS = "perks";

        public static final String POST_DATE = "postdate";

        public static final String RELOCATION_ASSISTANCE = "relocationassistance";

        public static final String COMPANY_NAME = "companyname";

        public static final String KEYWORDS = "keywords";

        public static final String URL = "url";

        public static final String APPLY_URL = "applyurl";

        public static final String COMPANY_TAG_LINE = "companytagline";


        // for building URIs on insertion
        public static Uri buildJobUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
