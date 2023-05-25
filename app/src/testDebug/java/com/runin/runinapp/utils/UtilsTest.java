package com.runin.runinapp.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Unit tests for Utils
 * Created by Samuel Kobelkowsky on 12/19/17.
 */
public class UtilsTest {
    @Test
    public void dateOnly() throws Exception {
        Date date = new Date(1513726431117L);
        Assert.assertEquals(DateUtils.dateOnly(date).getTime(), 1513663200000L);
    }
}