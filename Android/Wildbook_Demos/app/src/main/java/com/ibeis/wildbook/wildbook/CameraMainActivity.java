        package com.ibeis.wildbook.wildbook;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.res.Configuration;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.ImageFormat;
        import android.graphics.Matrix;
        import android.graphics.Point;
        import android.graphics.Rect;
        import android.graphics.RectF;
        import android.graphics.SurfaceTexture;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.hardware.camera2.CameraAccessException;
        import android.hardware.camera2.CameraCaptureSession;
        import android.hardware.camera2.CameraCharacteristics;
        import android.hardware.camera2.CameraDevice;
        import android.hardware.camera2.CameraManager;
        import android.hardware.camera2.CaptureFailure;
        import android.hardware.camera2.CaptureRequest;
        import android.hardware.camera2.CaptureResult;
        import android.hardware.camera2.TotalCaptureResult;
        import android.hardware.camera2.params.StreamConfigurationMap;
        import android.location.Location;
        import android.media.ExifInterface;
        import android.media.Image;
        import android.media.ImageReader;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.HandlerThread;
        import android.os.Message;
        import android.support.annotation.NonNull;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;

        import android.os.Bundle;

        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.util.Size;
        import android.util.SparseIntArray;
        import android.view.Display;

        import android.view.MotionEvent;
        import android.view.Surface;
        import android.view.TextureView;
        import android.view.View;

        import android.widget.ImageView;
        import android.widget.Toast;

        import com.google.android.gms.auth.api.Auth;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.nio.ByteBuffer;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.Date;
        import java.util.List;

        /*****************************************************************
 * DISCLAIMER:-
 * The code in the class file was referenced from multiple sources.
 * References:-
 * https://www.youtube.com/watch?v=69J2ycNCtpE - Nigel App Tuts
 * https://github.com/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic -- Google AndroidCamera2 basic repository
 * https://stackoverflow.com/questions/29971319/image-orientation-android/32747566#32747566 -- to help with the image display orientations...
 *********************************************************************/


