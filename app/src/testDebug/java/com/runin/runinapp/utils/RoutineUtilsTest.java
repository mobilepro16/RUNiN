package com.runin.runinapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.runin.runinapp.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit testing for RoutineUtils
 * Created by Samuel Kobelkowsky on 12/19/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class RoutineUtilsTest {
    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);

        // Given a mocked Context injected into the object under test...
        when(context.getResources()).thenReturn(resources);
        when(context.getResources().getQuantityString(R.plurals.long_minutes, 0)).thenReturn("m 'minutos'");
        when(context.getResources().getQuantityString(R.plurals.long_minutes, 1)).thenReturn("m 'minuto'");
        when(context.getResources().getQuantityString(R.plurals.long_minutes, 2)).thenReturn("m 'minutos'");
        when(context.getResources().getQuantityString(R.plurals.long_seconds, 0)).thenReturn("s 'segundos'");
        when(context.getResources().getQuantityString(R.plurals.long_seconds, 1)).thenReturn("s 'segundo'");
        when(context.getResources().getQuantityString(R.plurals.long_seconds, 5)).thenReturn("s 'segundos'");
    }

    @Test
    public void roundTwoDecimals() throws Exception {
        assertEquals(RoutineUtils.roundTwoDecimals(1.2321312), 1.23, 0.00001);
    }

    @Test
    public void getTimeFormatString() throws Exception {
        assertEquals(DateUtils.millisToMinSec(60000), "01:00");
    }

    @Test
    public void getTimeLongFormatString() throws Exception {
        assertEquals(DateUtils.millisToLongTextString(context, 0), "0 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 1000), "1 segundo");
        assertEquals(DateUtils.millisToLongTextString(context, 5000), "5 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 60000), "1 minuto 0 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 61000), "1 minuto 1 segundo");
        assertEquals(DateUtils.millisToLongTextString(context, 65000), "1 minuto 5 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 120000), "2 minutos 0 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 125000), "2 minutos 5 segundos");
        assertEquals(DateUtils.millisToLongTextString(context, 121000), "2 minutos 1 segundo");
    }
}