package com.cs478.arjan.a3;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

/**
 * Created by Arjan on 3/17/2017.
 */

public class WebViewingFragment extends Fragment {
    private static final String TAG = "WebViewingFragment";
    private WebView mWebview = null;
    private int mCurrIdx = -1;
    private int mUrlArrLen;
    private WebSettings mWebSettings=null;
    private String lastPrintedUrl=null;



   public int getShownIndex() {
        return mCurrIdx;
    }

    public void showUrl(int newIndex, int a){
        Log.i("Project III",TAG+"in ShowUrl:"+newIndex+" a:"+a);
        if (newIndex < 0 || newIndex >= mUrlArrLen)
            return;
        mCurrIdx = newIndex;
        if (a==0) {
            //mWebview.setWebViewClient(new WebViewClient());
           //mWebView.getSettings().setDomStorageEnabled(true);
            Log.i("Proj","Throwing error"+ Basketball.mUrlArray[newIndex]);
            mWebview.getSettings().setJavaScriptEnabled(true);
            lastPrintedUrl=Basketball.mUrlArray[newIndex];
            mWebview.loadUrl(Basketball.mUrlArray[newIndex]);
            //Log.i("",mWebview.getProgress());
        }
        else if (a==1) {
            lastPrintedUrl = Baseball.mUrlArray[newIndex];
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.loadUrl(Baseball.mUrlArray[mCurrIdx]);
        }

    }
    public String getLastPrintedUrl(){
        return lastPrintedUrl;
    }
    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // Called to create the content view for this Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreateView()");


        // Inflate the layout defined in website_layout.xml

        return inflater.inflate(R.layout.website_layout,container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mWebview = (WebView)getActivity().findViewById(R.id.webview);
        if (getActivity() instanceof Basketball)
             mUrlArrLen = Basketball.mUrlArray.length;
        else
            mUrlArrLen = Baseball.mUrlArray.length;

        mWebview.setWebViewClient(new WebViewClient());
        //mWebView.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebSettings = mWebview.getSettings();
        mWebSettings.setDomStorageEnabled(true);


    }

        @Override
    public void onStart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
        super.onStart();
            if(mCurrIdx !=-1)
            mWebview.loadUrl(getLastPrintedUrl());

        }

     @Override
    public void onResume() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
        super.onResume();
         Log.i("Project III",TAG+" "+mCurrIdx);


    }

     @Override
    public void onPause() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onPause()");
        super.onPause();
    }

     @Override
    public void onStop() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStop()");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onDetach()");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onDestroyView()");
        super.onDestroyView();
    }



}
