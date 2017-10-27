package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import static java.lang.System.load;

public class CameraMainActivity extends AppCompatActivity implements  View.OnClickListener{
    public static String SWAP="Rear";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
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
    protected static final int STORAGE_PERMISSION_REQUEST=99;

    private Handler mUIHandler = new Handler(){
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch(what){
                case UPDATE_PIC_PREVIEW:
                    getGeoTagData(mImageFile.getAbsolutePath());
                    mPicPreview.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));


                    mCapturedPics.add(mImageFile.getAbsoluteFile().toString());
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(mImageFile)); // may cause lags...
                    getApplicationContext().sendBroadcast(mediaScanIntent);
                    //mPicPreview.invalidate();
                    break;
            }
        }};
    private ArrayList<String> mCapturedPics = new ArrayList<String>();
    private static File mImageFile;
    private File mImageFolder;
    private static String mImageFileName;
    private ImageReader mImageReader;
    private TextureView mTextureView;
    private RecyclerView mRecycleView;
    private String currentPreview;
    private ImageButton mImageButton;
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
    private FusedLocationProviderClient mFusedLocationClient;
    private Button mCaptureButton;
    private static File mLatestFile;
    private static ImageView mPicPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_main);
        createImageFolder();
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mCaptureButton = (Button) findViewById(R.id.photoButton);
        mPicPreview = (ImageView)findViewById(R.id.picPreview);
        mCaptureButton.setOnClickListener(this);
        mRecycleView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        mImageButton =(ImageButton) findViewById(R.id.imageButton);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(gridLayoutManager);
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

        mImageButton.setOnClickListener(this);
        mPicPreview.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        openBackgroundThread();
        mCapturedPics.clear();
        if(sortFilesToLatest(mImageFolder).length>0) {
            mLatestFile = sortFilesToLatest(mImageFolder)[0];
            mPicPreview.setImageBitmap(BitmapFactory.decodeFile(mLatestFile.getAbsolutePath()));
            currentPreview = mLatestFile.getAbsolutePath().toString();
        }
        if(mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            transformImage(mTextureView.getWidth(), mTextureView.getHeight());
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.photoButton:
                takePhoto();
                break;
            case R.id.picPreview:
                Intent i = new Intent(CameraMainActivity.this,UploadCamPics.class);

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
        }
    }
    @Override
    public void onPause() {
        closeCamera();
        closeBackgoundThread();
        super.onPause();
    }
    private Size mPreviewSize;
    private String mCameraId;
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mCameraManager =cameraManager;
        if(SWAP.equals("Front")) {
            try {
                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) !=
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
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
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
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
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        continue;
                    }
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
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    mCameraId = cameraId;
                    return;
                }

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
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
            Toast.makeText(getApplicationContext(), "Camera Opened!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Focus Lock Unsuccessful", Toast.LENGTH_SHORT).show();
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
    private void openBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera2 background thread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
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

    private void createImageFolder() {
        //Log.i(TAG,"createImageFolder");
        if(ContextCompat.checkSelfPermission(getApplicationContext(),"android.permission.READ_EXTERNAL_STORAGE")== PackageManager.PERMISSION_GRANTED) {
            File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            mImageFolder = new File(imageFile, "camera2Api");
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
                }
            break;
        }
    }

    private File createImageFileName() throws IOException {
        // Log.i(TAG,"createImageFileName");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMG_" + timestamp + "_";
        File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        mImageFileName = imageFile.getAbsolutePath();
        //  Log.i(TAG,"Image FILE NAME="+mImageFileName);
        return imageFile;
    }

    public void takePhoto() {
        if(SWAP.equals("Rear")) {
            lockFocus();
        }
        else{
            captureStillImage();
        }
    }


    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(),mUIHandler));
                }
            };

    private static class ImageSaver implements Runnable {
        private final Image mImage;
        private final Handler mHandler;

        private ImageSaver(Image image) {
            mImage = image;
            mHandler = null;
        }
        private ImageSaver(Image image,Handler handler) {
            mImage = image;
            mHandler = handler;
        }


        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;
            try {
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
            Message message = mHandler.obtainMessage(UPDATE_PIC_PREVIEW);
            mHandler.sendMessage(message);
        }
    }

    private void captureStillImage() {
        try {
            CaptureRequest.Builder captureStillBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureStillBuilder.addTarget(mImageReader.getSurface());
            int rotation = getWindowManager().getDefaultDisplay().getRotation(); CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
                captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                        ORIENTATIONSFRONT.get(rotation));
            }
            else {
                captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                        ORIENTATIONS.get(rotation));
            }
            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                            Toast.makeText(getApplicationContext(),
                                    "Image Captured!", Toast.LENGTH_SHORT).show();
                            unLockFocus();
                        }
                        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                            //   Log.i(TAG,"captureStillImage+ onCapturestarted");
                            try {
                                //createImageFile();
                                mImageFile=createImageFileName();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
            mCameraCaptureSession.capture(
                    captureStillBuilder.build(), captureCallback, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
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
    private void getGeoTagData(final String imagePath){
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                try{
                                    ExifInterface exif = new ExifInterface(imagePath);
                                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,GPS.convert(mLocation.getLatitude()));
                                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,GPS.latitudeRef(mLocation.getLatitude()));
                                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,GPS.convert(mLocation.getLongitude()));
                                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(mLocation.getLongitude()) );
                                    SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                    exif.setAttribute(ExifInterface.TAG_DATETIME,fmt_Exif.format(new Date(mLocation.getTime())));
                                    exif.saveAttributes();
                                }catch(IOException e){e.printStackTrace();}
                            }
                        }
                    });
        }catch(SecurityException e){e.printStackTrace();}

    }
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
         * returns ref for latitude which is S or N.
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


}
