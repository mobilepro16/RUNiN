package com.runin.runinapp.data.database;

import android.provider.BaseColumns;

/**
 * Table definitions of the database
 * Created by Cesar on 09/08/2016.
 */
class RuninContract {
    /**
     * The table for completed indoor tracks
     */
    static abstract class CompletedIndoorTrackTable implements BaseColumns {
        static final String TABLE_NAME = "completed_track";
        static final String TRACK_ID = "track_id";
        static final String COMPLETED_DATE = "completed_date";
        static final String SPEED_LEVEL = "speed_level";
        static final String INDOOR_LEVEL = "indoor_level";
        static final String SELECTED_PROGRAM = "selected_program";
        static final String DISTANCE_RAN = "distance_ran";
    }

    /**
     * Table for previously selected plans
     */
    static abstract class SelectedPlansHistoryTable implements BaseColumns {
        static final String TABLE_NAME = "selected_plans_history";
        static final String PLAN_NAME = "plan_name";
        static final String DAYS_IN_WEEK = "days_in_week";
        static final String START_DATE = "start_date";
        static final String LENGTH = "length";
    }

    /**
     * Table for the outdoor workouts finished
     */
    static abstract class OutdoorWorkoutsHistoryTable implements BaseColumns {
        static final String TABLE_NAME = "outdoor_workouts_history";
        static final String WORKOUT_TYPE = "workout_type";
        static final String DATE = "date";
        static final String OBJECTIVE_DISTANCE = "objective_distance";
        static final String OBJECTIVE_TIME = "objective_time";
        static final String MEASURING_UNIT = "mesuring_unit";
        static final String DISTANCE = "distance";
        static final String DURATION = "duration";
        static final String PLAN_ID = "plan_id";
        static final String STAGE_ID = "stage_id";
        static final String TRAINING_ID = "training_id";
        static final String AVERAGE_SPEED = "average_speed";
        static final String MAXIMUM_SPEED = "maximum_speed";
        static final String COMPLETED = "completed";
        static final String STATUS = "status";
    }

    /**
     * Table for the gps points
     */
    static abstract class GPSRunPointTable implements BaseColumns {
        static final String TABLE_NAME = "gps_point";
        static final String TIME = "date";
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String SPEED = "speed";
        static final String WORKOUT_ID = "workout_id";
    }
}
