package com.runin.runinapp.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a Stage of an Outdoor Plan
 * Created by Samuel Kobelkowsky on 11/23/17.
 */

public class Stage {
    /**
     * The list of trainings that this stage contains
     */
    @NonNull
    private final List<Training> trainings;

    /**
     * The sequence of this stage within the training
     */
    private final int sequence;

    /**
     * The description of this stage
     */
    @NonNull
    private final String description;

    /**
     * The title of this stage
     */
    @NonNull
    private final String title;

    /**
     * The SKU for purchasing the stage on the store
     */
    private final String sku;

    /**
     * Tells if this stage was purchased or not
     */
    private boolean purchased = false;

    /**
     * Class constructor
     *
     * @param title       The title of the stage
     * @param sequence    The sequence of the stage
     * @param description The description of the stage
     * @param sku         The SKU in store for the stage
     */
    Stage(@NonNull String title, int sequence, @NonNull String description, String sku) {
        this.title = title;
        this.sequence = sequence;
        this.description = description;
        this.sku = sku;

        trainings = new ArrayList<>();
    }

    /**
     * Returns the list of trainings that this stage contains
     *
     * @return the list of trainings that this stage contains
     */
    @NonNull
    public List<Training> getTrainings() {
        return trainings;
    }

    /**
     *
     * @param sequence The sequence of the wanted training within the Stage
     * @return The wanted training with the given sequence
     */
    @NonNull
    public Training getTraining(int sequence) {
        for (Training training : trainings) {
            if (training.getSequence() == sequence) {
                return training;
            }
        }

        throw new IllegalArgumentException("Invalid sequence");
    }

    /**
     * Tells if this stage was purchased
     *
     * @return if this stage was purchased
     */
    public boolean isPurchased() {
        return purchased;
    }

    /**
     * Sets whether this stage was purchase
     *
     * @param purchased whether this stage was purchased
     */
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    /**
     * Returns the sequence of this stage within the training
     *
     * @return the sequence of this stage within the training
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Gets the description of this stage
     *
     * @return description of this stage
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns the SKU in store for the stage
     *
     * @return SKU in store for the stage
     */
    public String getSku() {
        return sku;
    }

    /**
     * Returns the title of this stage
     *
     * @return title of this stage
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    public enum FinishedStatus {
        NotStarted, Finished, NotFinished
    }
}
