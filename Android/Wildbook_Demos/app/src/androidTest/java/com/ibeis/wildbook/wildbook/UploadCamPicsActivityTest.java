package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Arjan on 3/29/2018.
 */
@RunWith(AndroidJUnit4.class)
public class UploadCamPicsActivityTest {
    @Rule
    public ActivityTestRule<UploadCamPicsActivity> uploadCamPicsActivityActivityTestRule=
            new ActivityTestRule<UploadCamPicsActivity>(UploadCamPicsActivity.class);

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

}
