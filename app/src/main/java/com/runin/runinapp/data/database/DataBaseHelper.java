package com.runin.runinapp.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.runin.runinapp.data.OutdoorAppState;

/**
 * Database Helper. Creates the tables for the database.
 * Uses a Singleton model
 * Created by Cesar on 09/08/2016.
 *
 * Versions: 1  - Initial structure
 *           2  - Added status field to be able to recover a workout after a crash
 * */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database_runin";
    private static final int DATABASE_VERSION = 2;

    private static DataBaseHelper instance;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns the instance of the database helper
     *
     * @param context The class constructor
     * @return The instance of the database helper
     */
    public static DataBaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Creates the tables in the SQL database
     *
     * @param sqLiteDatabase the SQL database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the table of indoor completed tracks
        sqLiteDatabase.execSQL("CREATE TABLE " + RuninContract.CompletedIndoorTrackTable.TABLE_NAME + "("
                + RuninContract.CompletedIndoorTrackTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RuninContract.CompletedIndoorTrackTable.COMPLETED_DATE + " INTEGER NOT NULL,"
                + RuninContract.CompletedIndoorTrackTable.INDOOR_LEVEL + " INTEGER NOT NULL,"
                + RuninContract.CompletedIndoorTrackTable.SELECTED_PROGRAM + " INTEGER NOT NULL,"
                + RuninContract.CompletedIndoorTrackTable.SPEED_LEVEL + " INTEGER NOT NULL,"
                + RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN + " REAL NOT NULL,"
                + RuninContract.CompletedIndoorTrackTable.TRACK_ID + " TEXT NOT NULL)"
        );

        // Create the table of selected plans history
        sqLiteDatabase.execSQL("CREATE TABLE " + RuninContract.SelectedPlansHistoryTable.TABLE_NAME + "("
                + RuninContract.SelectedPlansHistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RuninContract.SelectedPlansHistoryTable.PLAN_NAME + " TEXT NOT NULL,"
                + RuninContract.SelectedPlansHistoryTable.LENGTH + " INTEGER NOT NULL,"
                + RuninContract.SelectedPlansHistoryTable.START_DATE + " INTEGER NOT NULL,"
                + RuninContract.SelectedPlansHistoryTable.DAYS_IN_WEEK + " INTEGER NOT NULL)"
        );

        // Create the table of outdoor workouts history
        sqLiteDatabase.execSQL("CREATE TABLE " + RuninContract.OutdoorWorkoutsHistoryTable.TABLE_NAME + "("
                + RuninContract.OutdoorWorkoutsHistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RuninContract.OutdoorWorkoutsHistoryTable.WORKOUT_TYPE + " INTEGER NOT NULL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.DATE + " INTEGER NOT NULL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.OBJECTIVE_DISTANCE + " REAL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.OBJECTIVE_TIME + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.MEASURING_UNIT + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.DISTANCE + " REAL NOT NULL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.DURATION + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.PLAN_ID + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.STAGE_ID + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.TRAINING_ID + " INTEGER,"
                + RuninContract.OutdoorWorkoutsHistoryTable.AVERAGE_SPEED + " REAL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.MAXIMUM_SPEED + " REAL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.STATUS + " INTEGER NOT NULL,"
                + RuninContract.OutdoorWorkoutsHistoryTable.COMPLETED + " INTEGER NOT NULL"
                + ")");

        // Create the table storing GPS points
        sqLiteDatabase.execSQL("CREATE TABLE " + RuninContract.GPSRunPointTable.TABLE_NAME + "("
                + RuninContract.GPSRunPointTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RuninContract.GPSRunPointTable.WORKOUT_ID + " INTEGER NOT NULL,"
                + RuninContract.GPSRunPointTable.TIME + " INTEGER NOT NULL,"
                + RuninContract.GPSRunPointTable.LATITUDE + " REAL NOT NULL,"
                + RuninContract.GPSRunPointTable.LONGITUDE + " REAL NOT NULL,"
                + RuninContract.GPSRunPointTable.SPEED + " REAL NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion, int newerVersion) {
        if (previousVersion < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE " + RuninContract.OutdoorWorkoutsHistoryTable.TABLE_NAME +
                    " ADD COLUMN " + RuninContract.OutdoorWorkoutsHistoryTable.STATUS + " INTEGER NOT NULL DEFAULT " + String.valueOf(OutdoorAppState.WorkoutStatus.Finished.getValue()));
        }

        // if (previousVersion < 3) {...}
    }
}
