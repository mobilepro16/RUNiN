package com.runin.runinapp.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.util.Log;

import com.runin.runinapp.App;
import com.runin.runinapp.data.OutdoorAppState.WorkoutStatus;
import com.runin.runinapp.data.OutdoorAppState.WorkoutType;
import com.runin.runinapp.data.database.RuninContract.OutdoorWorkoutsHistoryTable;
import com.runin.runinapp.utils.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database operations for the workouts history
 * Created by Samuel Kobelkowsky on 11/24/17.
 */
public class OutdoorWorkoutsHistoryOperationsDB {
    private static final String TAG = OutdoorWorkoutsHistoryOperationsDB.class.getSimpleName();

    /**
     * Ads a record to the database
     *
     * @param contentValues The record to add
     * @param context       The application context
     */
    public long add(@NonNull ContentValues contentValues, @NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getWritableDatabase();
        return sqLiteDatabase.insert(OutdoorWorkoutsHistoryTable.TABLE_NAME, null, contentValues);
    }

    /**
     * Updates a record in the database
     *
     * @param contentValues The values to update
     * @param workoutId     The ID of the record to update
     * @param context       The application context
     */
    public void update(@NonNull ContentValues contentValues, long workoutId, @NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.update(OutdoorWorkoutsHistoryTable.TABLE_NAME, contentValues, OutdoorWorkoutsHistoryTable._ID + "=" + workoutId, null);
    }

