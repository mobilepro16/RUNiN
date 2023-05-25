package com.runin.runinapp.data.database;

import android.content.ContentValues;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.runin.runinapp.App.LengthUnit;
import com.runin.runinapp.data.OutdoorAppState.WorkoutStatus;
import com.runin.runinapp.data.OutdoorAppState.WorkoutType;

import java.util.Date;

/**
 * This class describes the historic records for Outdoor
 * Created by Samuel Kobelkowsky on 11/24/17.
 */
@SuppressWarnings("unused") // TODO: Check why is this necessary
public class OutdoorWorkoutsHistory {
    /**
     * The workout ID from database
     */
    private long id;

    /**
     * The date when the user ran
     */
    @NonNull
    private Date date;

    /**
     * The distance ran by the user
     */
    private double distanceKm;

    /**
     * The distance the user wanted to run
     */
    private double objectiveDistanceKm;

    /**
     * The average speed the user ran
     */
    private double averageSpeedKmPerHour;

    /**
     * The unit the user wants the data to be displayed
     */
    private LengthUnit measuringUnit;

    /**
     * The workout type: plan, quickstart or improve yourself
     */
    @NonNull
    private WorkoutType workoutType;

    /**
     * The plan id ran by the user
     */
    private int planId;

    /**
     * The stage sequence ran by the user
     */
    private int stageId;

    /**
     * The status of the workout: started, in progress, finised
     */
    private WorkoutStatus status;

    /**
     * The training sequence ran by the user
     */
    private int trainingId;

    /**
     * Whether the ran was completed
     */
    private boolean completed;

    /**
     * The time the user wants to finish the run in
     */
    private long objectiveTimeMillis;

    /**
     * The time the user ran
     */
    private long durationMillis;

    /**
     * The maximum speed the user achieved
     */
    private double maximumSpeedKmPerHour;

    /**
     * Class constructor
     *
     * @param date        The date of the workout
     * @param workoutType The workout type
     */
    public OutdoorWorkoutsHistory(@NonNull Date date, @NonNull WorkoutType workoutType) {
        this.date = date;
        this.workoutType = workoutType;
    }

