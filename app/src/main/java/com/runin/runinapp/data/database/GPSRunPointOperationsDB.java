package com.runin.runinapp.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database operations for GPS points
 * Created by Samuel Kobelkowsky on 11/30/17.
 */
public class GPSRunPointOperationsDB {
    //private static final String TAG = GPSRunPointOperationsDB.class.getSimpleName();

    /**
     * Adds a point to the database
     *
     * @param point   Point to add to the database
     * @param context Application context
     */
    public void addPoint(@NonNull GPSRunPoint point, @NonNull Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.insert(RuninContract.GPSRunPointTable.TABLE_NAME, null, point.getContentValues());
    }

    /**
     * Returns all the points for a given workout
     *
     * @param workoutId The workout to obtain points for
     * @param context   The application context
     * @return The list of points
     */
    @NonNull
    public List<GPSRunPoint> getPoints(long workoutId, @NonNull Context context) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + RuninContract.GPSRunPointTable.TABLE_NAME + " WHERE " + RuninContract.GPSRunPointTable.WORKOUT_ID + "="
                + String.valueOf(workoutId) + " ORDER BY " + RuninContract.GPSRunPointTable.TIME;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        ArrayList<GPSRunPoint> result = new ArrayList<>();

        while (cursor.moveToNext()) {
            final double latitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LATITUDE));
            final double longitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LONGITUDE));
            final double speed = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.SPEED));
            final long time = cursor.getLong(cursor.getColumnIndex(RuninContract.GPSRunPointTable.TIME));

            GPSRunPoint point = new GPSRunPoint(latitude, longitude, speed, time, workoutId);

            result.add(point);
        }

        cursor.close();

        return result;
    }

    /**
     * Calculates the pace in min/km of the GPS points for the given workout for the given time
     *
     * @param workoutId    The workout ID
     * @param context      The application context
     * @param milliseconds The time over the window is calculated
     * @return The pace in min/km
     */
    public double getRunningWindowPaceOverTime(long workoutId, @NonNull Context context, long milliseconds) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + RuninContract.GPSRunPointTable.TABLE_NAME + " WHERE " + RuninContract.GPSRunPointTable.WORKOUT_ID + "="
                + String.valueOf(workoutId) + " AND " +
                RuninContract.GPSRunPointTable.TIME + ">=" + String.valueOf(new Date().getTime() - milliseconds) + " ORDER BY " + RuninContract.GPSRunPointTable.TIME;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        double distance = 0.0;
        long initialTime = 0;
        long finalTime = 0;
        GPSRunPoint lastPoint = null;
        GPSRunPoint newPoint;

        while (cursor.moveToNext()) {
            final double latitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LATITUDE));
            final double longitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LONGITUDE));
            final double speed = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.SPEED));
            final long time = cursor.getLong(cursor.getColumnIndex(RuninContract.GPSRunPointTable.TIME));

            newPoint = new GPSRunPoint(latitude, longitude, speed, time, workoutId);

            // Initial conditions
            if (lastPoint == null) lastPoint = newPoint;
            if (initialTime == 0) initialTime = newPoint.getTime().getTime();

            finalTime = newPoint.getTime().getTime();

            distance += newPoint.distanceTo(lastPoint);

            lastPoint = newPoint;
        }

        cursor.close();

        if (distance == 0.0 || finalTime == initialTime) return 0.0;

        return (double) (finalTime - initialTime) / distance / 60.0;
    }

    /**
     * Calculates the pace in min/km of the GPS points for the given workout for the given distance
     *
     * @param workoutId    The workout ID
     * @param context      The application context
     * @param meters  The distance over the window is calculated
     * @return The pace in min/km
     */
    public double getRunningWindowPaceOverDistance(long workoutId, @NonNull Context context, float meters ) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + RuninContract.GPSRunPointTable.TABLE_NAME + " WHERE " + RuninContract.GPSRunPointTable.WORKOUT_ID + "="
                + String.valueOf(workoutId) + " ORDER BY " + RuninContract.GPSRunPointTable.TIME + " DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        double distance = 0.0;
        long initialTime = 0;
        long finalTime = 0;
        GPSRunPoint lastPoint = null;
        GPSRunPoint newPoint;

        while (cursor.moveToNext() && distance < meters) {
            final double latitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LATITUDE));
            final double longitude = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.LONGITUDE));
            final double speed = cursor.getDouble(cursor.getColumnIndex(RuninContract.GPSRunPointTable.SPEED));
            final long time = cursor.getLong(cursor.getColumnIndex(RuninContract.GPSRunPointTable.TIME));

            newPoint = new GPSRunPoint(latitude, longitude, speed, time, workoutId);

            // Initial conditions
            if (lastPoint == null) lastPoint = newPoint;
            if (initialTime == 0) initialTime = newPoint.getTime().getTime();

            finalTime = newPoint.getTime().getTime();

            distance += newPoint.distanceTo(lastPoint);

            lastPoint = newPoint;
        }

        cursor.close();

        if (distance == 0.0 || finalTime == initialTime) return 0.0;

        return (double) (initialTime - finalTime) / distance / 60.0;
    }
}
