package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    protected ImageButton CaptureBtn,CrossBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        CaptureBtn=(ImageButton)findViewById(R.id.imageButton3);
        CrossBtn = (ImageButton)findViewById(R.id.crossButton);
        CaptureBtn.setOnClickListener(this);
        CrossBtn.setOnClickListener(this);
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imageButton3:
                startActivity(new Intent(getApplicationContext(),PicturePreviewActivity.class));
                break;
            case R.id.crossButton:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
    }
}
