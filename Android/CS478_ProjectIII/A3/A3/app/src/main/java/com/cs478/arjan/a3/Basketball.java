package com.cs478.arjan.a3;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cs478.arjan.a3.ListingFragment.ListSelectionListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class Basketball extends AppCompatActivity implements ListSelectionListener {

    private String TAG="Basketball Activity";
    private FrameLayout mListFrame, mWebsiteFrame;
    private FragmentManager mFragmentManager;
    private ListingFragment listingFragment;
    //private WebViewingFragment mWVFragment = new WebViewingFragment();
    private WebViewingFragment mWVFragment;
    public static String[] mTitleArray;
    public static String[] mUrlArray;
    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    private static final String TAG_RETAINED_Web_FRAGMENT = "wRetainedFragment";

    //private FrameLayout mTitleFrameLayout, mWebSiteFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);

        // Get the string arrays with the titles and qutoes
        mTitleArray = getResources().getStringArray(R.array.basketballlist);
        mUrlArray = getResources().getStringArray(R.array.nbaUrls);
        Log.i("proj","mURLArray IS NOT NULL:"+mUrlArray.length);
        setContentView(R.layout.activity_basketball);

            //setContentView(R.layout.activity_basketball);
            mListFrame = (FrameLayout) findViewById(R.id.list_fragment_container);
            mWebsiteFrame = (FrameLayout) findViewById(R.id.website_fragment_container);

        mFragmentManager = getFragmentManager();
        listingFragment = (ListingFragment) mFragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        mWVFragment = (WebViewingFragment) mFragmentManager.findFragmentByTag(TAG_RETAINED_Web_FRAGMENT);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mWVFragment !=null)
        Log.i("last position", "mWVFragment last position"+ mWVFragment.getShownIndex());
        // Start a new FragmentTransaction


        fragmentTransaction = mFragmentManager.beginTransaction();
        if (listingFragment==null){
           // FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            // Add the listingFragment to the layout
            Log.i("Project III", "Basketball listingFragment is null");
            listingFragment = new ListingFragment();
            //mWVFragment = new WebViewingFragment();
            fragmentTransaction.add(listingFragment,TAG_RETAINED_FRAGMENT);
           //fragmentTransaction.add(mWVFragment,TAG_RETAINED_Web_FRAGMENT);
            fragmentTransaction.replace(R.id.list_fragment_container,listingFragment);
            //dont add because it will throw and null pointer exception. addtobackstack has a listener defined
             // which will be called everytime the backstack changes. the listener calls setLayout.
             //which checks if wWVFragment is added or not.
             //fragmentTransaction.addToBackStack(null);

             fragmentTransaction.commit();
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());

        }
         else if (listingFragment!=null ){

             Log.i("Project III", "Basketball listingFragment is NOT null and configuration change");

             /*if(!listingFragment.isAdded()) {
                 fragmentTransaction.add(listingFragment,TAG_RETAINED_FRAGMENT);
                 fragmentTransaction.replace(R.id.list_fragment_container, listingFragment);
             }*/
             if(mWVFragment==null){
                 mWVFragment = new WebViewingFragment();
             }
            else  if (mWVFragment!=null){
                 Log.i("Project III",TAG+"mWVFragment is not null");
                 if (!mWVFragment.isAdded()) {
                     Log.i("proj III", TAG+" webView fragment is NOT added");
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
                    // Force Android to execute the committed FragmentTransaction
                    mFragmentManager.executePendingTransactions();
                     Log.i("last position", "mWVFragment last position"+ mWVFragment.getShownIndex());
                    // mWVFragment.showUrl(mWVFragment.getShownIndex(),0);
                    setLayout();
             }
             else { Log.i("","Inside else");

                    // mWVFragment.showUrl(mWVFragment.getShownIndex(), 0);

                     setLayout();
                 }
             }

         }
        if (mWVFragment == null){
            Log.i("mWVFragment","is NULL");
            mWVFragment = new WebViewingFragment();
            fragmentTransaction = mFragmentManager.beginTransaction();
            //fragmentTransaction.addToBackStack(null);
            //fragmentTransaction.commit();
            fragmentTransaction.addToBackStack("wMVFrag");
            fragmentTransaction.commit();
            Log.i("P","Before pending execution");
            mFragmentManager.executePendingTransactions();
            Log.i("P","After pending execution");
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());
            fragmentTransaction.add(mWVFragment,TAG_RETAINED_Web_FRAGMENT);
             fragmentTransaction.replace(R.id.website_fragment_container,mWVFragment,TAG_RETAINED_Web_FRAGMENT);
        }

        //fragmentTransaction.commit();
            mFragmentManager
                    .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        public void onBackStackChanged() {
                            Log.i("Basketball", "Backstack changed");
                            setLayout();
                        }
                    });
        //setLayout();


    } //onCreate ends
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, "Baseball");
         return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("project III",TAG+" in optionsItems Selected");
        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(Basketball.this,Baseball.class);
                startActivity(intent);
                break;
        }
        return true;
    }
    private void setLayout() {
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("Project III", "Basketball in Landscape mode");
            // Determine whether the WebView Fragment has been added
            if (!mWVFragment.isAdded()) {
                // Make the TitleFragment occupy the entire layout
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT));
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT));
            } else {
                // Make the TitleLayout take 1/3 of the layout's width
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 1f));
                // Make the webSiteLayout take 2/3's of the layout's width
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 2f));
            }
        }
        else{
            Log.i("Project III", "Basketball in Portrait mode");
            if (!mWVFragment.isAdded()) {

                // Make the TitleFragment occupy the entire layout
                Log.i("project III", "mWVFrag not added in portrait");
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT));
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT));

            } else {
                Log.i("project III", "mWVFrag added in portrait");
                // Make the TitleLayout take 1/3 of the layout's width
                mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 0f));
                // Make the webSiteLayout take 2/3's of the layout's width
                mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                        MATCH_PARENT, 1f));
            }

        }
    }
    public void onListSelection(int index){
        Log.i("Project III", TAG+"onListSelection");

        // If the WebSite Fragment has not been added, add it now
        if (!mWVFragment.isAdded()) {
            Log.i("proj III", TAG+" webView fragment is NOT added");
            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();
            fragmentTransaction.add(R.id.website_fragment_container,
                        mWVFragment);
            // Add this FragmentTransaction to the backstack
            fragmentTransaction.addToBackStack(null);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

            // Force Android to execute the committed FragmentTransaction
            mFragmentManager.executePendingTransactions();
        }
        else{
            Log.i ("mWVFragment","is not added on click");
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            this.getFragmentManager().popBackStack(TAG_RETAINED_Web_FRAGMENT,0);
            fragmentTransaction.commit();
            fragmentTransaction = mFragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.website_fragment_container,mWVFragment,TAG_RETAINED_Web_FRAGMENT);
            mWVFragment.onActivityCreated(null);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());

        }
        Log.i("proj III", TAG+" webView fragment is NOW added");
        if (mWVFragment.getShownIndex() != index) {
                Log.i("Project III", TAG+" in the index is"+index);
            // Tell the WebViewingFragment to show the quote string at position index
            mWVFragment.showUrl(index,0);

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
            Log.i("dsadas", "finishing");
            FragmentManager fm = getFragmentManager();
            Log.i("","Fragment count "+mFragmentManager.getBackStackEntryCount());
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.
            fm.beginTransaction().remove(listingFragment).commit();
            fm.beginTransaction().remove(mWVFragment).commit();
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
   /* public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 0f));
            // Make the webSiteLayout take 2/3's of the layout's width
            mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 1f));
        }
        else{
            mListFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 1f));
            // Make the webSiteLayout take 2/3's of the layout's width
            mWebsiteFrame.setLayoutParams(new LinearLayout.LayoutParams(0,
                    MATCH_PARENT, 2f));
        }
    }*/

}
