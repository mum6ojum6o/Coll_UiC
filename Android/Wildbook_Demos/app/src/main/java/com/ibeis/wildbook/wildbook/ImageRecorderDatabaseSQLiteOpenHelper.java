package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/*******************************
 * Created by Arjan on 10/16/2017.
 *
 * This class is the SQLite database helper class to create the database which will be used
 * to keep track of the files uploaded by the user.
 *************************/



public class ImageRecorderDatabaseSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME="images_recorder";
    public static final String TABLE_NAME_II="sync_record";
    public static final String DATABASE_NAME="Wildbook_db";
    public static final String _ID="_id";
    public static final String FILE_NAME="filename";
    public static final String USER_NAME="username";
    public static final String LONGITUDE="longitude";
    public static final String LATITUDE="latitude";
    public static final String ENCOUNTER_NUM="encounternum";
    public static final String DATE="date";
    public static final String FILE_ID="file_id";
    public static final String IS_UPLOADED="isuploaded";
    public static final String WIFI_SYNC = "wifi_sync";
    public static final String LTE_SYNC = "LTE_sync";
    public static final String ENCOUNTER_ID="Encoounter_ID";
    public static final String UPLOAD_RESPONSE="Upload_Response"; //table to hold responses from the database.

    final private static Integer VERSION = 1;
    public static final String TAG="DatabaseHelper";
    final private Context mContext;
    final private static String CREATE_CMD =
            "CREATE TABLE "+TABLE_NAME+"("+
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    FILE_NAME+" TEXT NOT NULL,"+
                    ENCOUNTER_NUM+" INTEGER NOT NULL, "+
                    LONGITUDE+" DOUBLE ,"+
                    LATITUDE+ " DOUBLE ,"+
                    USER_NAME+" TEXT NOT NULL,"+
                    DATE+" DATE NOT NULL,"+
                    IS_UPLOADED+" BOOLEAN NOT NULL DEFAULT 0 CHECK("+IS_UPLOADED+
                    " IN (0,1)))";
    final static String[] columns_imagesrecorder = {_ID,FILE_NAME,LONGITUDE,LATITUDE,USER_NAME,DATE,IS_UPLOADED};
    final static String[] columns_sync_recorder = {_ID,FILE_ID,WIFI_SYNC,LTE_SYNC};
    final static String[] columns_upload_response={_ID,ENCOUNTER_ID,DATE};
    final private static String CREATE_CMD_II =
            "CREATE TABLE "+TABLE_NAME_II+"("+
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    ENCOUNTER_NUM+" INTEGER NOT NULL, "+
                    FILE_ID + " INTEGER NOT NULL, " +
                    WIFI_SYNC+ " BOOLEAN ," +
                    LTE_SYNC + " BOOLEAN, " +
                    "FOREIGN KEY(FILE_ID) REFERENCES IMAGES_RECORDER(_ID) )";
    final private static String CREATE_TBL_UPLOAD_RESPONSE="CREATE TABLE "+
            UPLOAD_RESPONSE+" ("+
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            ENCOUNTER_ID + " TEXT NOT NULL, "+
            USER_NAME + " TEXT NOT NULL, "+
            DATE+ " DATE NOT NULL)";

    /*constructor takes 4 params:
		* 1. context
		* 2. DB filename: Null if db held in RAM
		* 3. SQLiteDatabase.CursorFactory: used to create cursor objects.null for default factory
		* 4. version number (int)*/
    //the constructor does not created. the db will be created when either getReadable() or getWritable() is called.
    public ImageRecorderDatabaseSQLiteOpenHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CMD);
        Log.i(TAG,"After CREATE_CMD");
        db.execSQL(CREATE_CMD_II);
        Log.i(TAG,"After CREATE_CMD_II");
        db.execSQL(CREATE_TBL_UPLOAD_RESPONSE);
        Log.i(TAG,"After "+UPLOAD_RESPONSE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

}
