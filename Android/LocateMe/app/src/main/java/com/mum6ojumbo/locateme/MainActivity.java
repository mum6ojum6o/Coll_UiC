package com.mum6ojumbo.locateme;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,OnSuccessListener,OnFailureListener,
        GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback{
    public static final String TAG="LocateMe";
    public DrawerLayout mDrawerLayout;
    GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private Button mFetchLocBtn,mStartTransmittingBtn,mStoptransmitting;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int LOC_REQ_CODE=345;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest.Builder mLocBuilder;
    private int REQUEST_CHECK_SETTINGS = 543;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates=false;
    private TextView textViewLon,textViewLat;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState !=null)
            updateSavedState(savedInstanceState);

        setContentView(R.layout.activity_main_drawer_included);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch(menuItem.getItemId())
                        {
                            case R.id.username:
                                Toast.makeText(getApplicationContext(),"You selected Username:",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.share:
                                Toast.makeText(getApplicationContext(),"You selected share option:",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.signout_menu_item:
                                signOutUser();
                                break;
                        }
                        return true;
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.bringToFront();
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        toolbar.setVisibility(View.VISIBLE);
        mFetchLocBtn = (Button)findViewById(R.id.SignOutBtn);
        mStartTransmittingBtn = (Button)findViewById(R.id.TransmittingLocBtn);
        mStoptransmitting= (Button)findViewById(R.id.StopLocBtn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mStartTransmittingBtn.setOnClickListener(this);
        mStoptransmitting.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFetchLocBtn.setOnClickListener(this);
        mapFragment.getMapAsync(this);
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this,this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        //if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") ==PackageManager.PERMISSION_GRANTED) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location aLocation : locationResult.getLocations()) {
                        //update UI
                        Log.i("MainAct", "from Location Callback");
                        mCurrentLocation = aLocation;
                        //updateUI();
                        updateMarker();
                        goOnline();
                    }
                }

                ;
            };


    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainAct","In onResume");
        if(!mGoogleApiClient.isConnected()){
            //mGoogleApiClient.connect();
        }
        if(ContextCompat.checkSelfPermission(this,"android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED) {

            createLocationRequest();
            mLocBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(mLocBuilder.build());
            task.addOnSuccessListener(this);
            task.addOnFailureListener(this);
            Log.i("MainAct","LocationUpdates Status:"+mRequestingLocationUpdates);
            if(mRequestingLocationUpdates)
                startLocationUpdates();
        }
/*
        else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION"},LOC_REQ_CODE);
        }
*/

    }

    @Override
    protected void onPause(){
        super.onPause();
        //mGoogleApiClient.disconnect();
        //stopLocationUpdates();
    }
    @Override
    protected void onStop(){
        super.onStop();
   }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.SignOutBtn:
                signOutUser();
                break;
            case R.id.TransmittingLocBtn:
                    startLocationUpdates();
                break;
            case R.id.StopLocBtn:
                stopLocationUpdates();
                break;
        }
    }

    public void signOutUser(){
        stopLocationUpdates();
        FirebaseAuth.getInstance()
                .signOut();
        AuthenticationActivity.getGoogleSignInClient().signOut();
        mGoogleApiClient.disconnect();
        startActivity(new Intent(MainActivity.this,AuthenticationActivity.class));
        finish();
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] result ){
        switch(requestCode){
            case LOC_REQ_CODE:
                if(permissions.length>0 && result[0]==PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                    stopLocationUpdates();
                }
                else{
                    Log.i("MainAct","Location Services denied!!");
                    Toast.makeText(this,"Please grant this app access to your Location Services!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void getLastLocation(){
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                mCurrentLocation=location;
                                Toast.makeText(MainActivity.this, "Longitude:" + location.getLongitude() + "Latitude:" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (SecurityException e ){e.printStackTrace();}
    }
    protected void createLocationRequest(){
        Log.i("MainAct","Creating Location Request");
        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION"},LOC_REQ_CODE);
    }

    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED) {
            Log.i("MainAct","Location Updates Started!");
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            mRequestingLocationUpdates=true;
        }
        else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION"},LOC_REQ_CODE);
    }
    private void stopLocationUpdates(){
        Log.i("MainAct","Stopping Location Services");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates=false;
    }
    @Override
    public void onSuccess(Object o) {
        Log.i("MainAct","Sucess");
        if(ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED) {
            if (o instanceof LocationSettingsResponse) {
                LocationSettingsResponse locSettResponse = (LocationSettingsResponse) o;
                Toast.makeText(this, "Location Services enabled!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{"android.permission.ACCESS_FINE_LOCATION"},LOC_REQ_CODE);
        }

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof ResolvableApiException) {
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(MainActivity.this,
                        REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                // Ignore the error.
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection with Play Services Failed!",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putBoolean("LOCATION_UPDATES_STATUS",mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }
    protected void updateSavedState(Bundle savedInstanceState){
        if(savedInstanceState.containsKey("LOCATION_UPDATES_STATUS"))
            mRequestingLocationUpdates = savedInstanceState.getBoolean("LOCATION_UPDATES_STATUS");
    }
    private void updateUI(){
        if(mCurrentLocation!=null) {
            textViewLon.setText("Longitude:" + mCurrentLocation.getLongitude());
            textViewLat.setText("Longitude:"+mCurrentLocation.getLatitude());
        }

    }
    public void goOnline(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        FireBaseDatabaseRec aRec=new  FireBaseDatabaseRec(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                mCurrentLocation.getLongitude(),mCurrentLocation.getLatitude(),formatter.format(aDate));
        Log.i(TAG,mCurrentLocation.getLongitude()+" "+mCurrentLocation.getLatitude());
        mDatabase.child("LocationsHistory").child(FirebaseAuth.getInstance().getUid()).setValue(aRec);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        updateMarker();
    }
    public void updateMarker(){
        LatLng position=null;
        mGoogleMap.clear();
        Log.i("MainAct","update Marker");
        if(ContextCompat.checkSelfPermission(this,"android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED) {
            if (mCurrentLocation != null) {
                Log.i(TAG,"currentLocation retreived");
                position = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(position).title("Your current Location!"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f));
            } else {
                getLastLocation();
                if (mCurrentLocation != null) {
                    position = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions().position(position).title("Your current Location!"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f));
                }
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{"android.permission.ACCESS_FINE_LOCATION"},LOC_REQ_CODE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
class FireBaseDatabaseRec{
    public String name;
    public double latitude,longitude;
    public String timestamp;
    FireBaseDatabaseRec(String name,double lon, double lat,String date){
        this.name=name;
        this.longitude=lon;
        this.latitude=lat;
        timestamp=date;
    }
}