package com.ibeis.wildbook.wildbook;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/*************************************************************************
 * Created by Arjan on 10/19/2017.
 * This class is creates the service that will sync the images as per the availability of
 * Network.
 *
 ****************************************************************************/

public class SyncerService extends IntentService {
    private static final String TAG = "SyncerService";
    private SQLiteCursor mSQLiteCursor;
    private ImageRecorderDatabase mDBHelper;
   public SyncerService(){
       super(TAG);
   }
    @Override
    public void onHandleIntent(Intent intent){
        int count=0;
        mDBHelper= new ImageRecorderDatabase(this);
        mDBHelper.getReadableDatabase();
        String where = "isUploaded = 0";
        //String orderBy= "ORDER BY "
        String columns[]={ImageRecorderDatabase._ID,ImageRecorderDatabase.FILE_NAME,ImageRecorderDatabase.LONGITUDE,ImageRecorderDatabase.LATITUDE};
        Cursor c = mDBHelper.getReadableDatabase().query(ImageRecorderDatabase.TABLE_NAME,columns,ImageRecorderDatabase.IS_UPLOADED +"=?",
                new String[]{"0"},null,null,null);
        c.moveToFirst();
        //String groupBy
        final ArrayList<Uri> successUploads = new ArrayList<Uri>();
        while(!c.isAfterLast()) {
            Log.i(TAG,"Checking NetworkAvailability!!");
            while (c.getCount() > 0 && !c.isAfterLast() && new Utilities(this).isNetworkAvailable()) {
                String filename =c.getString(c.getColumnIndex(ImageRecorderDatabase.FILE_NAME));
                StorageReference storage;
                FirebaseAuth auth;
                DatabaseReference databaseReference;
                StorageReference filePath = null;
                UploadTask upload = null;
                auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                storage = FirebaseStorage.getInstance().getReference();
                Uri uploadImage = Uri.fromFile(new File(filename));
                filePath = storage.child("Photos/" + auth.getCurrentUser().getEmail()).child(uploadImage.getLastPathSegment());
                upload = filePath.putFile(uploadImage);
                upload.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        Log.i(TAG, "Error!");
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        successUploads.add(downloadUrl);


                    }
                });
                count++;
                Log.i(TAG, "There are " + c.getCount() + "remaining. Record " + count);
                Log.i(TAG, "Column ID=" + c.getString(c.getColumnIndex(ImageRecorderDatabase._ID)));
                c.moveToNext();
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Checking Next Record!");
            }Log.i(TAG,"Network not available");
            try {
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        c.close();
        Log.i(TAG,"Service Stopping");
    }
}
