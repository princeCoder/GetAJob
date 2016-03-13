package com.princecoder.getajob.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.princecoder.getajob.R;
import com.princecoder.getajob.utils.L;

/**
 * Created by Prinzly Ngotoum on 3/12/16.
 */
public class JobProvider extends ContentProvider {

    private static final String LOG_TAG = JobProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private JobDbHelper mOpenHelper;

    // Codes for the UriMatcher
    private static final int JOB = 100;
    private static final int JOB_WITH_ID = 101;
    public Context context;


    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = JobContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, JobContract.JobEntry.TABLE_JOBS, JOB);
        matcher.addURI(authority, JobContract.JobEntry.TABLE_JOBS + "/#", JOB_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new JobDbHelper(getContext());
        context=getContext();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case JOB:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        JobContract.JobEntry.TABLE_JOBS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case JOB_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        JobContract.JobEntry.TABLE_JOBS,
                        projection,
                        JobContract.JobEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case JOB:{
                return JobContract.JobEntry.CONTENT_DIR_TYPE;
            }
            case JOB_WITH_ID:{
                return JobContract.JobEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case JOB: {
                long _id = db.insert(JobContract.JobEntry.TABLE_JOBS, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = JobContract.JobEntry.buildJobUri(_id);
                } else {
                    throw new android.database.SQLException(context.getString(R.string.db_insertion_error) + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }



    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case JOB:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try{
                    for(ContentValues value : values){
                        if (value == null){
                            throw new IllegalArgumentException(context.getString(R.string.db_update_error));
                        }
                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(JobContract.JobEntry.TABLE_JOBS,
                                    null, value);
                        }catch(SQLiteConstraintException e) {
                            L.m(LOG_TAG, context.getString(R.string.db_redondance_error));
                        }
                        if (_id != -1){
                            numInserted++;
                        }
                    }
                    if(numInserted > 0){
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0){
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case JOB:
                numDeleted = db.delete(
                        JobContract.JobEntry.TABLE_JOBS, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        JobContract.JobEntry.TABLE_JOBS + "'");
                break;
            case JOB_WITH_ID:
                numDeleted = db.delete(JobContract.JobEntry.TABLE_JOBS,
                        JobContract.JobEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        JobContract.JobEntry.TABLE_JOBS + "'");

                break;
            default:
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException(context.getString(R.string.db_update_error)

            );
        }

        switch(sUriMatcher.match(uri)){
            case JOB:{
                numUpdated = db.update(JobContract.JobEntry.TABLE_JOBS,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case JOB_WITH_ID: {
                numUpdated = db.update(JobContract.JobEntry.TABLE_JOBS,
                        contentValues,
                        JobContract.JobEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
