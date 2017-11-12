package com.ibeis.wildbook.wildbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/****************************************************************************************
 * Created by Arjan on 11/8/2017.
 * This class will act as a model to hold the important details that are to be
 * uploaded/received from the Wildbook Database.
 **************************************************************************************/

public class ImageModel {
    Bitmap mImage;
    double mLongitude,mLatitude;//to be loaded from the imagePath
    public ImageModel(String imageName){
        File image = new File(imageName);
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        mImage=BitmapFactory.decodeFile(image.getAbsolutePath(),bitmapOptions);
        setCoordinates(imageName);
        //mImage=Bitmap.createScaledBitmap(mImage,parent.getWidth(),parent.getHeight(),true);
    }
    public void setCoordinates(String imagePath){
        float[] latlong=new float[2];
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            if(exif.getLatLong(latlong)){
                mLongitude=latlong[1];
                mLatitude=latlong[0];
            }
            else{
                Log.i("ImageModel","Image is NOT GeoTageed!!");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
