package com.ibeis.wildbook.wildbook;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Arjan on 3/29/2018.
 */
@RunWith(AndroidJUnit4.class)
public class CameraMainActivityTest {
    @Rule
    public ActivityTestRule<CameraMainActivity> mCameraMainActivityRule =
            new ActivityTestRule<CameraMainActivity>(CameraMainActivity.class);
   //IntentsTestRule<CameraMainActivity> mIntentRule = new IntentsTestRule<>(CameraMainActivity.class);

    @Test
    public void onPicPreviewButtonClick_LaunchesUploadCamPicsActivity(){
        onView(withId(R.id.picPreview)).perform(click());
        onView(withId(R.id.SelectAllBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.UploadBtn2)).check(matches(isDisplayed()));
        onView(withId(R.id.DiscardBtn2)).check(matches(isDisplayed()));
        onView(withId(R.id.UnselectAll)).check(matches(isDisplayed()));
    }

}
