package com.ibeis.wildbook.wildbook;

import java.util.Date;

/***************************************************************
 * Created by Arjan on 10/18/2017.
 * This class represents a record to be inserted into the
 * ImageRecorderDatabaseSQLiteOpenHelper's images_recorder table
 * This class should be passed to the Utilities class's InsertToDB method to inser the record.
 **************************************************************/

public class ImageRecordDBRecord {
    private String mFileName;
    private long mLatitude;
    private long mLongitude;
    private String mUsername;
    private Date mDate;
    private boolean mIsUploaded;
    private String mEncounterId;
    private long mEncounterNum;

    public void setFileName(String fileName){
    this.mFileName=fileName;
    }
    public void setLatitude(long latitude){
        this.mLatitude=latitude;
    }
    public void setLongitude(long longitude){
        this.mLongitude=longitude;
    }
    public void setUsername(String username){
        this.mUsername=username;
    }
    public void setDate(Date date){
        this.mDate=date;
    }
    public void setIsUploaded(boolean isUploaded){
        this.mIsUploaded=isUploaded;
    }
    public String getmFileName(){return mFileName;}
    public String getmUsername(){return mUsername;}
    public long getmLongitude(){return mLongitude;}
    public long getmLatitude(){return mLatitude;}
    public boolean getmIsUploaded(){return mIsUploaded;}
    public Date getmDate(){return mDate;}
    public String getmEncounterId() {return mEncounterId;   }
    public void setmEncounterId(String mEncounterId) {        this.mEncounterId = mEncounterId;    }
    public long getmEncounterNum() {return mEncounterNum;    }
    public void setmEncounterNum(long mEncounterNum) {this.mEncounterNum = mEncounterNum;    }
}
