package com.ibeis.wildbook.wildbook;

import java.util.Date;

/**
 * Created by Arjan on 10/18/2017.
 * This class represents a record to be inserted into the
 * ImageRecorderDatabase's images_recorder table
 * This class should be passed to the Utilities class's InsertToDB method to inser the record.
 */

public class ImageRecordDBRecord {
    private String mFileName;
    private long mLatitude;
    private long mLongitude;
    private String mUsername;
    private Date mDate;
    private boolean mIsUploaded;

    public void setFileName(String fileName){
    mFileName=fileName;
    }
    public void setLatitude(long latitude){
        mLatitude=latitude;
    }
    public void setLongitude(long longitude){
        mLongitude=longitude;
    }
    public void setUsername(String username){
        mUsername=username;
    }
    public void setDate(Date date){
        mDate=date;
    }
    public void setIsUploaded(boolean isUploaded){
        mIsUploaded=isUploaded;
    }
    public String getmFileName(){return mFileName;}
    public String getmUsername(){return mUsername;}
    public long getmLongitude(){return mLongitude;}
    public long getmLatitude(){return mLatitude;}
    public boolean getmIsUploaded(){return mIsUploaded;}
    public Date getmDate(){return mDate;}

}
