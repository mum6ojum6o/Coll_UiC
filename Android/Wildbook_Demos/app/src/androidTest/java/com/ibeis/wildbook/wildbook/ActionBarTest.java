package com.ibeis.wildbook.wildbook;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Arjan on 3/30/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ActionBarTest {
ActivityTestRule<BaseActivity> baseActivityActivityTestRule = new ActivityTestRule<BaseActivity>(BaseActivity.class);

}