    /**
     * Returns a list of Improve Yourself workouts
     *
     * @param context application context
     * @return All the Improve Yourself workouts in the database
     */
    @NonNull
    public List<OutdoorWorkoutsHistory> getImproveYourselfHistory(@NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE " + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + String.valueOf(WorkoutType.ImproveYourself.getValue());
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        ArrayList<OutdoorWorkoutsHistory> result = new ArrayList<>();

        while (cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DATE)));
            WorkoutType workoutType = WorkoutType.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.WORKOUT_TYPE))];

            OutdoorWorkoutsHistory history = new OutdoorWorkoutsHistory(date, workoutType);

            history.setId(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable._ID)));
            history.setObjectiveDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_DISTANCE)));
            history.setObjectiveTimeMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_TIME)));
            history.setDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DISTANCE)));
            history.setDurationMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DURATION)));
            history.setMaximumSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MAXIMUM_SPEED)));
            history.setAverageSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.AVERAGE_SPEED)));
            history.setCompleted(cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.COMPLETED)) >= 1);
            history.setMeasuringUnit(App.LengthUnit.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MEASURING_UNIT))]);
            history.setStatus(WorkoutStatus.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.STATUS))]);

            Log.i(TAG, String.format("History: %s, Objective: %.3f km, Distance: %.3f km, Speed: %.1f km/h, Completed: %s, Unit: %s",
                    history.getDate(), history.getObjectiveDistanceKm(), history.getDistanceKm(), history.getAverageSpeedKmPerHour(), history.isCompleted() ? "yes" : "no", history.getMeasuringUnit().name()));
            result.add(history);
        }

        cursor.close();

        return result;
    }

    /**
     * Returns the distance ran today
     *
     * @param context The application context
     * @return The distance ran today
     */
    public double getTodayDistance(@NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        Date today = new Date();
        try {
            today = DateUtils.dateOnly(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        double distance = 0;
        String selectQuery = "SELECT " + OutdoorWorkoutsHistoryTable.DISTANCE + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE " + OutdoorWorkoutsHistoryTable.DATE + ">=" + today.getTime();
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                double d = c.getDouble(0);

                distance += d;
            } while (c.moveToNext());
        }

        c.close();

        return distance;
    }

    /**
     * Returns the last distance ran
     *
     * @param context The application context
     * @return The last distance ran
     */
    public double getLastDistance(@NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        double distance = 0;
        String selectQuery = "SELECT " + OutdoorWorkoutsHistoryTable.DISTANCE + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME;
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                distance = c.getFloat(0);
            } while (c.moveToNext());
        }

        c.close();

        return distance;
    }

    /**
     * Returns the total distance ran
     *
     * @param context The application context
     * @return The total distance ran
     */
    public double getTotalDistance(@NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        double distance = 0;
        String selectQuery = "SELECT " + OutdoorWorkoutsHistoryTable.DISTANCE + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME;
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                double d = c.getDouble(0);
                distance += d;
            } while (c.moveToNext());
        }

        c.close();

        return distance;
    }

    /**
     * Returns all the different trainings completed for a given plan and stage
     *
     * @param planId  The plan id
     * @param stage   the stage sequence
     * @param context The application context
     * @return a list of the different trainings completed by the user
     */
    public int getDistinctCompletedTrainings(int planId, int stage, @NonNull Context context) {

        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " AND "
                + OutdoorWorkoutsHistoryTable.COMPLETED + ">0";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        ArrayList<Integer> trainings = new ArrayList<>();

        while (cursor.moveToNext()) {
            final int trainingId = cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.TRAINING_ID));

            boolean found = false;
            for (Integer training : trainings) {
                if (training == trainingId) {
                    found = true;
                    break;
                }
            }

            if (!found) trainings.add(trainingId);
        }

        cursor.close();

        Log.i(TAG, String.format("Found %d trainings run for plan: %d and stage: %d", trainings.size(), planId, stage));

        return trainings.size();
    }

    /**
     * Returns the last workoutId of a training in a plan if it has been completed.
     *
     * @param planId   The plan ID
     * @param stage    the stage sequence
     * @param training The training sequence
     * @param context  the application context
     * @return whether the user has completed the training or -1 if the training has never been completed.
     */
    public OutdoorWorkoutsHistory workoutCompleted(int planId, int stage, int training, @NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT " + OutdoorWorkoutsHistoryTable._ID + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " AND "
                + OutdoorWorkoutsHistoryTable.TRAINING_ID + "=" + String.valueOf(training) + " AND "
                + OutdoorWorkoutsHistoryTable.COMPLETED + ">0"
                + " ORDER BY " + OutdoorWorkoutsHistoryTable._ID + " DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        try {
            OutdoorWorkoutsHistory result = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                Date date = new Date(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DATE)));
                WorkoutType workoutType = WorkoutType.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.WORKOUT_TYPE))];

                result = new OutdoorWorkoutsHistory(date, workoutType);

                result.setId(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable._ID)));
                result.setObjectiveDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_DISTANCE)));
                result.setObjectiveTimeMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_TIME)));
                result.setDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DISTANCE)));
                result.setDurationMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DURATION)));
                result.setMaximumSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MAXIMUM_SPEED)));
                result.setAverageSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.AVERAGE_SPEED)));
                result.setCompleted(cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.COMPLETED)) >= 1);
                result.setMeasuringUnit(App.LengthUnit.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MEASURING_UNIT))]);
                result.setStatus(WorkoutStatus.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.STATUS))]);
            }
            cursor.close();

            return result;
        } catch (Exception ex) {
            Log.e(TAG, "Cannot retrieve record", ex);
        }

        return null;
    }

    /**
     * Returns whether a training plan was ran by the user and not necessarily completed
     *
     * @param planId   The plan id
     * @param stage    the stage sequence number
     * @param training the training sequence number
     * @param context  The application context
     * @return whether a training plan was ran by the user and not necessarily completed
     */
    public boolean workoutExists(int planId, int stage, int training, @NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        long rows = DatabaseUtils.longForQuery(sqLiteDatabase, "SELECT COUNT(*) FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " AND "
                + OutdoorWorkoutsHistoryTable.TRAINING_ID + "=" + String.valueOf(training), null);

        return rows > 0;
    }

    /**
     * Obtains a list of completed trainings for a "plan" workout
     *
     * @param planId  The plan ID
     * @param stage   The stage sequence
     * @param context The application context
     * @return The list of completed trainings for the given plan and stage
     */
    public long getTrainingsCompleted(int planId, int stage, @NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        return DatabaseUtils.longForQuery(sqLiteDatabase, "SELECT COUNT(*) FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " AND "
                + OutdoorWorkoutsHistoryTable.COMPLETED + ">0", null);
    }

    public double getLastRanDistanceKm(int planId, int stage, int training, @NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        String query = "SELECT " + OutdoorWorkoutsHistoryTable.DISTANCE + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.TRAINING_ID + "=" + String.valueOf(training) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " ORDER BY "
                + OutdoorWorkoutsHistoryTable.DATE + " DESC LIMIT 1";

        Cursor c = sqLiteDatabase.rawQuery(query, null);
        if (c == null || c.getCount() == 0) return 0.0;
        c.moveToFirst();
        double result = c.getDouble(0);
        c.close();
        return result;
    }

    public long getLastRanElapsedTimeMilliSeconds(int planId, int stage, int training, @NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        String query = "SELECT " + OutdoorWorkoutsHistoryTable.DURATION + " FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE "
                + OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + "=" + WorkoutType.Plans.getValue() + " AND "
                + OutdoorWorkoutsHistoryTable.PLAN_ID + "=" + String.valueOf(planId) + " AND "
                + OutdoorWorkoutsHistoryTable.TRAINING_ID + "=" + String.valueOf(training) + " AND "
                + OutdoorWorkoutsHistoryTable.STAGE_ID + "=" + String.valueOf(stage) + " ORDER BY "
                + OutdoorWorkoutsHistoryTable.DATE + " DESC LIMIT 1";

        Cursor c = sqLiteDatabase.rawQuery(query, null);
        if (c == null || c.getCount() == 0) return 0;
        c.moveToFirst();
        long result = c.getLong(0);
        c.close();
        return result;
    }

    public List<OutdoorWorkoutsHistory> getIncomplete(@NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + OutdoorWorkoutsHistoryTable.TABLE_NAME + " WHERE " + OutdoorWorkoutsHistoryTable.STATUS + "=" + String.valueOf(WorkoutStatus.InProgress.getValue());
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        ArrayList<OutdoorWorkoutsHistory> result = new ArrayList<>();

        while (cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DATE)));
            WorkoutType workoutType = WorkoutType.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.WORKOUT_TYPE))];

            OutdoorWorkoutsHistory history = new OutdoorWorkoutsHistory(date, workoutType);

            history.setId(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable._ID)));
            history.setObjectiveDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_DISTANCE)));
            history.setObjectiveTimeMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.OBJECTIVE_TIME)));
            history.setDistanceKm(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DISTANCE)));
            history.setDurationMillis(cursor.getLong(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.DURATION)));
            history.setMaximumSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MAXIMUM_SPEED)));
            history.setAverageSpeedKmPerHour(cursor.getDouble(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.AVERAGE_SPEED)));
            history.setCompleted(cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.COMPLETED)) >= 1);
            history.setMeasuringUnit(App.LengthUnit.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.MEASURING_UNIT))]);
            history.setStatus(WorkoutStatus.values()[cursor.getInt(cursor.getColumnIndex(OutdoorWorkoutsHistoryTable.STATUS))]);

            Log.i(TAG, String.format("Incomplete workout: %s, Objective: %.3f km, Distance: %.3f km, Speed: %.1f km/h, Completed: %s, Unit: %s",
                    history.getDate(), history.getObjectiveDistanceKm(), history.getDistanceKm(), history.getAverageSpeedKmPerHour(), history.isCompleted() ? "yes" : "no", history.getMeasuringUnit().name()));
            result.add(history);
        }

        cursor.close();

        return result;
    }

}
