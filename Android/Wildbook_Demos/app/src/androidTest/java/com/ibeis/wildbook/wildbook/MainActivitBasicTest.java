package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

/************************************************
 * Created by Arjan on 3/16/2018.
 ************************************************/
@RunWith(AndroidJUnit4.class)
public class MainActivitBasicTest {
    public static final String message = "Loading Images...";
    ArrayList<Uri> parcelableArrayList = new ArrayList<>();
    //ArrayList<String> imagePaths = new ArrayList<>();
    Uri uri1= Uri.parse("content://com.android.providers.media.documents/document/image%3A17545");
    Uri uri2= Uri.parse("content://com.android.providers.media.documents/document/image%3A17510");
    Intent resultData = new Intent();
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);



   @Test
    public void onReportEncounterClick_LaunchesCameraMainActivity(){
        onView(withId(R.id.RepEnc)).perform(click());
        onView(withId(R.id.imageButton)).check(matches(isDisplayed()));
    }



   /* @Test
    public void onLogoutContextMenuItemClick(){
        openContextualActionModeOverflowMenu();
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.login_Layout)).check(matches(isDisplayed()));
    }*/

    @Test
    public void onNameContextMenuItemClick(){
        openContextualActionModeOverflowMenu();
        onView(withText("Arjan Mundy")).perform(click());
        //onView(withId(R.id.menu_name)).perform(click());
        onView(withId(R.id.user_name_details)).check(matches(isDisplayed()));
    }

    /*public void optionMenuItemSelect_Logout(){
        //onView(withId(R.id.menu)).perform(click());
        openContextualActionModeOverflowMenu();
        onView(withText("Logout"))
                .perform(click());
        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
    }*/

    @Test
    public void onPreferencesContextMenuItemClick(){
        openContextualActionModeOverflowMenu();
        onView(withText("Preferences")).perform(click());
        onView(withId(R.id.radio_group)).check(matches(isDisplayed()));
    }

    @Test
    public void onUploadEncounterClick_GreaterThan10ImagesSelected(){
        Intent resultData = new Intent();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        Uri uri1= Uri.parse("image {U:content://com.android.providers.media.documents/document/image%3A17545}");
        Uri uri2= Uri.parse("image {U:content://com.android.providers.media.documents/document/image%3A17510}");
        for(int i=0;i<6;i++){
            parcelableArrayList.add(uri1);
            parcelableArrayList.add(uri2);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM,parcelableArrayList);
        resultData.putExtras(bundle);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK,resultData);
        Intents.init();
        intending(Matchers.not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        onView(withId(R.id.GallUpload)).perform(click());
        Intents.release();
        onView(withText(R.string.maxuploadString)).
                inRoot(withDecorView(Matchers.not(is(mMainActivityTestRule.getActivity().getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }



}
