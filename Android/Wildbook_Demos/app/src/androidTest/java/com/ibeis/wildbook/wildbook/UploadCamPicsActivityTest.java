package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

/***********************************
 * Created by Arjan on 3/29/2018.
 ************************************/
@RunWith(AndroidJUnit4.class)
public class UploadCamPicsActivityTest {
    @Rule
    public ActivityTestRule<UploadCamPicsActivity> uploadCamPicsActivityActivityTestRule=
            new ActivityTestRule<UploadCamPicsActivity>(UploadCamPicsActivity.class,false,false);

    //not working because we have to fake the intent data.
    @Test
    public void onCancelButtonClick_Launches_CameraMainActivity(){
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext,UploadCamPicsActivity.class);
        ArrayList<String> files = new ArrayList<>();
        files.add("test1");
        files.add("test2");
        files.add("test3");
        intent.putExtra("Files",files);
        uploadCamPicsActivityActivityTestRule.launchActivity(intent);
        onView(withId(R.id.DiscardBtn2)).perform(click());
        onView(withId(R.id.picPreview)).check(matches(isDisplayed()));
    }
    @Test
    public void onPicPreviewRecyclerViewClick_LaunchesImagePreviewActivity(){
        //Espresso.onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.onClick());
        testSetup();
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.imgView)).check(matches(isDisplayed()));
    }
    @Test
    public void onNoImageSelectedUploadClick_DisplaysDialog(){
        testSetup();
        onView(withId(R.id.UploadBtn2)).perform(click());
        onView(allOf(withId(R.id.dialog_txt),withText(R.string.noimageselected)));
    }

    /*@Test
    public void onLonClickOnRecyclerViewItem_SelectsTheItem(){
        testSetup();
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,longClick()));
        onView(withId(R.id.recyclerView).)
    }*/

    @Test
    public void onImageSelectedUploadClick_RedirectsToMainActivity(){
        testSetup();
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,longClick()));
        onView(withId(R.id.UploadBtn2)).perform(click());
        onView(allOf(withId(R.id.dialog_txt),withText(R.string.thankyou)));
    }
    public void testSetup(){
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext,UploadCamPicsActivity.class);
        ArrayList<String> files = new ArrayList<>();
        files.add("test1");
        files.add("test2");
        files.add("test3");
        intent.putExtra("Files",files);
        uploadCamPicsActivityActivityTestRule.launchActivity(intent);
    }

}
