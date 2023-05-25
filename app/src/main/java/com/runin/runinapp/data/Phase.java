package com.runin.runinapp.data;

import androidx.annotation.NonNull;

import com.runin.runinapp.R;

/**
 * A Phase describes a part of a Training which belongs to a Stage which belongs to an Outdoor Plan.
 * It has a concrete objective, e.g.: walk 200 meters with a pace of 5 min/km
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class Phase {

    /**
     * A more detailed description of the exercise to do
     */
    @NonNull
    private final Instruction instruction;

    /**
     * The objective pace of the phase in min/km
     */
    private final double paceMinPerKm;

    /**
     * The objective distance to exercise
     */
    private final double distanceMeters;

    /**
     * The sequence of the phase inside the training
     */
    private final int sequence;

    /**
     * The type of exercise to do: walk, jog, etc.
     */
    @NonNull
    private final Kind kind;

    /**
     * Class constructor
     *
     * @param sequence       The sequence of the phase inside the training
     * @param kind           The type of exercise to do
     * @param instruction    A more detailed description of the exercise to do
     * @param paceMinPerKm   The objective pace
     * @param distanceMeters The objective distance
     */
    Phase(int sequence, @NonNull Kind kind, @NonNull Instruction instruction, double paceMinPerKm, double distanceMeters) {
        this.kind = kind;
        this.instruction = instruction;
        this.paceMinPerKm = paceMinPerKm;
        this.distanceMeters = distanceMeters;
        this.sequence = sequence;
    }

    /**
     * Returns the type of exercise to do.
     *
     * @return the type of exercise to do.
     */
    @NonNull
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns a resource string representing the kind of exercise to do
     *
     * @return a resource string representing the kind of exercise to do
     */
    public int getKindName() {
        switch (kind) {
            case WARM_UP:
                return R.string.warm_up;
            case TRIAL:
                return R.string.trial;
            case RECOVERY:
                return R.string.recovery;
            case TRAINING:
                return R.string.training;
            case RUN:
                return R.string.run;
            case COOL_DOWN:
                return R.string.cool_down;
            default:
                throw new IllegalArgumentException("No such training name");
        }
    }

    /**
     * A more detailed description of the exercise to do
     *
     * @return A more detailed description of the exercise to do
     */
    public int getInstructionName() {
        switch (instruction) {
            case JOG:
                return R.string.jog;
            case WALK:
                return R.string.walk;
            case FAST_JOG:
                return R.string.fast_jog;
            case RECOVERY:
                return R.string.recovery;
            case COOL_DOWN:
                return R.string.cool_down;
            case FAST_WALK:
                return R.string.fast_walk;
            case VERY_FAST:
                return R.string.very_fast;
            case FINAL_WALK:
                return R.string.final_walk;
            case INITIAL_WALK:
                return R.string.initial_walk;
            default:
                throw new IllegalArgumentException("No such training name");
        }
    }

    /**
     * Returns the objective pace to achieve
     *
     * @return the objective pace to achieve
     */
    public double getPaceMinPerKm() {
        return paceMinPerKm;
    }

    /**
     * Returns the objective distance
     *
     * @return the objective distance
     */
    double getDistanceMeters() {
        return distanceMeters;
    }

    /**
     * Returns the duration of the phase in milliseconds
     *
     * @return The duration of the phase in milliseconds
     */
    public long getDurationMillis() {
        return (long) (distanceMeters * paceMinPerKm * 60.0);
    }

    /**
     * Returns the sequence of this phase
     *
     * @return The sequence of this phase
     */
    @SuppressWarnings("unused")
    public int getSequence() {
        return sequence;
    }

    /**
     * An enumeration of the distinct kinds of phases
     */
    public enum Kind {
        WARM_UP,
        RECOVERY,
        TRAINING,
        COOL_DOWN,
        TRIAL,
        RUN
    }

    /**
     * An enumeration of the distinct instructions
     */
    public enum Instruction {
        INITIAL_WALK,
        WALK,
        FAST_WALK,
        FINAL_WALK,
        JOG,
        FAST_JOG,
        VERY_FAST,
        RECOVERY,
        COOL_DOWN
    }
}