public class CameraMainActivity extends BaseActivity implements
                View.OnClickListener,View.OnTouchListener,SensorEventListener {
    public static String SWAP="Rear";
    public static final int ROTATE_RIGHT=180,ROTATE_LEFT=0,DEF_ORIENTATION=270,UPSIDE_DOWN=90;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        //ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);

    }
    /*This will change based on the location of the device's sensor*/
    private static final SparseIntArray ORIENTATIONSFRONT = new SparseIntArray();
    static {
        ORIENTATIONSFRONT.append(Surface.ROTATION_0, 270);
        ORIENTATIONSFRONT.append(Surface.ROTATION_270, 180);
        ORIENTATIONSFRONT.append(Surface.ROTATION_180, 90);
        ORIENTATIONSFRONT.append(Surface.ROTATION_90, 0);
    }
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private Toast toast;
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    protected static final int STORAGE_PERMISSION_REQUEST=99;
    private int mSensorOrientation;
    private Handler mUIHandler = new Handler(){
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch(what){
                case UPDATE_PIC_PREVIEW:
                    getGeoTagData(mImageFile.getAbsolutePath());
                    Log.i("CamAct","UIHandler mImageFile:"+mImageFile.getAbsolutePath().toString());
                    //mCapturedPics.add(mImageFile.getAbsoluteFile().toString());
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(mImageFile)); // may cause lags...
                    getApplicationContext().sendBroadcast(mediaScanIntent);


                    mPicPreview.setImageBitmap(getUpdatedBitmap(mImageFile));
                    //mPicPreview.invalidate();
                    break;
            }
        }};
    private boolean swappedDimensions;
    SensorManager mSensorManager;//Manages the sensors on the device...
    Sensor mSensor;
    /**
     * Gets the Amount of Degress of rotation using the exif integer to determine how much
     * we should rotate the image.
     * @param exifOrientation - the Exif data for Image Orientation
     * @return - how much to rotate in degress
     * Source:- https://stackoverflow.com/questions/29971319/image-orientation-android/32747566#32747566
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
    private CameraCharacteristics mCameraCharacteristics;
    private ArrayList<String> mCapturedPics = new ArrayList<String>();  //holds the list of images clicked
    private static File mImageFile;
    private File mImageFolder;
    private static String mImageFileName;
    private ImageReader mImageReader;
    private TextureView mTextureView;
    private RecyclerView mRecycleView;
    private String currentPreview;
    private ImageView mImageButton,mBurstButton;
    private CameraManager mCameraManager;
    private List<Uri> mFileUris;

    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    setupCamera(width, height);

                    transformImage(width, height);
                    openCamera();
                }
                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }
                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }
                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            };
    private Location mLocation;
    /* Warning: Please continue using the FusedLocationProviderApi class and don't migrate to the
    FusedLocationProviderClient class until Google Play services version 12.0.0 is available, which
     is expected to ship in early 2018. Using the FusedLocationProviderClient before version 12.0.0
      causes the client app to crash when Google Play services is updated on the device.
      We apologize for any inconvenience this may have caused.
      Source:- developer.android.com */
    private FusedLocationProviderClient mFusedLocationClient;
    private FloatingActionButton mCaptureButton;
    private static File mLatestFile;
    private static ImageView mPicPreview;
    private CaptureRequest.Builder[] mCaptureStillBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null && savedInstanceState.containsKey("CapturedImages")){
            Log.i("CameraMainActivity","Capturing previous Activity State");
            mCapturedPics = savedInstanceState.getStringArrayList("CapturedImages");
        }
        mSensorManager =(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensor == null)
            Toast.makeText(this,"No Accelerometer!1",Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_camera_main);
        createImageFolder();//setup directory
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setOnTouchListener(this);
        mCaptureButton = (FloatingActionButton) findViewById(R.id.photoButton);
        mPicPreview = (ImageView)findViewById(R.id.picPreview);
        mCaptureButton.setOnClickListener(this);
        //mRecycleView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        mImageButton =(ImageView) findViewById(R.id.imageButton);
        mBurstButton =(ImageView) findViewById(R.id.burstModeButton);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        // gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // mRecycleView.setLayoutManager(gridLayoutManager);
        //RecyclerView.Adapter imageAdapter = new CameraImageAdapter(mImageFolder);
        //List<File> files = Arrays.asList(Uri.fromFile(mImageFolder.listFiles()));

        mFileUris = new ArrayList<Uri>();
        for(File f: mImageFolder.listFiles()){
            mFileUris.add(Uri.fromFile(f));
        }
        //removing the picture preview as it may confuse the user....
        /*RecyclerView.Adapter imageAdapter = new DispPicAdapter(getApplicationContext(),mFileUris,null);
        mRecycleView.setAdapter(imageAdapter);*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mBurstButton.setOnClickListener(this);
        mImageButton.setOnClickListener(this);
        mPicPreview.setOnClickListener(this);
        setLAYOUT();

    }

    /****
     * Method to retrieve the previous saved state of the activity
     * @param savedState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedState){
        Log.i("CameraMainActivity","in OnRestoreInstanceState");
        if(savedState!=null && savedState.containsKey("CapturedImages")){
            mCapturedPics = savedState.getStringArrayList("CapturedImages");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
       // ActivityUpdaterBroadcastReceiver.activeActivity=this;
        openBackgroundThread();
        //mCapturedPics.clear();
        if(sortFilesToLatest(mImageFolder).length>0) {
            mLatestFile = sortFilesToLatest(mImageFolder)[0];
            mPicPreview.setImageBitmap(getUpdatedBitmap(mLatestFile));
            currentPreview = mLatestFile.getAbsolutePath().toString();
        }
        if(mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            transformImage(mTextureView.getWidth(), mTextureView.getHeight());
            mTextureView.setOnTouchListener(this);
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        if(mBurstButton.getAlpha()==0.5f) {
            //Toast.makeText(this,getResources().getString(R.string.burstmodeon),Toast.LENGTH_SHORT).show();
            mCaptureStillBuilder = new CaptureRequest.Builder[3];
            new Utilities(this).displaySnackBar(LAYOUT,R.string.burstmodeon,MainActivity.WARNING);
        }
        else {
            mCaptureStillBuilder = new CaptureRequest.Builder[1];
            //Toast.makeText(this,getResources().getString(R.string.burstmodeoff),Toast.LENGTH_SHORT).show();
            new Utilities(this).displaySnackBar(LAYOUT,R.string.burstmodeoff,MainActivity.WARNING);
        }
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.photoButton:
                Toast.makeText(this,"Click!",Toast.LENGTH_SHORT).show();
                takePhoto();
                break;
            case R.id.picPreview:
                Intent i = new Intent(CameraMainActivity.this,UploadCamPicsActivity.class);

                if(mCapturedPics.size()==0){
                    mCapturedPics.add(mLatestFile.getAbsoluteFile().toString());
                }
                i.putExtra("Files",mCapturedPics);
                startActivity(i);

                break;
            case R.id.imageButton:
                if(SWAP.equals("Front")) {
                    SWAP = "Rear";
                }
                else{
                    SWAP="Front";
                }
                closeCamera();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                setupCamera(width, height);

                //transformImage(width, height);
                openCamera();
                break;
            case R.id.burstModeButton:
                    if(mBurstButton.getAlpha()==0.5) {
                        mBurstButton.setAlpha(1.0f); // Burst mode deactivated
                        //Toast.makeText(this,getResources().getString(R.string.burstmodeoff),Toast.LENGTH_SHORT).show();
                        new Utilities(this).displaySnackBar(LAYOUT,R.string.burstmodeoff,MainActivity.WARNING);
                    }
                    else {
                        mBurstButton.setAlpha(0.5f); // Burst mode activated
                        //Toast.makeText(this,getResources().getString(R.string.burstmodeon),Toast.LENGTH_SHORT).show();
                        new Utilities(this).displaySnackBar(LAYOUT,R.string.burstmodeon,MainActivity.WARNING);
                    }
                break;
        }
    }
    @Override
    public void onPause() {
        closeCamera();
        closeBackgoundThread();
        super.onPause();
        if(mSensor!=null){
            mSensorManager.unregisterListener(this);
        }

    }
    private Size mPreviewSize;
    private String mCameraId;

    /***************************************
     * Sets up Camera configurations
     * @param width
     * @param height
     **************************************/
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCameraManager =cameraManager;
        if(SWAP.equals("Front")) {
            try {
                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    mCameraCharacteristics = cameraCharacteristics;
                    mMaximumZoomLevel = cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                    mZoomLevel=1f;
                    // mZoom=null;
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) !=
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }

                    //Get the largest supported imageSize....
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size largestImageSize = Collections.max(
                            Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                            new Comparator<Size>() {
                                @Override
                                public int compare(Size lhs, Size rhs) {
                                    return Long.signum(lhs.getWidth() * lhs.getHeight() -
                                            rhs.getWidth() * rhs.getHeight());
                                }
                            }
                    );
                     mImageReader = ImageReader.newInstance(largestImageSize.getWidth(),
                            largestImageSize.getHeight(),
                            ImageFormat.JPEG,
                            1);
                    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                            mBackgroundHandler);

                    int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                    mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Log.i("CamAct","Lens FACING FRONT display Rotation:"+displayRotation);
                    Log.i("CamAct","Sensor Orientation: "+mSensorOrientation);
                    boolean swappedDimensions = false;
                    switch (displayRotation) {
                        case Surface.ROTATION_0:
                            break;
                        case Surface.ROTATION_180:
                            if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                                swappedDimensions = true;
                            }
                            break;
                        case Surface.ROTATION_90:
                            break;
                        case Surface.ROTATION_270:
                            if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                                // if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                                swappedDimensions = true;
                            }
                            break;
                        default:
                            Log.e("CamAct", "Display rotation is invalid: " + displayRotation);
                    }

                    Point displaySize = new Point();
                    getWindowManager().getDefaultDisplay().getSize(displaySize);
                    int rotatedPreviewWidth = width;
                    int rotatedPreviewHeight = height;
                    int maxPreviewWidth = displaySize.x;
                    int maxPreviewHeight = displaySize.y;

                    if (swappedDimensions) {
                        Log.i("CamAct", "Swapping Dimensions");
                        //Swapping Dimensions....
                        rotatedPreviewWidth = height;
                        rotatedPreviewHeight = width;
                        maxPreviewWidth = displaySize.y;
                        maxPreviewHeight = displaySize.x;
                    }

                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                        maxPreviewWidth = MAX_PREVIEW_WIDTH;
                    }

                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                        maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                    }
                    //Updating the previewSize depending on the orientation of the screen....
                    // mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight);

                    int orientation = getResources().getConfiguration().orientation;
                  /*  if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Log.i("CamAct","ORIENTATION_LANDSCAPE ");
                        mTextureView.setMinimumWidth(mPreviewSize.getHeight());
                        mTextureView.setMinimumHeight(mPreviewSize.getWidth());
                    } else {
                        Log.i("CamAct","ORIENTATION_PORTRAIT ");
                        mTextureView.setMinimumWidth(mPreviewSize.getWidth());
                        mTextureView.setMinimumHeight(mPreviewSize.getHeight());
                    }*/

                    mCameraId = cameraId;

                }
                return;


            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        else{
            try {

                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    mCameraCharacteristics=cameraCharacteristics;
                    mMaximumZoomLevel = cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
                    mZoomLevel=1f;
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size largestImageSize = Collections.max(
                            Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                            new Comparator<Size>() {
                                @Override
                                public int compare(Size lhs, Size rhs) {
                                    return Long.signum(lhs.getWidth() * lhs.getHeight() -
                                            rhs.getWidth() * rhs.getHeight());
                                }
                            }
                    );
                    mImageReader = ImageReader.newInstance(largestImageSize.getWidth(),
                            largestImageSize.getHeight(),
                            ImageFormat.JPEG,
                            1);
                    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,
                            mBackgroundHandler);
                    int displayRotation = getWindowManager().getDefaultDisplay().getRotation();

                    //noinspection ConstantConditions
                    // Source :-https://github.co/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic
                    //this is being done to streamline the Sensor Orientation along with the camera Orientation...
                    mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Log.i("CamAct","display Rotation:"+displayRotation);
                    Log.i("CamAct","Sensor Orientation: "+mSensorOrientation);
                    boolean swappedDimensions = false;
                    switch (displayRotation) {
                        case Surface.ROTATION_0:
                        case Surface.ROTATION_180:
                            if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                                swappedDimensions = true;
                            }
                            break;
                        case Surface.ROTATION_90:
                        case Surface.ROTATION_270:
                            if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                                swappedDimensions = true;
                            }
                            break;
                        default:
                            Log.e("CamAct", "Display rotation is invalid: " + displayRotation);
                    }

                    Point displaySize = new Point();
                    getWindowManager().getDefaultDisplay().getSize(displaySize);
                    int rotatedPreviewWidth = width;
                    int rotatedPreviewHeight = height;
                    int maxPreviewWidth = displaySize.x;
                    int maxPreviewHeight = displaySize.y;

                    if (swappedDimensions) {
                        rotatedPreviewWidth = height;
                        rotatedPreviewHeight = width;
                        maxPreviewWidth = displaySize.y;
                        maxPreviewHeight = displaySize.x;
                    }

                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                        maxPreviewWidth = MAX_PREVIEW_WIDTH;
                    }

                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                        maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                    }
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class),
                            rotatedPreviewWidth, rotatedPreviewHeight);
                    mCameraId = cameraId;

                    return;
                }

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /******************
     * Returns the maximun resolutionsize that can be supported by the device
     * @param mapSizes
     * @param width
     * @param height
     * @return
     */
    private Size getPreferredPreviewSize(Size[] mapSizes, int width, int height) {
        List<Size> collectorSizes = new ArrayList<>();
        for(Size option : mapSizes) {
            if(width > height) {
                if(option.getWidth() > width &&
                        option.getHeight() > height) {
                    collectorSizes.add(option);
                }
            } else {
                if(option.getWidth() > height &&
                        option.getHeight() > width) {
                    collectorSizes.add(option);
                }
            }
        }
        if(collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return mapSizes[0];
    }
    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback
            = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //Toast.makeText(getApplicationContext(), "Camera Opened!", Toast.LENGTH_SHORT).show();
            createCameraPreviewSession();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    /***********
     * Method that opens connection to Camera based on Camera Id
     **********************/
    private void openCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            //checkpermission here
            cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
    }
    /*****************************************************
     * Method to close connection to Camera
     *****************************************************/
    private void closeCamera() {
        if(mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if(mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }
    private int mState;
    private static final int UPDATE_PIC_PREVIEW=99;
    private static final int STATE_PREVIEW=0;
    private static final int STATE_WAIT_AF_LOCK=1;
    private CaptureRequest mPreviewCaptureRequest;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session,
                                     CaptureRequest request,
                                     long timestamp,
                                     long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }
        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request,
                                       TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }
        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.i("CamMain", "Failure reason:"+failure.toString()+" "+ failure.getReason());
            //Toast.makeText(getApplicationContext(), "Focus Lock Unsuccessful", Toast.LENGTH_SHORT).show();
        }
        private void process(CaptureResult result) {
            switch(mState) {
                case STATE_PREVIEW:
                    // Do nothing
                    break;
                case STATE_WAIT_AF_LOCK:
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if(afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        ///unLockFocus();
                        //Toast.makeText(getApplicationContext(), "Focus Lock Successful", Toast.LENGTH_SHORT).show();
                        mState=STATE_PREVIEW;
                        captureStillImage();
                    }
                    break;
            }
        }
    };

    /******************************
     *  Method to create previews captured by the open Camera
     **********************************/
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface,mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            if(mCameraDevice == null) {
                                return;
                            }
                            try {
                                mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
                                mCameraCaptureSession = session;
                                mCameraCaptureSession.setRepeatingRequest(
                                        mPreviewCaptureRequest,
                                        mSessionCaptureCallback,
                                        mBackgroundHandler
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "create camera session failed!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    /****************
     * Initialzes a HandlerThread and Handler
     */
    private void openBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera2 background thread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    /****************
     * deinitialzes a HandlerThread and Handler
     */
    private void closeBackgoundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /******************
     * Method to request Camera Auto Focus and capture after the Auto Focus request has been processed
     *********************/
    private void lockFocus() {
        try {
            mState = STATE_WAIT_AF_LOCK;
            mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START);
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(),
                    mSessionCaptureCallback, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    /******************
     * Method to release Camera Auto Focus and capture after the Auto Focus request has been processed
     ************************/
    private void unLockFocus() {
        try {
            mState = STATE_PREVIEW;
            mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
            mCameraCaptureSession.capture(mPreviewCaptureRequestBuilder.build(),
                    mSessionCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /***************************
     * Method to create a Folder in the internal storage with the name Wildbook, subject to user permissions
     * and request for user permission if they are not provided.
     ************************/
    private void createImageFolder() {
        //Log.i(TAG,"createImageFolder");
        if(ContextCompat.checkSelfPermission(getApplicationContext(),"android.permission.READ_EXTERNAL_STORAGE")== PackageManager.PERMISSION_GRANTED) {
            //creating a directory on the external storage and tagging it for the purpose of storing images
            //https://developer.android.com/guide/topics/data/data-storage.html
            File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            mImageFolder = new File(imageFile, "Wildbook");
            ImageSaver.mImageSaver_folder =mImageFolder;
            if (!mImageFolder.exists()) {
                mImageFolder.mkdirs();
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE"},
                    STORAGE_PERMISSION_REQUEST);
        }
    }

    /**************
     * Method to handle permission results.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int []grantResults){
        switch(requestCode) {
            case STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createImageFolder();
                }
                else{
                    Toast.makeText(getApplicationContext(),"In order to contribute to our cause we " +
                            "would encourage you to grant us access in the future!",Toast.LENGTH_LONG);
                    startActivity(new Intent(CameraMainActivity.this,MainActivity.class));
                    // toast.cancel();
                }
                break;
        }
    }



    /***************
     * Requests to Capturing of an Image to the connected Camera
     */
    public void takePhoto() {
        if(SWAP.equals("Rear")) {
            lockFocus();
        }
        else{
            captureStillImage();
        }
    }

    /**************
     * Callback interface for being notified that a new image is available.
     **/
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(),mUIHandler,mCapturedPics));
                }
            };

    //Gestures Listener that enables zoom feature.
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Log.i("CameraMain","onTouch!!!!!!!!");
        try {
            Rect rect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            if (rect == null) return false;
            float currentFingerSpacing;

            if (event.getPointerCount() == 2) { //Multi touch.
                Log.i("CameraMain","MULTI TOUCH!!!!");
                currentFingerSpacing = getFingerSpacing(event);
                Log.i("fingerSpacing","Current FingerSpacing:"+currentFingerSpacing);
                float delta = 0.05f; //Control this value to control the zooming sensibility
                Log.i("fingerSpacing","mFingerSpacing:"+mFingerSpacing);
                if (mFingerSpacing != 0) {
                    if (currentFingerSpacing > mFingerSpacing) { //Don't over zoom-in
                        if ((mMaximumZoomLevel - mZoomLevel) <= delta) {
                            delta = mMaximumZoomLevel - mZoomLevel;
                        }
                        mZoomLevel = mZoomLevel + delta;
                    } else if (currentFingerSpacing < mFingerSpacing){ //Don't over zoom-out
                        if ((mZoomLevel - delta) < 1f) {
                            delta = mZoomLevel - 1f;
                        }
                        mZoomLevel = mZoomLevel - delta;
                    }
                    float ratio = (float) 1 / mZoomLevel; //This ratio is the ratio of cropped Rect to Camera's original(Maximum) Rect
                    //croppedWidth and croppedHeight are the pixels cropped away, not pixels after cropped
                    int croppedWidth = rect.width() - Math.round((float)rect.width() * ratio);
                    int croppedHeight = rect.height() - Math.round((float)rect.height() * ratio);
                    //Finally, zoom represents the zoomed visible area
                    Log.i("CamAct","mZoom:"+mZoom);
                    mZoom = new Rect(croppedWidth/2, croppedHeight/2,
                            rect.width() - croppedWidth/2, rect.height() - croppedHeight/2);
                    Log.i("CamAct","mZoom:"+mZoom);
                    mPreviewCaptureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, mZoom);
                }
                mFingerSpacing = currentFingerSpacing;
            } else { //Single touch point, needs to return true in order to detect one more touch point
                return true;
            }
            Log.i("CameraMain", "Setting up ZOOMED camera repeating request");

            mCameraCaptureSession.setRepeatingRequest(mPreviewCaptureRequestBuilder.build(), mSessionCaptureCallback, mBackgroundHandler);
            return true;
        } catch (final Exception e) {
            //Error handling up to you
            e.printStackTrace();
            return true;
        }

    }

    /*************
     * Inner Class that helps save a captured Image
     */
    private static class ImageSaver implements Runnable {
        private final Image mImage;
        private final Handler mHandler;
        private ArrayList<String> capturedImages;
        public static File mImageSaver_folder;

        private ImageSaver(Image image) {
            mImage = image;
            mHandler = null;
        }
        private ImageSaver(Image image,Handler handler) {
            mImage = image;
            mHandler = handler;
        }
        private ImageSaver(Image image,Handler handler,ArrayList<String> imageFileNames) {
            mImage = image;
            mHandler = handler;
            capturedImages=imageFileNames;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;
            try {
                mImageFile=createImageFileName();
                    Log.i("CamAct","FileName generated:"+mImageFile);
                fileOutputStream = new FileOutputStream(mImageFile);
                fileOutputStream.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                // mCapturedPics.add(mImageFile.getAbsoluteFile().toString());
                //mPicPreview.setImageBitmap(BitmapFactory.decodeFile(mLatestFile.getAbsolutePath()));
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("CamAct","file:"+mImageFile);
            capturedImages.add(mImageFile.getAbsolutePath().toString());
            Message message = mHandler.obtainMessage(UPDATE_PIC_PREVIEW);
            mHandler.sendMessage(message);
        }
        /****************
         * Creates a file with the timestamp for an Image
         * @return
         * @throws IOException
         *******************/
        private File createImageFileName() throws IOException {
            Log.i("CamAct","createImageFileName");
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String prepend = "IMG_" + timestamp + "_";
            File imageFile = File.createTempFile(prepend, ".jpg", mImageSaver_folder);
            mImageFileName = imageFile.getAbsolutePath();
            Log.i("CamAct","Image FILE NAME="+mImageFileName);
            return imageFile;
        }
    }

    /***************************************
     * Method that captures a still Image
     *************************************/
    private void captureStillImage() {
        try {
            CaptureRequest.Builder[] captureStillBuilder;

            //list of capture requests to process burst mode.
                List<CaptureRequest> requests = new ArrayList<>();
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
                mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                /*if(mBurstButton.getAlpha()==0.5f) { //Burst mode is enabled
                    captureStillBuilder = new CaptureRequest.Builder[3];
                    for(int i=0;i<captureStillBuilder.length;i++) {
                        captureStillBuilder[i] = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                        captureStillBuilder[i].addTarget(mImageReader.getSurface());
                    }
                }
                else {//burst Mode is disabled
                    captureStillBuilder = new CaptureRequest.Builder[1];
                    captureStillBuilder[0] = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureStillBuilder[0].addTarget(mImageReader.getSurface());
                }*/
                for(int i=0;i<mCaptureStillBuilder.length;i++) {
                mCaptureStillBuilder[i] = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                mCaptureStillBuilder[i].addTarget(mImageReader.getSurface());
                }

                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){

                    Log.i("CamAct:","rotation:"+rotation);
                    Log.i("CamAct","mOrientation="+mOrientation);
                    for(int i=0;i<mCaptureStillBuilder.length;i++) {
                        mCaptureStillBuilder[i].set(CaptureRequest.SCALER_CROP_REGION, mZoom);
                        mCaptureStillBuilder[i].set(CaptureRequest.JPEG_ORIENTATION, mOrientation);
                    }
                    /*if(captureStillBuilder1!=null)
                        captureStillBuilder1.set(CaptureRequest.JPEG_ORIENTATION,mOrientation);*/
                    Log.i("CamAct","LENS_FACING_FRONT ORIENTATIONSFRONT.get("+rotation+")="+ORIENTATIONSFRONT.get(rotation));
                }
                else {
                    Log.i("CamAct:","rotation:"+rotation);
                    Log.i("CamAct","mOrientation="+mOrientation);
                    //captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
                    for(int i=0;i<mCaptureStillBuilder.length;i++) {
                        mCaptureStillBuilder[i].set(CaptureRequest.SCALER_CROP_REGION, mZoom);
                        mCaptureStillBuilder[i].set(CaptureRequest.JPEG_ORIENTATION, mOrientation);
                    }
                    /*if(captureStillBuilder1!=null)
                        captureStillBuilder1.set(CaptureRequest.JPEG_ORIENTATION,mOrientation);*/
                    Log.i("CamAct","getOrientation("+rotation+")="+getOrientation(rotation));
                }
                CameraCaptureSession.CaptureCallback captureCallback =
                        new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                                /*toast = Toast.makeText(getApplicationContext(),
                                        "Image Captured!", Toast.LENGTH_SHORT);

                                toast.show();*/
                                unLockFocus();

                            }
                            public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                            }
                        };
                    for(int i=0;i<mCaptureStillBuilder.length;i++) {
                        requests.add(mCaptureStillBuilder[i].build());
                    }
                    //requests.add(captureStillBuilder.build());
                    if(mBurstButton.getAlpha()==0.5f)
                        mCameraCaptureSession.captureBurst(requests, captureCallback, null);
                    else
                        mCameraCaptureSession.capture(mCaptureStillBuilder[0].build(), captureCallback, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

    /******
     * Method that sets the image preview for the best suitable dimensions
     * @param width
     * @param height
     */
    private void transformImage(int width,int height){
        Matrix matrix = new Matrix();

        if(mPreviewSize==null||mTextureView==null) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0,0,width,height);
        RectF previewRectF = new RectF(0,0,mPreviewSize.getHeight(),mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation==Surface.ROTATION_90||rotation==Surface.ROTATION_270){
            previewRectF.offset(centerX - previewRectF.centerX(),centerY-previewRectF.centerY());
            matrix.setRectToRect(textureRectF,previewRectF,Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width/mPreviewSize.getWidth(),
                    (float)height/mPreviewSize.getHeight());
            matrix.postScale(scale,scale,centerX,centerY);
            matrix.postRotate(90*(rotation-2),centerX,centerY);
        }

        mTextureView.setTransform(matrix);
    }

    /************
     *
     * Method used to retreive the latest captured image to be displayed in the preview imageview
     * @param fileImagesDir
     * @return
     */
    private File[] sortFilesToLatest(File fileImagesDir) {
        File[] files = fileImagesDir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.valueOf(rhs.lastModified()).compareTo(lhs.lastModified());
            }
        });
        return files;
    }

    /************
     * Method that geotag a captured image
     * @param imagePath
     */
    private void getGeoTagData(final String imagePath){
        try {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION")
                    == PackageManager.PERMISSION_GRANTED){
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    mLocation = task.getResult();
                                    try {
                                        ExifInterface exif = new ExifInterface(imagePath);
                                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(mLocation.getLatitude()));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(mLocation.getLatitude()));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(mLocation.getLongitude()));
                                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(mLocation.getLongitude()));
                                        SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                        exif.setAttribute(ExifInterface.TAG_DATETIME, fmt_Exif.format(new Date(mLocation.getTime())));
                                        exif.saveAttributes();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }

        }catch(SecurityException e){e.printStackTrace();}

    }

    /*************
     * Inner Class to convert the longitude and latitude into DMS
     **********************************/
    private static class GPS {
        private static StringBuilder sb = new StringBuilder(20);
        /**
         * returns ref for latitude which is S or N.
         *
         * @param latitude
         * @return S or N
         */
        public static String latitudeRef(final double latitude) {
            return latitude < 0.0d ? "S" : "N";
        }

        /**
         * returns ref for latitude which is W or E.
         *
         * @param latitude
         * @return S or N
         */
        public static String longitudeRef(final double longitude) {
            return longitude < 0.0d ? "W" : "E";
        }
        /**
         * convert latitude into DMS (degree minute second) format. For instance<br/>
         * -79.948862 becomes<br/>
         * 79/1,56/1,55903/1000<br/>
         * It works for latitude and longitude<br/>
         *
         * @param latitude could be longitude.
         * @return
         */
        public static final String convert(double latitude) {
            latitude = Math.abs(latitude);
            final int degree = (int)latitude;
            latitude *= 60;
            latitude -= degree * 60.0d;
            final int minute = (int)latitude;
            latitude *= 60;
            latitude -= minute * 60.0d;
            final int second = (int)(latitude * 1000.0d);
            sb.setLength(0);
            sb.append(degree);
            sb.append("/1,");
            sb.append(minute);
            sb.append("/1,");
            sb.append(second);
            sb.append("/1000,");
            return sb.toString();
        }
    }

    //from google github repository.
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90,  return mapping from ORIENTATIONS.
        // For devices with orientation of 270, rotate the JPEG 180 degrees.

        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /*********
     * Method to inflate an image on the preview imageview based on the imageview's dimensions
     * @param imageFile
     * @return
     */
    public Bitmap getUpdatedBitmap(File imageFile){
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        Bitmap rotatedBitmap=null;
        try{
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        }catch(Exception e){
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    /****************
     * Method to save the current state of the activity
     * @param outstate
     */
    @Override
    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);
        Log.i("CameraMainActivity","Saving Instance State");
        outstate.putStringArrayList("CapturedImages",mCapturedPics);
    }
    protected void signOut() {
        final Context ctx = getApplicationContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();

                        Intent intent = new Intent(CameraMainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        // toast.cancel();
                        startActivity(intent);

                        Log.i("CameraMainActivity","Logging out from UserContributionsActivity");
                        new Utilities(CameraMainActivity.this).setCurrentIdentity("");
                        CameraMainActivity.this.finish();
                        CameraMainActivity.this.finishAffinity();

                    }
                });
    }
    // for zooming purposes
    //references :- https://stackoverflow.com/questions/32711975/zoom-camera2-preview-using-textureview
    protected float mFingerSpacing = 0;
    protected float mZoomLevel = 1f;
    protected float mMaximumZoomLevel;
    protected Rect mZoom;
    protected int mOrientation=-1;

    //Method that returns distance between two touch points.
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("SensorAccuracyChanged", "values="+sensor.getName());
    }
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.i("SensorEventChanged","values: X Axis"+sensorEvent.values[0]+" Y-axis="+sensorEvent.values[1]+" Z-axis="+sensorEvent.values[2]);
        if((sensorEvent.values[0]>=-8.0 && sensorEvent.values[0]<=8.0)&&(sensorEvent.values[1]>= 2.0 && sensorEvent.values[1]<=10.0)){
            //def orientation
            mOrientation=DEF_ORIENTATION;

            if(mCameraCharacteristics!=null && mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT){
                mOrientation-=180;
            }
        }
        else if((sensorEvent.values[0]>=-10.0 && sensorEvent.values[0]<=1.0)&&(sensorEvent.values[1]>= -7.5 && sensorEvent.values[1]<=5.0)) {
            // rotated right
            mOrientation=ROTATE_RIGHT;

        }
        else if((sensorEvent.values[0]>=5.0 && sensorEvent.values[0]<=10.5)&&(sensorEvent.values[1]>= -5.0 && sensorEvent.values[1]<=8.05)){
            //Rotated Left
            mOrientation=ROTATE_LEFT;
        }
        /*else{
            mOrientation=UPSIDE_DOWN;
        }*/
    }
            public void setLAYOUT(){
                if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                    LAYOUT=findViewById(R.id.camera_main_layout);//setupLayout;

                }
                else{
                    LAYOUT=findViewById(R.id.camera_main_layout);
                }
            }
}
