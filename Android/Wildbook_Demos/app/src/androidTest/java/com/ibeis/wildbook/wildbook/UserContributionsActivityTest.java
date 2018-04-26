package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.PendingIntent.getActivity;
import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by Arjan on 4/22/2018.
 */
@RunWith(AndroidJUnit4.class)
public class UserContributionsActivityTest {
    @Rule
    public ActivityTestRule<UserContributionsActivity> activityActivityTestRule =
            new ActivityTestRule<>(UserContributionsActivity.class,false,true);
    @Before
    public void setup(){
        Intent i = new Intent();
        activityActivityTestRule.launchActivity(i);
        Espresso.registerIdlingResources(activityActivityTestRule.getActivity().getIdlingResource());
    }
   /* @Test
    public void imagesLoadedSuccesfully(){
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }*/
    @Test
    public void clickRecyclerViewItem_DisplayEncounterRelatedImages(){
        //Espresso.registerIdlingResources(activityActivityTestRule.getActivity().getIdlingResource());
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));//recycler view to display images.
        onView(allOf(withId(R.id.dispselimgLayout),withText(R.string.encounterDetailsLabel)));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.imgView)).check(matches(isDisplayed()));

    }
    /*@Test
    public void clickRecyclerViewItem_DisplaysEncounterDetails(){
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(allOf(withId(R.id.dispselimgLayout),withText(R.string.encounterDetailsLabel)));
    }*/
    /*@Test
    public void clickRecyclerViewItem_clickEncounterImageLaunchesImagePreview(){
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.recyclerView))
         .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.imgView)).check(matches(isDisplayed()));

    }*/
}
