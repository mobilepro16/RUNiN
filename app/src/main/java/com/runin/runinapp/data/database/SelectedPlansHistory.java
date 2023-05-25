package com.runin.runinapp.data.database;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import com.runin.runinapp.data.Plan;

import java.util.Date;

/**
 * Class that represent the History of plan trainings ran by the user. The database only stores one record for each plan name. Its main purpose is to tell whether the user has selected this plan at least once.
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class SelectedPlansHistory {
    /**
     * Class constructor
     * @param planName The name of the plan
     * @param length Whether the plan is short or long
     * @param daysInWeek How many days a week the user wants to train
     * @param startDate The start date of the plan
     */
    public SelectedPlansHistory(@NonNull String planName, @NonNull Plan.Length length, int daysInWeek, @NonNull Date startDate) {
        this.planName = planName;
        this.length = length;
        this.daysInWeek = daysInWeek;
        this.startDate = startDate;
    }

    /**
     * The plan name
     */
    @NonNull
    private final String planName;

    /**
     * The length of the plan: Short or Long
     */
    @NonNull
    private final Plan.Length length;

    /**
     * The days of the week the user wanted to run
     */
    private final int daysInWeek;

    /**
     * The date the user wanted to start running
     */
    @NonNull
    private final Date startDate;

    /**
     * Returns the plan length: Short or Long
     *
     * @return The plan length
     */
    @NonNull
    public Plan.Length getLength() {
        return length;
    }

    /**
     * Returns the days of the week the user wanted to run
     *
     * @return The days of the week the user wanted to run
     */
    public int getDaysInWeek() {
        return daysInWeek;
    }

    /**
     * Returns the plan name
     *
     * @return The plan name
     */
    @NonNull
    String getPlanName() {
        return planName;
    }

    /**
     * Returns a ContentValues object suitable for database insertion
     *
     * @return a ContentValues object
     */
    @NonNull
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(RuninContract.SelectedPlansHistoryTable.PLAN_NAME, planName);
        values.put(RuninContract.SelectedPlansHistoryTable.DAYS_IN_WEEK, daysInWeek);
        values.put(RuninContract.SelectedPlansHistoryTable.LENGTH, length.getValue());
        values.put(RuninContract.SelectedPlansHistoryTable.START_DATE, startDate.getTime());

        return values;
    }
}
