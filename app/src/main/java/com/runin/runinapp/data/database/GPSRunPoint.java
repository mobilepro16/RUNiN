package com.runin.runinapp.data.database;

import android.content.ContentValues;
import android.location.Location;
import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * This object stores the spacial location and speed of a user. Suitable for database operations
 * Created by Samuel Kobelkowsky on 9/12/17.
 */
public class GPSRunPoint {
    private final double latitude;
    private final double longitude;
    private final double speed;
    private final long time;
    private final long workoutId;

    /**
     * Class constructor. Accepts a Location object from the Android GPS system
     *
     * @param workoutId The workout ID to identify this run
     * @param location  The GPS point
     */
    public GPSRunPoint(long workoutId, @NonNull Location location) {
        this.workoutId = workoutId;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.speed = location.getSpeed();
        this.time = location.getTime();
    }

    /**
     * Class constructor. Accepts parameters as single values
     *
     * @param latitude  The latitude
     * @param longitude The longitude
     * @param speed     The phone speed
     * @param time      The time of the measure
     * @param workoutId The workout ID to identify this run
     */
    GPSRunPoint(double latitude, double longitude, double speed, long time, long workoutId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.time = time;
        this.workoutId = workoutId;
    }

    /**
     * Calculates a distance from one point to another
     *
     * @param point The other point to measure the distance to
     * @return The distance in meters
     */
    public double distanceTo(@NonNull GPSRunPoint point) {
        float[] results = new float[5];

        Location.distanceBetween(latitude, longitude, point.latitude, point.longitude, results);
        return results[0];
    }

    /**
     * Returns the point latitude
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the point longitude
     *
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the time at measurement
     *
     * @return The time at measurement
     */
    public Date getTime() {
        return new Date(time);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "lat/lng/speed/time: (%.6f, %.7f, %.1f, %s)", latitude, longitude, speed, new Date(time));
    }

    /**
     * Creates a hash code for comparision purposes
     *
     * @return The hash code for comparision purposes
     */
    public final int hashCode() {
        return Objects.hash(latitude, longitude, speed, time);
    }

    /**
     * Determines equality to another object. That is, all properties have same values
     *
     * @param o The object to compare to
     * @return Wether the object has properties with same values as this
     */
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        else if (!(o instanceof GPSRunPoint)) {
            return false;
        }
        else {
            GPSRunPoint GPSRunPoint = (GPSRunPoint) o;

            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(GPSRunPoint.latitude) &&
                    Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(GPSRunPoint.longitude) &&
                    Double.doubleToLongBits(this.speed) == Double.doubleToLongBits(GPSRunPoint.speed) &&
                    this.workoutId == GPSRunPoint.workoutId &&
                    time == GPSRunPoint.time;
        }
    }

    /**
     * Obtain content values suitable for database insertion
     *
     * @return ContentValues object for database insertion
     */
    @NonNull
    ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(RuninContract.GPSRunPointTable.WORKOUT_ID, workoutId);
        contentValues.put(RuninContract.GPSRunPointTable.TIME, time);
        contentValues.put(RuninContract.GPSRunPointTable.LATITUDE, latitude);
        contentValues.put(RuninContract.GPSRunPointTable.LONGITUDE, longitude);
        contentValues.put(RuninContract.GPSRunPointTable.SPEED, speed);

        return contentValues;
    }
}
