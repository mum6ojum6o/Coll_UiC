package com.example.arjan.funclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arjan.commons.FunCommons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

public class FunClient extends AppCompatActivity {
    public Button _pic1;
    public Button _pic2;
    public Button _pic3;
    public ImageView _im;
    public Button _song1;
    public Button _song2;
    public Button _song3;
    public Button _pause;
    public Button _stop;
    public Button _clear;
    public TextView _hist;
    private boolean mIsBound = false;
    private FunCommons mFunCommonsService;
    final private String TAG="FUNClient";
    public LinearLayout ll;
    private final static String fileName = "History.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_client);
        _clear = (Button)findViewById(R.id.button_history);
        _pic1 = (Button)findViewById(R.id.pic1);
        _pic2 = (Button)findViewById(R.id.pic2);
        _pic3 = (Button)findViewById(R.id.pic3);
        _song1 = (Button)findViewById(R.id.song1);
        _song2 = (Button)findViewById(R.id.song2);
        _song3 = (Button)findViewById(R.id.song3);
        _pause = (Button)findViewById(R.id.pause);
        _stop = (Button)findViewById(R.id.stop);
        _im =  (ImageView)findViewById(R.id.imageView1);
        _hist = (TextView)findViewById(R.id._hist);

        _hist.setMovementMethod(new ScrollingMovementMethod());
        try {

            readFile(ll);

        } catch (IOException e) {
            Log.i(TAG, "IOException");
        }
        _pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 try {

                        writeFile("Request: Pic1");

                    } catch (FileNotFoundException e) {
                        Log.i(TAG, "FileNotFoundException");
                    }
                 try {

                    if (mIsBound)
                        _im.setImageBitmap(mFunCommonsService.getPicture(1));
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                     Log.e(TAG, e.toString());
                }
            }
        });
        _pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Pic2");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {

                    // Call KeyGenerator and get a new ID
                    if (mIsBound)
                        _im.setImageBitmap(mFunCommonsService.getPicture(2));
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        _pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Pic3");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {

                    if (mIsBound)
                        _im.setImageBitmap(mFunCommonsService.getPicture(3));
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        _song1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Play Song1");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {
                    // Call KeyGenerator and get a new ID
                    if (mIsBound) {
                        mFunCommonsService.playSong(1);
                        _pause.setVisibility(View.VISIBLE);
                        _stop.setVisibility(View.VISIBLE);
                        //LinearLayout.LayoutParams =  (LinearLayout.LayoutParams)_pause.getLayoutParams();

                    }
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        _song2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Play Song 2");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {
                    // Call KeyGenerator and get a new ID
                    if (mIsBound) {
                        mFunCommonsService.playSong(2);
                        _pause.setVisibility(View.VISIBLE);
                        _stop.setVisibility(View.VISIBLE);
                        //LinearLayout.LayoutParams =  (LinearLayout.LayoutParams)_pause.getLayoutParams();

                    }
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        _song3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Play Song 3");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {
                    // Call KeyGenerator and get a new ID
                    if (mIsBound) {
                        mFunCommonsService.playSong(3);
                        _pause.setVisibility(View.VISIBLE);
                        _stop.setVisibility(View.VISIBLE);
                        //LinearLayout.LayoutParams =  (LinearLayout.LayoutParams)_pause.getLayoutParams();

                    }
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });


        _stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Stop Song");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                try {
                    // Call KeyGenerator and get a new ID
                    if (mIsBound) {
                        //mFunCommonsService.playSong(20);
                        mFunCommonsService.stopSong();
                    }
                    else {
                        Log.i(TAG, "The service was not bound!");
                    }

                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
                _stop.setVisibility(View.INVISIBLE);
                _pause.setVisibility(View.INVISIBLE);
            }
        });
        _pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    writeFile("Request: Pause/Resume Song");
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException");
                }
                if(_pause.getText().equals("Pause")) {
                    Log.i(TAG,"Paused!!");
                    _pause.setText("Play");
                    try {
                        // Call KeyGenerator and get a new ID
                        if (mIsBound) {
                            //mFunCommonsService.playSong(30);
                            mFunCommonsService.pauseSong();
                        } else {
                            Log.i(TAG, "The service was not bound!");
                        }

                    } catch (RemoteException e) {
                        Log.e(TAG, e.toString());
                    }
                }
                else if(_pause.getText().equals("Play")){
                    Log.i(TAG,"Play!!");
                    _pause.setText("Pause");

                    try {
                        // Call KeyGenerator and get a new ID
                        if (mIsBound) {
                            //mFunCommonsService.playSong(25);
                            mFunCommonsService.resumeSong();
                        } else {
                            Log.i(TAG, "The service was not bound!");
                        }

                    } catch (RemoteException e) {
                        Log.e(TAG, e.toString());
                    }
                }

            }
        });
        _clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //_clear.setText("");
                _hist.setText("");
                try {
                    FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                    PrintWriter p= new PrintWriter(fileName);
                    p.flush();
                }catch (IOException e ){e.printStackTrace();}

            }
        });

    }
    private void writeFile(String text) throws FileNotFoundException {
        Log.i("FunClient", text);
        FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
        OutputStreamWriter o = new OutputStreamWriter(fos);
        try {
            o.append(text+"\n");
            o.flush();
        }catch (IOException e){e.printStackTrace();}

    }
    protected void onResume() {
        super.onResume();
        try {
            readFile(ll);
        } catch (IOException e) {
            Log.i(TAG, "IOException");
        }
        if (!mIsBound) {
            Log.i(TAG,"Service is not bound!");
            boolean b = false;
            Intent i = new Intent(FunCommons.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, Context.BIND_AUTO_CREATE);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            //mIsBound=true;
            if (b) {
                Log.i(TAG, "bindService() succeeded!");
            } else {
                Log.i(TAG, "bindService() failed!");
            }

        }
    }
    protected void onPause(){


        super.onPause();
    }
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            Log.i("FunClient","onServiceConnected");
            mFunCommonsService = FunCommons.Stub.asInterface(iservice);
            mIsBound = true;
        }
        public void onServiceDisconnected(ComponentName className) {
            Log.i("FunClient","onServiceDisConnected");
            mFunCommonsService = null;
            mIsBound = false;
        }
    };
    public void onDestroy(){
        if (mIsBound) {
            Log.i(TAG,"Inside onDEstroy and unbinding service!!!");
            unbindService(this.mConnection);
            //mIsBound=false;
        }
        super.onDestroy();
    }
    private void readFile(LinearLayout l) throws IOException {

        FileInputStream fis = openFileInput(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = "";
        _hist.setText("");
        while (null != (line = br.readLine())) {
            _hist.setMovementMethod(new ScrollingMovementMethod());
            _hist.append(line+"\n");
            //_hist.setText(line+"\n");
            Log.i("reading from File",line);

        }

        br.close();

    }

}
