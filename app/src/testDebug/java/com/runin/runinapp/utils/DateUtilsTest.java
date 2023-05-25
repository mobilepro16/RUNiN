package com.runin.runinapp.utils;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;

/**
 * Unit testing for DateUtils class
 * Created by Samuel Kobelkowsky on 12/19/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class DateUtilsTest {
    @Test
    public void getAge() throws Exception {
        PowerMockito.mockStatic(Log.class);
        assertTrue(DateUtils.getAge(1972, 8, 15) == 45);
    }
}