package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class PetProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();



    private PetDbHelper mDbHelper;



    private static final int PETS = 100;
    private static final int PETS_ID=101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS + "/#" ,PETS_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:{
                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection , selection ,selectionArgs,null ,null ,sortOrder);
                break;
            }
            case PETS_ID:{
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME , projection , selection , selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot query unknown uri "+uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:{
                return insertPet(uri,contentValues);
            }
            default:{
                throw new IllegalArgumentException("Insertion is not supported for : " + uri);
            }
        }
    }

    private Uri insertPet(Uri uri, ContentValues values){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(PetContract.PetEntry.TABLE_NAME,null,values);

        if(id==-1){
            Log.e(LOG_TAG, "insertPet: Failed to inser pet for  "+uri);
            return null;
        }

        return ContentUris.withAppendedId(uri,id);

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
