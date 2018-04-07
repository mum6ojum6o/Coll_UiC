package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Arjan on 4/1/2018.
 */
@RunWith(AndroidJUnit4.class)
public class GalleryUploadImagePreviewRecyclerViewActivityTest {
    public static String TAG="GalleryUploadImagePreviewRecyclerViewActivityTest";
    @Rule
    public ActivityTestRule<GalleryUploadImagePreviewRecyclerViewActivity> galleryUploadImagePreviewRecyclerViewActivityActivityTestRule
            =new ActivityTestRule<>(GalleryUploadImagePreviewRecyclerViewActivity.class,false,false);
    /*@Before
    public void activityInitialization(){
        Log.i(TAG,"Activity Initialization!!");
        Uri uri1= Uri.parse("content://com.android.providers.media.documents/document/image%3A17545");
        intended(allOf(hasComponent(GalleryUploadImagePreviewRecyclerViewActivity.class.getName()),hasData(uri1)));
    }*/
@Test
    public void onActivityCreated(){
        //ArrayList<Uri> parcelableArrayList = new ArrayList<>();
        //Uri uri1= Uri.parse("content://com.android.providers.media.documents/document/image%3A17545");
        //Uri uri2= Uri.parse("content://com.android.providers.media.documents/document/image%3A17510");
        String uri1= "content://com.android.providers.media.documents/document/image%3A17545";
        String uri2= "content://com.android.providers.media.documents/document/image%3A17510";
        String imgPath1="/storage/emulated/0/Pictures/Wildbook/IMG_20180329_195000_-83463885.jpg";
        String imgPath2="/storage/emulated/0/Pictures/Wildbook/IMG_20180318_234225_-1695890068.jpg";
        ArrayList<String> selectedImagesArrayList = new ArrayList<>();
    selectedImagesArrayList.add(imgPath1);selectedImagesArrayList.add(imgPath2);
        ArrayList<String> uriArrayList = new ArrayList<>();
        uriArrayList.add(uri1);
        uriArrayList.add(uri2);
        Intent intent = new Intent();
        intent.putStringArrayListExtra("ImageUris",uriArrayList);
        intent.putStringArrayListExtra("selectedImages",selectedImagesArrayList);
        galleryUploadImagePreviewRecyclerViewActivityActivityTestRule.launchActivity(intent);
        onView(withId(R.id.dispselimgLayout)).check(matches(isDisplayed()));

    }
}
