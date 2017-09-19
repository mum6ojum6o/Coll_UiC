package com.cs478.arjan.createplaylist;

/**
 *
 */

public class Songs {
    private String songName;
    private boolean checked;
    private int thumbId;
    Songs(){
        songName="";
        checked=false;
        thumbId=0;
    }
    Songs(String name, int picId){
        songName=name;
        checked=false;
        thumbId=picId;
    }
    String getName(){
        return this.songName;
    }
    boolean getChecked(){
         return this.checked;
    }
    void setName(String name){
        this.songName=name;
    }
    void setChecked(boolean b){
        this.checked=b;
    }
    int getPicId(){ return this.thumbId;}

}
