package com.runin.runinapp.utils;

import android.content.Context;
import android.content.res.Resources;

import com.runin.runinapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Methods that help in routine screen to get pace time in time format, round two decimal a double etc.
 * Created by Cesar on 09/08/2016.
 */
public class RoutineUtils {
    //private static final String TAG = RoutineUtils.class.getSimpleName();

    public static double roundTwoDecimals(double d) {
        try {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(d));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
