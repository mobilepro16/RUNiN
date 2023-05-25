package com.runin.runinapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.util.Log;

import com.runin.runinapp.App;
import com.runin.runinapp.R;

import java.io.Serializable;

/**
 * Created by Samuel Kobelkowsky on 7/24/17.
 * <p>
 * Model for Badges
 */
public class Badge implements Serializable {
    private static final String TAG = Badge.class.getSimpleName();

    @NonNull
    private final Level level;

    private boolean completed;

    /**
     * Instantiates a new Badge.
     *
     * @param level     the badge level
     * @param completed the completed status
     */
    public Badge(@NonNull Level level, boolean completed) {
        this.level = level;
        this.completed = completed;
    }

    /**
     * Gets the badge level.
     *
     * @return the badge level
     */
    @NonNull
    public Level getLevel() {
        return level;
    }

    /**
     * Is completed status.
     *
     * @return the completed status
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Marks the badge as completed.
     */
    void setCompleted() {
        this.completed = true;
    }

    /**
     * Gets the Android Resource ID for the icon.
     *
     * @return the resource ID for the icon
     */
    public int getIcon() {
        switch (level) {
            case NEWBIE:
                return R.drawable.badge_icon_newbie_color;
            case BOOST:
                return R.drawable.badge_icon_boost_color;
            case ENDURANCE:
                return R.drawable.badge_icon_endurance_color;
            case FITNESS:
                return R.drawable.badge_icon_fitness_color;
            case MUSCLE:
                return R.drawable.badge_icon_muscle_color;
            case RESISTANCE:
                return R.drawable.badge_icon_resistance_color;
            case PACE:
                return R.drawable.badge_icon_rhythm_color;
            case SPEED:
                return R.drawable.badge_icon_speed_color;
            case STRENGTH:
                return R.drawable.badge_icon_strength_color;
            case POWER:
                return R.drawable.badge_icon_power_color;
        }

        Log.e(TAG, "Invalid Badge icon");
        return 0;
    }

    /**
     * Gets the Android Resource ID for the disabled icon.
     *
     * @return the resource ID for the disabled icon
     */
    public int getGrayIcon() {
        switch (level) {
            case NEWBIE:
                return R.drawable.badge_icon_newbie_gray;
            case BOOST:
                return R.drawable.badge_icon_boost_gray;
            case ENDURANCE:
                return R.drawable.badge_icon_endurance_gray;
            case FITNESS:
                return R.drawable.badge_icon_fitness_gray;
            case MUSCLE:
                return R.drawable.badge_icon_muscle_gray;
            case RESISTANCE:
                return R.drawable.badge_icon_resistance_gray;
            case PACE:
                return R.drawable.badge_icon_rhythm_gray;
            case SPEED:
                return R.drawable.badge_icon_speed_gray;
            case STRENGTH:
                return R.drawable.badge_icon_strength_gray;
            case POWER:
                return R.drawable.badge_icon_power_gray;
        }

        Log.e(TAG, "Invalid Badge icon");
        return 0;
    }

    /**
     * Gets the Android Resource ID for the rectangular icon.
     *
     * @return the resource ID for the rectangular icon
     */
    public int getRectangularIcon() {
        switch (level) {
            case NEWBIE:
                return R.drawable.badges_rectangle_newbie_color;
            case BOOST:
                return R.drawable.badges_rectangle_boost_color;
            case ENDURANCE:
                return R.drawable.badges_rectangle_endurance_color;
            case FITNESS:
                return R.drawable.badges_rectangle_fitness_color;
            case MUSCLE:
                return R.drawable.badges_rectangle_muscle_color;
            case RESISTANCE:
                return R.drawable.badges_rectangle_resistance_color;
            case PACE:
                return R.drawable.badges_rectangle_rhythm_color;
            case SPEED:
                return R.drawable.badges_rectangle_speed_color;
            case STRENGTH:
                return R.drawable.badges_rectangle_strength_color;
            case POWER:
                return R.drawable.badges_rectangle_power_color;
        }

        Log.e(TAG, "Invalid Badge icon");
        return 0;
    }

    /**
     * Gets the Android Resource ID for the rectangular icon.
     *
     * @return the resource ID for the rectangular icon
     */
    public int getRectangularGrayIcon() {
        switch (level) {
            case NEWBIE:
                return R.drawable.badges_rectangle_newbie_gray;
            case BOOST:
                return R.drawable.badges_rectangle_boost_gray;
            case ENDURANCE:
                return R.drawable.badges_rectangle_endurance_gray;
            case FITNESS:
                return R.drawable.badges_rectangle_fitness_gray;
            case MUSCLE:
                return R.drawable.badges_rectangle_muscle_gray;
            case RESISTANCE:
                return R.drawable.badges_rectangle_resistance_gray;
            case PACE:
                return R.drawable.badges_rectangle_rhythm_gray;
            case SPEED:
                return R.drawable.badges_rectangle_speed_gray;
            case STRENGTH:
                return R.drawable.badges_rectangle_strength_gray;
            case POWER:
                return R.drawable.badges_rectangle_power_gray;
        }

        Log.e(TAG, "Invalid Badge icon");
        return 0;
    }

    /**
     * Get the resource for the title of the badge.
     * @return The resource for the title of the badge
     */
    public int getTitle() {
        switch (level) {
            case NEWBIE:
                return R.string.newbie;
            case BOOST:
                return R.string.boost;
            case ENDURANCE:
                return R.string.endurance;
            case FITNESS:
                return R.string.fitness;
            case MUSCLE:
                return R.string.muscle;
            case RESISTANCE:
                return R.string.resistance;
            case PACE:
                return R.string.pace;
            case SPEED:
                return R.string.speed;
            case STRENGTH:
                return R.string.strength;
            case POWER:
                return R.string.power;
        }

        Log.e(TAG, "Invalid Badge name");
        return 0;
    }

    /**
     * Get the resource for the description of the badge
     * @return The resource for the description of the badge
     */
    public int getDescription() {
        switch (level) {
            case NEWBIE:
                return R.string.newbie_badge;
            case BOOST:
                return R.string.boost_badge;
            case ENDURANCE:
                return R.string.endurance_badge;
            case FITNESS:
                return R.string.fitness_badge;
            case MUSCLE:
                return R.string.muscle_badge;
            case RESISTANCE:
                return R.string.resistance_badge;
            case PACE:
                return R.string.rythim_badge;
            case SPEED:
                return R.string.speed_badge;
            case STRENGTH:
                return R.string.strenght_badge;
            case POWER:
                return R.string.power_badge;
        }

        Log.e(TAG, "Invalid Badge name");
        return 0;
    }

    /**
     * Saves the achieved status for the badge in persistent memory
     * @param context The application context
     */
    void saveStatus(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(App.sharedPreferencesBadgeStatus + getTitle(), completed);
        editor.apply();
    }

    /**
     * Sets the 'completed' property of the Badge from persistent memory. If not found, use a default of false.
     * @param context The application context
     */
    void getStatus(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        completed = sharedPref.getBoolean(App.sharedPreferencesBadgeStatus + getTitle(), false);
    }

    /**
     * The badge type
     */
    public enum Level {
        NEWBIE,
        BOOST,
        ENDURANCE,
        FITNESS,
        MUSCLE,
        RESISTANCE,
        PACE,
        SPEED,
        STRENGTH,
        POWER
    }
}