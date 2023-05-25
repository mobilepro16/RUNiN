package com.runin.runinapp.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.util.Log;

import com.runin.runinapp.data.IndoorLevel;
import com.runin.runinapp.data.SpeedLevel;
import com.runin.runinapp.utils.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Operations for the Database of Completed tracks
 * Created by Samuel Kobelkowsky on 11/15/17.
 */
public class CompletedIndoorTrackOperationsDB {
    private static final String TAG = CompletedIndoorTrackOperationsDB.class.getSimpleName();

    /**
     * Add a new track to the completed tracks database
     *
     * @param track The completed track
     * @param ctx   The app context
     */
    public void add(@NonNull CompletedIndoorTrack track, @NonNull Context ctx) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(ctx);
        SQLiteDatabase sqLiteDatabase = myDataBase.getWritableDatabase();
        sqLiteDatabase.insert(RuninContract.CompletedIndoorTrackTable.TABLE_NAME, null, track.getCompletedTrackContentValues());
    }

    /**
     * Return a list of completed tracks found in the database
     *
     * @param ctx The application context
     * @return The list of completed tracks found in the database
     */
    @NonNull
    public List<CompletedIndoorTrack> getCompletedTracks(@NonNull Context ctx) {
        ArrayList<CompletedIndoorTrack> completedIndoorTracks = new ArrayList<>();

        DataBaseHelper myDataBase = DataBaseHelper.getInstance(ctx);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + RuninContract.CompletedIndoorTrackTable.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            final String track_id = cursor.getString(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.TRACK_ID));
            final Date completed_date = new Date(cursor.getLong(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.COMPLETED_DATE)));
            final IndoorLevel indoor_level = IndoorLevel.values()[cursor.getInt(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.INDOOR_LEVEL))];
            final int selected_program = cursor.getInt(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.SELECTED_PROGRAM));
            final SpeedLevel speed_level = SpeedLevel.values()[cursor.getInt(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.SPEED_LEVEL))];
            final double distanceRan = cursor.getDouble(cursor.getColumnIndex(RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN));

            CompletedIndoorTrack finishedTrack = new CompletedIndoorTrack(track_id, completed_date, indoor_level, selected_program, speed_level, distanceRan);
            completedIndoorTracks.add(finishedTrack);
            Log.i(TAG, String.format("Found finished track %s %s %d %d %d", track_id, completed_date, indoor_level.getValue(), selected_program, speed_level.getValue()));
        }

        cursor.close();

        return completedIndoorTracks;
    }

    public double getDistanceToday(@NonNull Context ctx) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(ctx);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        double distance = 0.0;

        try {
            Date today = DateUtils.dateOnly(new Date());

            String selectQuery = "SELECT SUM(" + RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN + ")" +
                    " FROM " + RuninContract.CompletedIndoorTrackTable.TABLE_NAME +
                    " WHERE " + RuninContract.CompletedIndoorTrackTable.COMPLETED_DATE + " >= " + today.getTime();

            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                distance = cursor.getDouble(0);
            }

            cursor.close();
        } catch (ParseException exception) {
            Log.e(TAG, "Invalid date format", exception);
        }

        return distance;
    }

    public double getDistanceTotal(@NonNull Context ctx) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(ctx);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        double distance = 0.0;

        String selectQuery = "SELECT SUM(" + RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN + ")" +
                " FROM " + RuninContract.CompletedIndoorTrackTable.TABLE_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            distance += cursor.getDouble(0);
        }

        cursor.close();

        return distance;
    }

    public double getDistanceLastTrainig(@NonNull Context ctx) {
        DataBaseHelper myDataBase = DataBaseHelper.getInstance(ctx);
        SQLiteDatabase sqLiteDatabase = myDataBase.getReadableDatabase();

        String selectQuery = "SELECT  SUM(" + RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN + ")" +
                " FROM " + RuninContract.CompletedIndoorTrackTable.TABLE_NAME +
                " ORDER BY " + RuninContract.CompletedIndoorTrackTable.COMPLETED_DATE + " DESC" +
                " LIMIT 1";

        double distance = 0.0;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            distance = cursor.getDouble(0);
        }

        cursor.close();

        return distance;
    }
}
