package com.ibeis.wildbook.wildbook;

/**
 * Created by Arjan on 11/14/2017.
 * Class will be used to convert HTTPURL response to JSON
 */

public class Response2Json {
    private String mSuccess;
    private int mEncounterId;
    public int getEncounterId(){
        return mEncounterId;
    }
    public String getSuccessStatus(){
        return mSuccess;
    }
    public void setmSuccessStatus(String mSuccess){
        this.mSuccess=mSuccess;
    }
    public void setEncounterId(int encounterId){
        this.mEncounterId=encounterId;
    }
}
