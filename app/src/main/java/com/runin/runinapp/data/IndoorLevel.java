package com.runin.runinapp.data;

import com.runin.runinapp.R;

/**
 * Created by Samuel Kobelkowsky on 7/24/17.
 * <p>
 * Different levels of Indoor Tracks
 */
public enum IndoorLevel {
    BEGINNER(0),
    COMPETITOR(1),
    ATHLETE(2),
    RUNNER(3);

    private final int value;

    IndoorLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getTitleId() {
        switch (this) {
            case BEGINNER:
                return R.string.beginner;
            case COMPETITOR:
                return R.string.competitor;
            case ATHLETE:
                return R.string.athlete;
            case RUNNER:
                return R.string.runner;
        }
        throw new IllegalStateException("Invalid value in enum");
    }

    public int getDescriptionId() {
        switch (this) {
            case BEGINNER:
                return R.string.beginner_level_description;
            case COMPETITOR:
                return R.string.competitor_level_description;
            case ATHLETE:
                return R.string.athlete_level_description;
            case RUNNER:
                return R.string.runner_level_description;
        }
        throw new IllegalStateException("Invalid value in enum");
    }


    public int getBackgroundResource() {
        switch (this) {
            case BEGINNER:
                return R.drawable.bg_level_beginner;
            case COMPETITOR:
                return R.drawable.bg_level_competitor;
            case ATHLETE:
                return R.drawable.bg_level_atlethe;
            case RUNNER:
                return R.drawable.bg_level_runner;
        }
        throw new IllegalStateException("Invalid value in enum");
    }
}