    /**
     * Obtains a ContentValues object suitable for database insertion
     *
     * @return a ContentValues object
     */
    @NonNull
    public ContentValues getAddContentValues() {
        ContentValues values = new ContentValues();

        values.put(RuninContract.OutdoorWorkoutsHistoryTable.WORKOUT_TYPE, workoutType.getValue());
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.DATE, date.getTime());
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.OBJECTIVE_DISTANCE, objectiveDistanceKm);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.OBJECTIVE_TIME, objectiveTimeMillis);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.MEASURING_UNIT, measuringUnit != null ? measuringUnit.getValue() : null);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.DISTANCE, distanceKm);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.DURATION, durationMillis);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.PLAN_ID, planId);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.STAGE_ID, stageId);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.TRAINING_ID, trainingId);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.AVERAGE_SPEED, averageSpeedKmPerHour);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.COMPLETED, completed);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.STATUS, status.getValue());

        return values;
    }

    /**
     * Obtains a ContentValues object suitable for database update
     *
     * @return a ContentValues object
     */
    @NonNull
    public ContentValues getUpdateContentValues() {
        ContentValues values = new ContentValues();

        values.put(RuninContract.OutdoorWorkoutsHistoryTable.DISTANCE, distanceKm);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.DURATION, durationMillis);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.COMPLETED, completed);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.AVERAGE_SPEED, averageSpeedKmPerHour);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.MAXIMUM_SPEED, maximumSpeedKmPerHour);
        values.put(RuninContract.OutdoorWorkoutsHistoryTable.STATUS, status.getValue());

        return values;
    }

    /**
     * Returns the date the workout was performed
     *
     * @return The date the workout was performed
     */
    @NonNull
    public Date getDate() {
        return date;
    }

    /**
     * Returns the distance ran
     *
     * @return The distance ran
     */
    public double getDistanceKm() {
        return distanceKm;
    }

    /**
     * Sets the distance ran by the user
     *
     * @param distanceKm The distance ran by the user
     */
    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    /**
     * Returns the distance the user wanted to run
     *
     * @return The distance the user wanted to run
     */
    public double getObjectiveDistanceKm() {
        return objectiveDistanceKm;
    }

    /**
     * Sets the distance the user wanted to run
     *
     * @param objectiveDistanceKm The distance the user wanted to run
     */
    public void setObjectiveDistanceKm(double objectiveDistanceKm) {
        this.objectiveDistanceKm = objectiveDistanceKm;
    }

    /**
     * Returns the average speed during the workout
     *
     * @return The average speed during the workout
     */
    public double getAverageSpeedKmPerHour() {
        return averageSpeedKmPerHour;
    }

    /**
     * Sets the average speed the user ran
     *
     * @param averageSpeedKmPerHour The speed the user ran
     */
    public void setAverageSpeedKmPerHour(double averageSpeedKmPerHour) {
        this.averageSpeedKmPerHour = averageSpeedKmPerHour;
    }

    /**
     * Returns the measuring unit: meters or km
     *
     * @return the measuring unit: meters or km
     */
    public LengthUnit getMeasuringUnit() {
        return measuringUnit;
    }

    /**
     * The measuring unit the user wants to display distance
     *
     * @param measuringUnit The measuring unit the user wants to display distance. It is nullable since it's only used in ImproveYourself.
     */
    public void setMeasuringUnit(@Nullable LengthUnit measuringUnit) {
        this.measuringUnit = measuringUnit;
    }

    /**
     * Returns the type of workout
     *
     * @return The type of workout
     */
    @NonNull
    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    /**
     * Returns the plan ID
     *
     * @return The plan ID
     */
    public int getPlanId() {
        return planId;
    }

    /**
     * Sets the plan id ran by the user
     *
     * @param planId the plan id ran by the user
     */
    public void setPlanId(int planId) {
        this.planId = planId;
    }

    /**
     * Returns the stage ID
     *
     * @return The stage ID
     */
    public int getStageId() {
        return stageId;
    }

    /**
     * Sets the stage sequence ran by the user
     *
     * @param stageId The stage sequence ran by the user
     */
    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    /**
     * Returns the status of the workout
     *
     * @return status
     */
    @NonNull
    public WorkoutStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the workout
     *
     * @param status The status of the workout
     */
    public void setStatus(WorkoutStatus status) {
        this.status = status;
    }

    /**
     * Returns the training ID
     *
     * @return The training ID
     */
    public int getTrainingId() {
        return trainingId;
    }

    /**
     * Sets the training sequence ran by the user
     *
     * @param trainingId the training sequence ran by the user
     */
    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    /**
     * Returns whether the workout is completed
     *
     * @return Whether the workout is completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets whether the ran was completed
     *
     * @param completed whether the ran was completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Returns the database ID
     *
     * @return The database ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the database ID
     *
     * @param id The database ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the intended duration of the workout in milliseconds
     *
     * @return the intended duration of the workout in milliseconds
     */
    public long getObjectiveTimeMillis() {
        return objectiveTimeMillis;
    }

    /**
     * Sets the intended duration of the workout in milliseconds
     *
     * @param objectiveTimeMillis the intended duration of the workout in milliseconds
     */
    public void setObjectiveTimeMillis(long objectiveTimeMillis) {
        this.objectiveTimeMillis = objectiveTimeMillis;
    }

    /**
     * Gets the maximum speed of the user during the training
     *
     * @return the maximum speed of the user during the training
     */
    public double getMaximumSpeedKmPerHour() {
        return maximumSpeedKmPerHour;
    }

    /**
     * Sets the maximum speed of the user during the training
     *
     * @param maximumSpeedKmPerHour the maximum speed of the user during the training
     */
    public void setMaximumSpeedKmPerHour(double maximumSpeedKmPerHour) {
        this.maximumSpeedKmPerHour = maximumSpeedKmPerHour;
    }

    /**
     * Returns the time the user ran
     *
     * @return long The time the user ran
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Sets the time the user ran
     *
     * @param durationMillis The time the user ran
     */
    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }
}
