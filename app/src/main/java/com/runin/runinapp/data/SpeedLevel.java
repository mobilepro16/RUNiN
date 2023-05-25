package com.runin.runinapp.data;

import com.runin.runinapp.R;

public enum SpeedLevel {
    MODERATED(0),
    INTENSE(1);

    private final int value;

    SpeedLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int getTitleId() {
        switch (this) {
            case MODERATED:
                return R.string.moderated;
            case INTENSE:
                return R.string.intense;
        }
        throw new IllegalStateException("Invalid value in enum");
    }
}
