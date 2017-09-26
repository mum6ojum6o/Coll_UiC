package com.ibeis.wildbook.wildbook;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesUsingRecyclerView extends AppCompatActivity {
public static String TAG = "DisplayImagesUsingRecyclerView ";
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
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayImagesUsingRecyclerView.this));

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesUsingRecyclerView.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);
        //storageReference = FirebaseStorage.getInstance().getReference(MainActivity.storagePath);

        // Adding Add Value Event Listener to databaseReference.

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i(TAG,"DATA CHANGED!!!");
               int snapShotSize=0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String storagePath=MainActivity.storagePath+"/"+postSnapshot.getValue();
                    snapShotSize++;
                    final int objeCount=snapShotSize;
                    storageReference =FirebaseStorage.getInstance().getReference().child(storagePath);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            imagesList.add(uri);
                            Log.i(TAG,"Successfully Downloaded Uri!!!Size"+imagesList.size());
                            if(imagesList.size()==objeCount){
                                Log.i(TAG,"ObjeCount="+objeCount);
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
                            Log.i(TAG,"Something went wrong!!");
                            //Toast.makeText(getApplicationContext(),"Something went wrong!!",Toast.LENGTH_LONG).show();
                        }
                    });

                    //ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    //list.add(imageUploadInfo);
                }
                Log.i(TAG,"Download completed!!!imagesList.SIZE()="+imagesList.size());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });

    }
}
