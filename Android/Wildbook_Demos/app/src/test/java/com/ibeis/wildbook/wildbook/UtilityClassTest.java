package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Arjan on 4/22/2018.
 */

public class UtilityClassTest {
    private Context mInstrumentationCtx;
    private Utilities mUtilities;
    @Before
    public void setup() {
        mInstrumentationCtx = InstrumentationRegistry.getContext();
        mUtilities = new Utilities(mInstrumentationCtx);
    }
    @Test
    public void currentIdentityTest_True(){
        String expectedIdentityResult = "mundyarjan@gmail.com";
        String actualIdentityResult = mUtilities.getCurrentIdentity();
        assertEquals(expectedIdentityResult,actualIdentityResult);
    }
}
