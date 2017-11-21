package com.ibeis.wildbook.wildbook;

import java.util.Date;

/**************************************************************
 * Created by Arjan on 11/19/2017.
 * Class to represent a record in the Upload_response SQL Table.
 ************************************************************/

public class ResponseDBRecord {
    private String mEncounterId;
    private Date mDate;

    public String getmEncounterId() {
        return mEncounterId;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }
}
