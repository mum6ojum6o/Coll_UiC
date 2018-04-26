package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Created by Arjan on 4/22/2018.
 */

public class UtilityClassTest {
    private Context mInstrumentationCtx;
    private Utilities mUtilities;
    @Before
    public void setup() {
        mInstrumentationCtx = InstrumentationRegistry.getTargetContext();
        mUtilities = new Utilities(mInstrumentationCtx);
    }
    @Test
    public void currentIdentityTest_True(){
        //Please set this expectedIdentityResult appropriatly.
        String expectedIdentityResult = "mundyarjan@gmail.com";
        String actualIdentityResult = mUtilities.getCurrentIdentity();
        assertEquals(expectedIdentityResult,actualIdentityResult);
    }

    @Test
    public void encounterNumFromSharedPrefTest(){
        long currEnc= mUtilities.getEncounterNumPreferences();
        mUtilities.writeEncounterNumPreferences(currEnc+1);
        long actualEncNum = mUtilities.getEncounterNumPreferences();
        assertEquals(currEnc+1,actualEncNum);
    }
    @Test
    public void checkUserSharedPreferenceTest(){
        String currIdentity = mUtilities.getCurrentIdentity();
        String setValue = mUtilities.getSyncSharedPreference(currIdentity);

        if(setValue.equals("Sync_Preference"))
            assertEquals(false,mUtilities.checkSharedPreference(currIdentity));
        else
            assertEquals(true,mUtilities.checkSharedPreference(currIdentity));
    }

    @Test
    public void checkUserSharedPreferenceTest_False(){
        String currIdentity = "randomidentity@xyz.com";
        assertEquals(false,mUtilities.checkSharedPreference(currIdentity));
    }

    @Test
    public void getSyncSharedPreferenceTest_True(){
        String actualResult = mUtilities.getSyncSharedPreference();
        assertTrue(mUtilities.getSyncSharedPreference()
                .equals(mInstrumentationCtx.getString(R.string.mobile_data_wifi_string))||
                mUtilities.getSyncSharedPreference().equals(R.string.wifiString)||
                mUtilities.getSyncSharedPreference().equals("Sync_Preference"));
    }
    @Test
    public void getNetworkTypeTest(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mInstrumentationCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo.getType()==ConnectivityManager.TYPE_WIFI)
            assertEquals(mInstrumentationCtx.getString(R.string.wifiString),mUtilities.getNetworkType());
        else if(activeNetworkInfo.getType()==ConnectivityManager.TYPE_MOBILE)
            assertEquals(mInstrumentationCtx.getString(R.string.mobiledataString),mUtilities.getNetworkType());
    }
    @Test
    public void isNetworkAvailableTest(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mInstrumentationCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo.isConnected())
            assertEquals(true,mUtilities.isNetworkAvailable());
        else
            assertEquals(false,mUtilities.isNetworkAvailable());
    }
    //Please click Sync option before running the test*/
    //this method tests whether the records are being inserted succesfully into the database.
    @Test
    public void saveToDbTest(){

        ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(mInstrumentationCtx);
        ArrayList<String> selectedFiles = new ArrayList<String>(){
            {
                add("File1"); add("File2"); add("File3");
            }
        };
        int count = -1;
        Utilities util = new Utilities(mInstrumentationCtx,selectedFiles,dbHelper);
        Espresso.registerIdlingResources(util.getIdlingResource());
        util.prepareBatchForInsertToDB(false);
        util.insertRecords();
        try{
            Thread.sleep(1000); // for some reason Idling resources did not seem to be working..
        }catch(Exception e){}
        Cursor c = dbHelper.getReadableDatabase().query(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME,new String[]{"count(*)"},
                ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED +"=?",
                new String[]{"0"},null,null, ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM);
        c.moveToFirst();
        while (c.getCount() > 0 && !c.isAfterLast()){
            count = c.getInt(c.getColumnIndex("count(*)"));
            c.moveToNext();
        }
        c.close();
        ImageRecorderDatabaseSQLiteOpenHelper dbHelperII = new ImageRecorderDatabaseSQLiteOpenHelper(mInstrumentationCtx);
        dbHelperII.getWritableDatabase()
                .execSQL("Delete from "+ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME+ " where isUploaded=0");
        assertEquals(3,count);
        try{
            Thread.sleep(1000); // for some reason Idling resources did not seem to be working..
        }catch(Exception e){}
        dbHelperII.close();
        dbHelper.close();
    }

    @Test
    public void imageUploadTest() {
        boolean fileCreated = false;
        //make a dummy file
        File f = new File("/storage/emulated/0/Pictures/camera2Api/test_img.jpg");
        try {
            fileCreated = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileCreated) {
            ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(mInstrumentationCtx);
            ArrayList<String> selectedFiles = new ArrayList<String>();
            selectedFiles.add(f.getPath());
            Utilities util = new Utilities(mInstrumentationCtx, selectedFiles, dbHelper);
            util.insertRecords();

            ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(mInstrumentationCtx,
                    selectedFiles, mUtilities.getCurrentIdentity());
            new Thread(task).start();
            sleepThread();
            JSONObject jsonResponse = task.getmJSONResponse();
            if(jsonResponse!=null && jsonResponse.has("encounterId")){
                assertEquals(true,true);
            }
            else
                assertEquals(false,true);

        }
        else
            fail();
    }
        public void sleepThread() {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





