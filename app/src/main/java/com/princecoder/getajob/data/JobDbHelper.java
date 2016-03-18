package com.princecoder.getajob.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.princecoder.getajob.utils.L;

/**
 * Created by Prinzly Ngotoum on 3/12/16.
 */
public class JobDbHelper extends SQLiteOpenHelper{
    public static final String LOG_TAG = JobDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "getajob.db";

    public JobDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_JOB_TABLE = "CREATE TABLE " + JobContract.JobEntry.TABLE_NAME + " ("+
                JobContract.JobEntry._ID + " TEXT PRIMARY KEY," +
                JobContract.JobEntry.TITLE + " TEXT ," +
                JobContract.JobEntry.LOCATION + " TEXT ," +
                JobContract.JobEntry.DESC + " TEXT ," +
                JobContract.JobEntry.PERKS + " TEXT, " +
                JobContract.JobEntry.POST_DATE + "  INTEGER , " +
                JobContract.JobEntry.RELOCATION_ASSISTANCE + " INTEGER , " +
                JobContract.JobEntry.COMPANY_NAME + " TEXT, " +
                JobContract.JobEntry.KEYWORDS + " TEXT, " +
                JobContract.JobEntry.URL + " TEXT, " +
                JobContract.JobEntry.APPLY_URL + " TEXT, " +
                JobContract.JobEntry.COMPANY_LOGO + " TEXT, " +
                JobContract.JobEntry.TYPE + " TEXT, " +
                JobContract.JobEntry.COMPANY_TAG_LINE + " TEXT, " +
                "UNIQUE ("+ JobContract.JobEntry._ID +") ON CONFLICT IGNORE)";
        sqLiteDatabase.execSQL(SQL_CREATE_JOB_TABLE);

        final String SQL_CREATE_RECENT_TABLE = "CREATE TABLE " + JobContract.RecentEntry.TABLE_NAME + " ("+
                JobContract.RecentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                JobContract.RecentEntry.TITLE + " TEXT NOT NULL," +
                JobContract.RecentEntry.LOCATION + " TEXT )";
        sqLiteDatabase.execSQL(SQL_CREATE_RECENT_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        L.m(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        //Drop the table Jobs
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JobContract.JobEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                JobContract.JobEntry.TABLE_JOBS + "'");

        //Drop the table Recents
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JobContract.RecentEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                JobContract.RecentEntry.TABLE_RECENTS + "'");

        //re-create database
        onCreate(sqLiteDatabase);
    }
}
