package com.runin.runinapp.data.database;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import com.runin.runinapp.data.IndoorLevel;
import com.runin.runinapp.data.SpeedLevel;

import java.util.Date;

/**
 * Class containing the Completed tracks that the user has run. Used for storing the data in SQL
 * Created by Samuel Kobelkowsky on 11/16/17.
 */
public class CompletedIndoorTrack {
    /**
     * When the user completed the track
     */
    @NonNull
    private final Date completedDate;

    /**
     * The indoor level selected by the user
     */
    @NonNull
    private final IndoorLevel indoorLevel;

    /**
     * The selected program of the track e.g. Power vs Speed
     */
    private final int selectedProgram;

    /**
     * The speed level the user selected e.g. Fast vs Normal
     */
    @NonNull
    private final SpeedLevel speedLevel;

    /**
     * The distance ran
     */
    private final double distanceRan;

    /**
     * The The track id
     */
    @NonNull
    private final String id;

    public CompletedIndoorTrack(@NonNull String id, @NonNull Date completedDate, @NonNull IndoorLevel indoorLevel, int selectedProgram, @NonNull SpeedLevel speedLevel, double distanceRan) {
        this.completedDate = completedDate;
        this.indoorLevel = indoorLevel;
        this.selectedProgram = selectedProgram;
        this.speedLevel = speedLevel;
        this.id = id;
        this.distanceRan = distanceRan;
    }

    /**
     * Gets the track id
     *
     * @return the track id
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Gets the completed date of the track
     *
     * @return the completed date
     */
    @NonNull
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * Gets the indoor level that the user selected
     *
     * @return the indoor level
     */
    @NonNull
    public IndoorLevel getIndoorLevel() {
        return indoorLevel;
    }

    /**
     * Gets the selected program for the track the user ran
     *
     * @return the selected program
     */
    public int getSelectedProgram() {
        return selectedProgram;
    }

    /**
     * Gets the speed level for the track the user ran
     *
     * @return The speed level
     */
    @NonNull
    public SpeedLevel getSpeedLevel() {
        return speedLevel;
    }

    /**
     * Gets the distance ran
     *
     * @return The distance ram
     */
    @SuppressWarnings("unused")
    public double getDistanceRan() { return distanceRan; }

    /**
     * Returns a ContentValues object suitable for the insertion of the track in the Completed Tracks database
     *
     * @return a ContentValues object with the completed track data
     */
    @NonNull
    ContentValues getCompletedTrackContentValues() {
        ContentValues values = new ContentValues();
        values.put(RuninContract.CompletedIndoorTrackTable.TRACK_ID, id);
        values.put(RuninContract.CompletedIndoorTrackTable.COMPLETED_DATE, completedDate.getTime());
        values.put(RuninContract.CompletedIndoorTrackTable.SPEED_LEVEL, speedLevel.getValue());
        values.put(RuninContract.CompletedIndoorTrackTable.INDOOR_LEVEL, indoorLevel.getValue());
        values.put(RuninContract.CompletedIndoorTrackTable.SELECTED_PROGRAM, selectedProgram);
        values.put(RuninContract.CompletedIndoorTrackTable.DISTANCE_RAN, distanceRan);

        return values;
    }
}
