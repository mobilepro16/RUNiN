package com.runin.runinapp.data;

import androidx.annotation.NonNull;

import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that describes the training of a Plan Stage
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class Training {
    /**
     * The sequence of this training within the stage
     */
    private final int sequence;

    /**
     * Tells if this training is of the "trial" kind
     */
    private final boolean isTest;
    /**
     * The description of the training
     */
    @NonNull
    private final String description;
    /**
     * The phases that this training contains
     */
    @NonNull
    private List<Phase> phases;
    /**
     * Tells if this training was previously selected by the user. Not necessarily was completed.
     */
    private boolean previouslySelected;

    /**
     * Tells if this training was previously completed by the user
     */
    private OutdoorWorkoutsHistory previouslyCompleted;

    /**
     * Class constructor
     *
     * @param sequence    Sequence of the training
     * @param isTest      If this training is a trial
     * @param description The description of the training
     */
    public Training(int sequence, boolean isTest, @NonNull String description) {
        this.sequence = sequence;
        this.isTest = isTest;
        this.description = description;
        previouslySelected = false;
        previouslyCompleted = null;

        phases = new ArrayList<>();
    }

    /**
     * Returns if this training is a trial
     *
     * @return If this training is a trial
     */
    public boolean isTest() {
        return isTest;
    }

    /**
     * Returns all the phases of the training
     *
     * @return the phases of the training
     */
    @NonNull
    public List<Phase> getPhases() {
        return phases;
    }

    /**
     * Sets the phases of the training
     *
     * @param phases the phases of the training
     */
    void setPhases(@NonNull List<Phase> phases) {
        this.phases = phases;
    }

    /**
     * returns te sequence of the training
     *
     * @return the sequence of the training
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Returns the distance (in meters) of this training
     *
     * @return The distance of the training
     */
    public double getDistanceMeters() {
        double distance = 0.0;
        for (Phase phase : phases) {
            distance += phase.getDistanceMeters();
        }

        return distance;
    }

    /**
     * Returns the duration (in milliseconds) of this training
     *
     * @return The duration of the training
     */
    public long getDuration() {
        long duration = 0;
        for (Phase phase : phases) {
            duration += phase.getDurationMillis();
        }

        return duration;
    }

    /**
     * Tells if this training was previously selected by the user
     *
     * @return if this training was previously selected
     */
    public boolean isPreviouslySelected() {
        return previouslySelected;
    }

    /**
     * Sets the indication that this training was previously selected by the user
     *
     * @param previouslySelected this training was previously selected by the user
     */
    public void setPreviouslySelected(boolean previouslySelected) {
        this.previouslySelected = previouslySelected;
    }

    /**
     * Tells is if this training was previously completed by the user
     *
     * @return If this training was previously completed by the user
     */
    public boolean isPreviouslyCompleted() {
        return previouslyCompleted != null;
    }

    /**
     * Returns the last workout id of the last time the training was finished.
     *
     * @return the last workout id of the last time the training was finished.
     */
    public OutdoorWorkoutsHistory getPreviouslyCompleted() {
        return previouslyCompleted;
    }

    /**
     * Sets that this training was previously completed by the user
     *
     * @param previouslyCompleted whether this training was previously completed by the user
     */
    public void setPreviouslyCompleted(OutdoorWorkoutsHistory previouslyCompleted) {
        this.previouslyCompleted = previouslyCompleted;
    }

    /**
     * Returns the training description
     *
     * @return The training description
     */
    @NonNull
    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }
}
