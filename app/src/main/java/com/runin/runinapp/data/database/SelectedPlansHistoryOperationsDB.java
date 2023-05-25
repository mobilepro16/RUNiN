package com.runin.runinapp.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.util.Log;

import com.runin.runinapp.data.Plan;

import java.util.Date;

/**
 * Operations on database for plans history. The database only stores one record for each plan name. Its main purpose is to tell whether the user has selected this plan at least once.
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class SelectedPlansHistoryOperationsDB {
    private static final String TAG = CompletedIndoorTrackOperationsDB.class.getSimpleName();

    /**
     * Returns the record containing the plan. Only the first one found for a given plan name
     *
     * @param planName The plan name
     * @param context  The application context
     * @return The record of the plan selected
     */
    public SelectedPlansHistory get(@NonNull String planName, @NonNull Context context) {

        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        Cursor cursor;
        try {
            SelectedPlansHistory result = null;
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + RuninContract.SelectedPlansHistoryTable.TABLE_NAME + " WHERE " + RuninContract.SelectedPlansHistoryTable.PLAN_NAME + "=?", new String[]{planName});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                final int days = cursor.getInt(cursor.getColumnIndex(RuninContract.SelectedPlansHistoryTable.DAYS_IN_WEEK));
                final Plan.Length length = Plan.Length.values()[cursor.getInt(cursor.getColumnIndex(RuninContract.SelectedPlansHistoryTable.LENGTH))];
                final String title = cursor.getString(cursor.getColumnIndex(RuninContract.SelectedPlansHistoryTable.PLAN_NAME));
                final Date date = new Date(cursor.getLong(cursor.getColumnIndex(RuninContract.SelectedPlansHistoryTable.START_DATE)));

                result = new SelectedPlansHistory(title, length, days, date);
            }
            cursor.close();

            return result;
        } catch (Exception ex) {
            Log.e(TAG, "Cannot retrieve record", ex);
        }

        return null;
    }

    /**
     * Adds a plan history to the database only if there is no such plan in the database
     *
     * @param history The record to add
     * @param context the application context
     */
    public void add(@NonNull SelectedPlansHistory history, @NonNull Context context) {
        if (get(history.getPlanName(), context) != null) return;

        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getWritableDatabase();
        sqLiteDatabase.insert(RuninContract.SelectedPlansHistoryTable.TABLE_NAME, null, history.getContentValues());
    }
}