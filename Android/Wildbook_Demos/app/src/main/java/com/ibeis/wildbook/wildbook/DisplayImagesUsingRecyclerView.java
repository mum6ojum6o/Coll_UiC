package com.ibeis.wildbook.wildbook;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;

public class DisplayImagesUsingRecyclerView extends AppCompatActivity {
public static String TAG = "DisplayImagesUsingRecyclerView ";
    private static final int DISPLAY=123;
    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;
    StorageReference storageReference;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();
    List<Uri> imagesList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images_using_recycler_view);

        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(DisplayImagesUsingRecyclerView.this,3));

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesUsingRecyclerView.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images.");

        // Showing progress dialog.


        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.




    }
    @Override
    public void onResume(){

        super.onResume();
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i(TAG,"DATA CHANGED!!!");
                long recordsRead=0;
                final long objeCount=snapshot.getChildrenCount();
                if(null != snapshot.getValue()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        long recordCount = snapshot.getChildrenCount();

                        String storagePath = MainActivity.storagePath + "/" + postSnapshot.getValue();

                        recordsRead++;

                        Log.i(TAG, "snapshot_ChildCount" + objeCount);
                        storageReference = FirebaseStorage.getInstance().getReference().child(storagePath);


                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagesList.add(uri);
                                Log.i(TAG, "Successfully Downloaded Uri!!!Size" + imagesList.size());
                                if ((long) imagesList.size() == objeCount) {
                                    Log.i(TAG, "ObjeCount=" + objeCount);
                                    Collections.reverse(imagesList);
                                    adapter = new RecyclerViewAdapter(getApplicationContext(), imagesList);
                                    recyclerView.setAdapter(adapter);
                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.i(TAG, "Something went wrong!!");
                            }
                        });
                    }
                }
                else{
                    Log.i(TAG,"No Contributions");
                    Toast.makeText(getApplicationContext(),"No Contributions",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

                //Log.i(TAG,"Download completed!!!imagesList.SIZE()="+imagesList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }

}
