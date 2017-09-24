package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
//activity display the preview of the image clicked via camera
public class PicturePreviewActivity extends AppCompatActivity implements View.OnClickListener{
protected Button Upload,Discard;
    protected Spinner speciesSpinner;
    protected ImageView PicPreview;
    protected ProgressBar progressBar;
    protected TextView validateText;
    public final String TAG="PicturePreviewActivity";
    List<String> species=null;
    AlertDialog uploadStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        Upload = (Button)findViewById(R.id.UploadBtn);
        Discard = (Button)findViewById(R.id.DiscardBtn);
        speciesSpinner = (Spinner)findViewById(R.id.Speciesspinner);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setMax(100);
        validateText = (TextView)findViewById(R.id.ValidationText);
        validateText.setVisibility(View.INVISIBLE);
        PicPreview = (ImageView)findViewById(R.id.PicturePreview);
         species = Arrays.asList(getResources().getStringArray(R.array.species_array));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,species);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speciesSpinner.setAdapter(dataAdapter);
        PicPreview.setImageResource(R.drawable.right_whale_fluke);
        Upload.setOnClickListener(this);
        Discard.setOnClickListener(this);
        speciesSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.UploadBtn:
                if(speciesSpinner.getSelectedItem().toString().equals(species.get(0))) {
                    validateText.setVisibility(View.VISIBLE);
                    validateText.setText("Please select Species!");

                }
                else
                {new UploadPic().execute();}

                break;
            case R.id.DiscardBtn:
                startActivity(new Intent(getApplicationContext(),CameraActivity.class));
                //startActivity(new Intent());
                break;
        }
    }

    public class UploadPic extends AsyncTask<String,Integer,AlertDialog.Builder>{
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        @Override
        protected AlertDialog.Builder doInBackground(String... string) {
            AlertDialog.Builder alertDialogBuilder=null;
            try {
                Thread.sleep(500);
                publishProgress(20);
                Thread.sleep(500);
                publishProgress(70);
                Thread.sleep(1000);
                publishProgress(99);
                alertDialogBuilder= new AlertDialog.Builder(PicturePreviewActivity.this);
                alertDialogBuilder.setTitle(R.string.imageUploadedString);
                alertDialogBuilder.setMessage(R.string.anotherIncidentString)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yesString,new DialogInterface.OnClickListener(){
                            public void onClick (DialogInterface dailog, int id){
                                startActivity(new Intent(getApplicationContext(),CameraActivity.class));
                            }
                        })
                        .setNegativeButton(R.string.noString,new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                dialog.cancel();
                            }
                        });
            } catch (Exception e) { Log.i(TAG,"Some Exception Occured!");
            }
            return alertDialogBuilder;
        }
        @Override
        protected void onProgressUpdate(Integer... value){progressBar.setProgress(value[0]);}
        protected void onPostExecute(AlertDialog.Builder result){
            progressBar.setVisibility(View.INVISIBLE);
            AlertDialog alertDialog = result.create();
            alertDialog.show();
        }

    }
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
            if(!parent.getSelectedItem().toString().equals(species.get(0))) {
                validateText.setVisibility(View.INVISIBLE);
            }
        }
        public void onNothingSelected(AdapterView parent){
            //Do nothing!!
        }
    }

}
