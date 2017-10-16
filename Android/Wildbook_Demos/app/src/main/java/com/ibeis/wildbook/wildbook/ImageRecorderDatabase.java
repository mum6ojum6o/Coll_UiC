package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arjan on 10/16/2017.
 *
 * This class is the SQLite database helper class to create the database which will be used
 * to keep track of the files uploaded by the user.
 */



public class ImageRecorderDatabase extends SQLiteOpenHelper {
    public static final String TABLE_NAME="imagesrecorder";
    public static final String DATABASE_NAME="Wildbook_db";
    public static final String _ID="_id";
    public static final String FILE_NAME="filename";
    public static final String USER_NAME="username";
    public static final String LONGITUDE="longitude";
    public static final String LATITUDE="latitude";
    public static final String DATE="date";
    public static final String IS_UPLOADED="isuploaded";
    final private static Integer VERSION = 1;
    final private Context mContext;
    final private static String CREATE_CMD =
            "CREATE TABLE "+TABLE_NAME+"("+
                    _ID + "INTEGER PRIMARY KEY AUTO INCREMENT,"+
                    FILE_NAME+"TEXT NOT NULL,"+
                    LONGITUDE+" DOUBLE ,"+
                    LATITUDE+ " DOUBLE ,"+
                    USER_NAME+"TEXT NOT NULL,"+
                    DATE+"DATE NOT NULL,"+
                    IS_UPLOADED+" BOOLEAN NOT NULL DEFAULT 0 CHECK("+IS_UPLOADED+
                    "IN (0,1)))";
    final static String[] columns = {_ID,FILE_NAME,LONGITUDE,LATITUDE,USER_NAME,DATE,IS_UPLOADED};
    /*constructor takes 4 params:
		* 1. context
		* 2. DB filename: Null if db held in RAM
		* 3. SQLiteDatabase.CursorFactory: used to create cursor objects.null for default factory
		* 4. version number (int)*/
    //the constructor does not created. the db will be created when either getReadable() or getWritable() is called.
    public ImageRecorderDatabase(Context context){
        super(context,DATABASE_NAME,null,VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){db.execSQL(CREATE_CMD);}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

}
