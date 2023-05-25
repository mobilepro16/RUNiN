package com.runin.runinapp.data;

import androidx.annotation.NonNull;

import com.runin.runinapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the different plans for Outdoor. Each plan has stages, each stage has trainings and each training has phases.
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class Plan {
    /**
     * The Plan identifier
     */
    private final int id;

    /**
     * The list of stages for this plan
     */
    @NonNull
    private final List<Stage> stages;

    /**
     * The "focus" of the plan. It can be Distance or Speed
     */
    @NonNull
    private final Focus focus;

    /**
     * The title of the plan. Note that we can have more than one plan with the same title
     */
    @NonNull
    private final String title;

    /**
     * The "length" of the plan. It can be Short or Long
     */
    @NonNull
    private final Length length;
    /**
     * The name of the plan.  Different from its title.
     */
    private final String name;
    /**
     * How many days per week the user will train
     */
    private int daysPerWeek;

    /**
     * Class Constructor
     *
     * @param id     the Plan identifier
     * @param title  The title of the plan. Notice that we can have more than one plan with the same title
     * @param length The length of the plan: Short or Long
     * @param focus  The focus of the plan: Distance or Speed
     */
    public Plan(int id, @NonNull String title, @NonNull Length length, @NonNull Focus focus) {
        this.id = id;
        this.title = title;
        this.length = length;
        this.focus = focus;

        stages = new ArrayList<>();

        switch (id) {
            case OutdoorAppState.PLAN_3K_ID:
                name = OutdoorAppState.PLAN_3K_TITLE;
                break;
            case OutdoorAppState.PLAN_5K_LONG_ID:
                name = OutdoorAppState.PLAN_5K_TITLE;
                break;
            case OutdoorAppState.PLAN_5K_SHORT_ID:
                name = OutdoorAppState.PLAN_5K_TITLE;
                break;
            case OutdoorAppState.PLAN_10K_LONG_ID:
                name = OutdoorAppState.PLAN_10K_TITLE;
                break;
            case OutdoorAppState.PLAN_10K_SHORT_ID:
                name = OutdoorAppState.PLAN_10K_TITLE;
                break;
            case OutdoorAppState.PLAN_5K_PLUS_LONG_ID:
                name = OutdoorAppState.PLAN_5K_PLUS_TITLE;
                break;
            case OutdoorAppState.PLAN_5K_PLUS_SHORT_ID:
                name = OutdoorAppState.PLAN_5K_PLUS_TITLE;
                break;
            case OutdoorAppState.PLAN_10K_PLUS_LONG_ID:
                name = OutdoorAppState.PLAN_10K_PLUS_TITLE;
                break;
            case OutdoorAppState.PLAN_10K_PLUS_SHORT_ID:
                name = OutdoorAppState.PLAN_10K_PLUS_TITLE;
                break;
            default:
                throw new IllegalArgumentException("Invalid plan id");
        }
    }

    /**
     * The list of stages
     *
     * @return The list of stages
     */
    @NonNull
    public List<Stage> getStages() {
        return stages;
    }

    /**
     * The plan title
     *
     * @return The plan title
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Returns the plan length
     *
     * @return The plan length
     */
    @NonNull
    public Length getLength() {
        return length;
    }

    /**
     * Returns the name of the plan.  Different from the title of the plan.
     *
     * @return The name of the plan
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Returns the plan focus
     *
     * @return The plan focus
     */
    @NonNull
    public Focus getFocus() {
        return focus;
    }

    /**
     * Returns how many days per week the user wants to train
     *
     * @return The days per week the user wants to train
     */
    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    /**
     * Sets the number of days per week the user wants to train
     *
     * @param daysPerWeek The number of days per week the user wants to train
     */
    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    /**
     * Returns the stage at a particular position
     *
     * @param sequence The position of the stage within the plan
     * @return The stage at the sequence position
     */
    public Stage getStage(int sequence) {
        for (Stage stage : stages) {
            if (stage.getSequence() == sequence) {
                return stage;
            }
        }
        throw new IllegalArgumentException("No such stage");
    }

    /**
     * Returns the string resource of the plan description
     *
     * @return the string resource of the plan description
     */
    public int getDescription() {
        switch (id) {
            case OutdoorAppState.PLAN_3K_ID:
                return R.string.plan_3k;
            case OutdoorAppState.PLAN_5K_LONG_ID:
                return R.string.plan_5k_largo;
            case OutdoorAppState.PLAN_5K_SHORT_ID:
                return R.string.plan_5k_corto;
            case OutdoorAppState.PLAN_10K_LONG_ID:
                return R.string.plan_10k_largo;
            case OutdoorAppState.PLAN_10K_SHORT_ID:
                return R.string.plan_10k_corto;
            case OutdoorAppState.PLAN_5K_PLUS_LONG_ID:
                return R.string.plan_5k_plus_largo;
            case OutdoorAppState.PLAN_5K_PLUS_SHORT_ID:
                return R.string.plan_5k_plus_corto;
            case OutdoorAppState.PLAN_10K_PLUS_LONG_ID:
                return R.string.plan_10k_plus_largo;
            case OutdoorAppState.PLAN_10K_PLUS_SHORT_ID:
                return R.string.plan_10k_plus_corto;
            default:
                throw new IllegalArgumentException("No such plan");
        }
    }

    /**
     * Returns the plan id
     *
     * @return the plan id
     */
    public int getId() {
        return id;
    }

    /**
     * An enum representing the Length of the plan: Short or Long
     */
    public enum Length {
        Short(0), Long(1);

        private final int value;

        Length(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * An enum representing the focus of the plan: Distance or Speed
     */
    public enum Focus {
        Distance, Speed
    }
}
