package com.mum6ojumbo.gestures;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

///from developers.android.com
public class GesturesActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{
        private static final String DEBUG_TAG="GestureActivity";
    private GestureDetectorCompat mDetector;
    double mPrimaryXCoord= 0.0,mSecondaryXCoord= 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(this,this);
       // mDetector.setOnDoubleTapListener(this);

        setContentView(R.layout.activity_gestures);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
       // Toast.makeText(this,"in onTouchEvent!",Toast.LENGTH_SHORT).show();
        if (event.getPointerCount()==2){
            Log.i("Gestures", "Pinch!!");
            Toast.makeText(this,event.getPointerCount()+ " touches",Toast.LENGTH_SHORT).show();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        if (event.getPointerCount()==2){
            Log.i("Gestures", "Pinch!!");
            Toast.makeText(this,event.getPointerCount()+ " touches",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
       // Toast.makeText(this,"Fling...",Toast.LENGTH_SHORT).show();
        if(mSecondaryXCoord>mPrimaryXCoord){
            //move Left
            Toast.makeText(this,"Left -> Right",Toast.LENGTH_SHORT).show();
        }
        else if(mPrimaryXCoord>mSecondaryXCoord){
            //move right
            Toast.makeText(this,"Right -> Left",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        //Toast.makeText(this,"scorlling...",Toast.LENGTH_SHORT).show();
        mPrimaryXCoord=event1.getX();
        mSecondaryXCoord=event2.getX();
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        //Toast.makeText(this,"DoubleTap!!",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
