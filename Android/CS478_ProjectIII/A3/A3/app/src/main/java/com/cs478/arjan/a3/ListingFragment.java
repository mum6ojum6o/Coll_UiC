package com.cs478.arjan.a3;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/*************************************
 * Created by Arjan on 3/16/2017.
 *********************************/

public class ListingFragment extends ListFragment {
    private String TAG="ListingFragment";
    private ListSelectionListener mListener = null;
    private int mCurrIdx = -1;
    private int actType;
    private int lastPosition = -1;
    // Callback interface that allows this Fragment to notify the QuoteViewerActivity when
    // user clicks on a List Item
    public interface ListSelectionListener {
        public void onListSelection(int index);
    }
    public void onListItemClick(ListView l, View v, int pos, long id) {
        // Indicates the selected item has been checked
        getListView().setItemChecked(pos, true);
        // Inform the Activity that the item in position pos has been selected
        mListener.onListSelection(pos);

    }
//how to call these functions??
   /* public void setLastPosition(){
            lastPosition = mCurrIdx;
    }
    public int getLastPosition(){
        return lastPosition;
    } */
    public void onAttach(Context activity) {

        Log.i(TAG, getClass().getSimpleName() + ":entered onAttach()");
        super.onAttach(activity);
        try {

            // Set the ListSelectionListener for communicating with the Activity
            mListener = (ListSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
        Log.i(TAG, getClass().getSimpleName() + ":exiting onAttach()");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":onCreate()");
        super.onCreate(savedInstanceState);
        // Retain this Fragment across Activity reconfigurations
        setRetainInstance(true);

    }


    public void onActivityCreated(Bundle savedState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onActivityCreated()");
        super.onActivityCreated(savedState);
        /*if (-1 != mCurrIdx) {
            getListView().setItemChecked(mCurrIdx, true);
            Log.i("Project III",TAG+" mCurrIdx="+mCurrIdx);
        }
        else*/ if (getActivity() instanceof Basketball && mCurrIdx ==-1) {
            // Set the list adapter for the ListView
            // Discussed in more detail in the user interface classes lesson
            Log.i("Project III",TAG+" Instance of Basketball mCurrIdx="+mCurrIdx);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    R.layout.list_layout, Basketball.mTitleArray));
        }
        else  if (getActivity() instanceof Baseball && mCurrIdx ==-1){
            Log.i("Project III",TAG+" Instance of Baseball mCurrIdx="+mCurrIdx);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    R.layout.list_layout, Baseball.mTitleArray));
        }

        // Set the list choice mode to allow only one selection at a time
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
    public void onStart() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, getClass().getSimpleName() + ":entered onResume()");
        super.onResume();
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
