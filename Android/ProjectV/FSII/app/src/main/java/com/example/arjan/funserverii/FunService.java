package com.example.arjan.funserverii;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.arjan.commons.*;

public class FunService extends Service {
    private MediaPlayer mPlayer = new MediaPlayer();

    public FunService() {
    }
    private final FunCommons.Stub mBinder= new FunCommons.Stub(){
       synchronized public Bitmap getPicture(int i){

            Bitmap pic;
            Integer picVal=null;
            switch (i){
                case 1:
                   // pic = BitmapFactory.decodeResource(getResources(),R.drawable.a);
                    picVal=R.drawable.a;
                    break;
                case 2:
                    //pic = BitmapFactory.decodeResource(getResources(),R.drawable.b);
                    picVal=R.drawable.b;
                    break;
                case 3:
                    //pic = BitmapFactory.decodeResource(getResources(),R.drawable.c);
                    picVal=R.drawable.c;
                    break;
            }
            pic = BitmapFactory.decodeResource(getResources(),picVal);
            return pic;
        }
        public void playSong(int i){

            synchronized (mPlayer) {
                Log.i("FunService", "in Play Song");            //MediaPlayer mPlayer;
                Integer song = 0;
                switch (i) {
                    case 1:
                        if(mPlayer.isPlaying()){
                            mPlayer.stop();
                        }
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.a);
                        mPlayer.start();
                        break;
                    case 2:
                        if(mPlayer.isPlaying()){
                            mPlayer.stop();
                        }
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b);
                        mPlayer.start();
                        break;
                    case 3:
                        if(mPlayer.isPlaying()){
                            mPlayer.stop();
                        }
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.c);
                        mPlayer.start();
                        break;

                    case 20:
                        mPlayer.stop();
                        break;
                    case 30:
                        mPlayer.pause();
                        break;
                    case 25:
                        mPlayer.start();

                }
                 mPlayer.setLooping(false);
                // Stop Service when music has finished playing
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        // stop Service if it was started with this ID
                        // Otherwise let other start commands proceed
                        stopSelf();

                    }
                });
            }
            }
            public void stopSong(){
                synchronized (mPlayer) {
                    Log.i("FunService", "In stopSong()");
                    if (null != mPlayer)
                        mPlayer.stop();
                }
            }
            public void pauseSong() {
                synchronized (mPlayer) {
                    Log.i("FunService", "In pauseSong()");
                    if (null != mPlayer) {
                        Log.i("FunService", "mPlayer is not NULL");
                        mPlayer.pause();
                    }
                }
            }
            public void resumeSong() {
                synchronized (mPlayer) {
                    Log.i("FunService", "In resumeSong()");
                    if (null != mPlayer) {
                        Log.i("FunService", "mPlayer is not NULL");
                        mPlayer.start();
                    }
                }
            }

    };
    @Override
    public void onDestroy() {
        Log.i("FunServer","In onDestroy!!");
        if (null != mPlayer) {
            Log.i("FunServer","mPlayer is not Null!!");
            mPlayer.stop();
            mPlayer.release();

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
