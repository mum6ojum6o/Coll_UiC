package com.cs478.arjan.a3;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
public class Baseball extends AppCompatActivity implements ListingFragment.ListSelectionListener {

    private String TAG="Baseball Activity";
    private FrameLayout mListFrame, mWebsiteFrame;
    private FragmentManager mFragmentManager;
    private WebViewingFragment mWVFragment;
    private ListingFragment listingFragment;
    public static String[] mTitleArray;
    public static String[] mUrlArray;
    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    private static final String TAG_RETAINED_Web_FRAGMENT = "wRetainedFragment";
    private boolean configChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);

         //a = getActionBar();
        //a.show();
        // Get the string arrays with the titles and qutoes
        mTitleArray = getResources().getStringArray(R.array.baseballlist);
        mUrlArray = getResources().getStringArray(R.array.mlbUrls);
        //if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
        setContentView(R.layout.activity_basketball);
            mListFrame = (FrameLayout) findViewById(R.id.list_fragment_container);
            mWebsiteFrame = (FrameLayout) findViewById(R.id.website_fragment_container);

        mFragmentManager = getFragmentManager();
        listingFragment = (ListingFragment) mFragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        mWVFragment = (WebViewingFragment) mFragmentManager.findFragmentByTag(TAG_RETAINED_Web_FRAGMENT);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

       if (listingFragment==null){
            Log.i("Project III", "Basketball listingFragment is null");
            listingFragment = new ListingFragment();
            //mWVFragment = new WebViewingFragment();
            fragmentTransaction.add(listingFragment,TAG_RETAINED_FRAGMENT);
            //fragmentTransaction.add(mWVFragment,TAG_RETAINED_Web_FRAGMENT);
            fragmentTransaction.replace(R.id.list_fragment_container,listingFragment);
            fragmentTransaction.commit();
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());
        }
        else if (listingFragment!=null && mWVFragment == null) {
            Log.i("Project III", "Basketball listingFragment is NOT null and configuration change");
            mWVFragment = new WebViewingFragment();
        }
        else if (listingFragment!=null && mWVFragment != null && !mWVFragment.isAdded()) {
            Log.i("Project III",TAG+"mWVFragment is not null");
            Log.i("project III", TAG+" webView fragment is NOT added");
                // Start a new FragmentTransaction
                fragmentTransaction = mFragmentManager.beginTransaction();
                // Add the QuoteFragment to the layout
                if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
                    fragmentTransaction.replace(R.id.website_fragment_container, mWVFragment);
                //fragmentTransaction.add(R.id.website_fragment_container, mWVFragment);
                // Add this FragmentTransaction to the backstack
                fragmentTransaction.addToBackStack(null);

                // Commit the FragmentTransaction
                fragmentTransaction.commit();
                Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());

                mFragmentManager.executePendingTransactions();
                Log.i("last position", "mWVFragment last position"+ mWVFragment.getShownIndex());

                setLayout();
            }
            else
            setLayout();

        if (mWVFragment == null){
            Log.i("mWVFragment","is NULL");
            mWVFragment = new WebViewingFragment();
            fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack("wMVFrag");
            fragmentTransaction.commit();
            Log.i("P","Before pending execution");
            mFragmentManager.executePendingTransactions();
            Log.i("P","After pending execution");
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());
            fragmentTransaction.add(mWVFragment,TAG_RETAINED_Web_FRAGMENT);
            fragmentTransaction.replace(R.id.website_fragment_container,mWVFragment,TAG_RETAINED_Web_FRAGMENT);
        }

        // fragmentTransaction.commit();
        Log.i("proj III", "frag trans commit");
        mFragmentManager.executePendingTransactions();
        // Add a OnBackStackChangedListener to reset the layout when the back stack changes
        mFragmentManager
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Log.i("Proj III","Backstack changed");
                        setLayout();
                    }
                });

        Log.i("proj III","Exiting oncreate");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i("project III", TAG+" Create Options menu");
        menu.add(0, 1, 0, "Basketball");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("project III",TAG+" in optionsItems Selected");
        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(Baseball.this,Basketball.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void setLayout() {
        Log.i("Project III","In Layout");
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("project III", "Landscape");
            // Determine whether the WebView Fragment has been added
            if (!mWVFragment.isAdded()) {
                Log.i("Proj III", "mWVFrag not added;creating layout");
                // Make the TitleFragment occupy the entire layout
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT));
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT));
            } else {
                Log.i("Proj III", "mWVFrag not added");
                // Make the TitleLayout take 1/3 of the layout's width
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 1f));
                // Make the webSiteLayout take 2/3's of the layout's width
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 2f));
            }
        }
        else{//in portrait mode
            if (!mWVFragment.isAdded()) {
                Log.i("back button ", " in portrait mode webview is added");
                // Make the TitleFragment occupy the entire layout
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT));
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT));
            } else {

                Log.i("back button", " in portrait mode webview is not added");
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 0f));
                 mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 1f));
            }

        }
    }
    public void onListSelection(int index){
        // If the WebSite Fragment has not been added, add it now
        if (!mWVFragment.isAdded()) {

            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            // Add the QuoteFragment to the layout
            fragmentTransaction.add(R.id.website_fragment_container,
                    mWVFragment);
            Log.i("proj III", "added to backstack");
            // Add this FragmentTransaction to the backstack
            fragmentTransaction.addToBackStack(null);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

            // Force Android to execute the committed FragmentTransaction
            mFragmentManager.executePendingTransactions();

        }
        else{
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();

        }
            Log.i("log","retained fragment index:"+ mWVFragment.getShownIndex());
         if ( mWVFragment.getShownIndex() != index) {

            // Tell the WebViewingFragment to show the quote string at position index
            mWVFragment.showUrl(index,1);

        }


    }
    protected void onDestroy() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
        super.onPause();
        if(isFinishing()) {
            FragmentManager fm = getFragmentManager();
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.

            fm.beginTransaction().remove(listingFragment).commit();
            fm.beginTransaction().remove(mWVFragment).commit();
            //fm.executePendingTransactions();
        }
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, "edu.uic.cs478.project3") !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"edu.uic.cs478.project3"}, 0);
        }
    }

    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, go ahead and display map
            Log.i("PojectII", "Permission Granted!!!") ;
        }
        else {
            Toast.makeText(this, "BUMMER: No Permission :-(", Toast.LENGTH_LONG).show() ;
            onStop();
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
        super.onStop();
    }

   /* public void onBackPressed(){
        super.onBackPressed();
        Log.i("Back pressed", "Back Pressed");
        FragmentManager fm = getFragmentManager();
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            fm.beginTransaction().remove(listingFragment).commit();
            fm.beginTransaction().remove(mWVFragment).commit();
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
        }
        else{
            fm.beginTransaction().remove(mWVFragment).commit();

            fm.beginTransaction().show(listingFragment).commit();
           //
        }

    }*/

}


