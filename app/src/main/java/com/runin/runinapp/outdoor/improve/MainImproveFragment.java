package com.runin.runinapp.outdoor.improve;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.App.LengthUnit;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.OutdoorImproveSwipeAdapter;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.User;
import com.runin.runinapp.data.database.GPSRunPoint;
import com.runin.runinapp.data.database.GPSRunPointOperationsDB;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.outdoor.OutdoorResultActivity;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 08/06/2017.
 */
public class MainImproveFragment extends Fragment {
    private static final String TAG = MainImproveFragment.class.getSimpleName();

    @BindView(R.id.buttonEnd)
    Button buttonEnd;

    @BindView(R.id.Tabla)
    TableLayout tabla;

    @BindView(R.id.user_txt)
    TextView txt_user;

    @BindView(R.id.sincarrera)
    TextView noRun;

    @BindView(R.id.details_improve)
    ViewPager viewPager;

    private Double objectiveDistanceKm = 0.0;
    private String measure = "";
    private long objectiveDurationMillis = 0;
    private String focus = "";
    private double recommendedPace = 0.0;

    @Inject
    User userProfile;
    private List<OutdoorWorkoutsHistory> improveHistory;
    private OutdoorAppState outdoorAppState;

    public MainImproveFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            outdoorAppState = ((App) getActivity().getApplication()).getOutdoorAppState();
        } else {
            throw new IllegalStateException("cannot get application");
        }

        if (getArguments() != null) {
            objectiveDistanceKm = getArguments().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);
            measure = getArguments().getString(App.extrasImproveYourselfMeasure);
            objectiveDurationMillis = getArguments().getLong(App.extrasImproveYourselfObjectiveTimeMillis);
            focus = getArguments().getString(App.extrasImproveYourselfFocus);
            recommendedPace = getArguments().getDouble(App.extrasImproveYourselfObjectivePace);

            Log.i(TAG, String.format("Distance: %.1f km, Measure: %s, Duration: %d, Focus: %s, Pace: %.1f", objectiveDistanceKm, measure, objectiveDurationMillis, focus, recommendedPace));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null) return null;

        View view = inflater.inflate(R.layout.fragment_improve_main, container, false);
        ButterKnife.bind(this, view);

        buttonEnd.setTypeface(Utils.getFontBebasNeue(getContext()), Typeface.ITALIC);

        OutdoorImproveSwipeAdapter swipeAdapter = new OutdoorImproveSwipeAdapter(getContext());
        viewPager.setAdapter(swipeAdapter);

        txt_user.setText(userProfile.getName());

        double selectedDistanceKm;
        double ranDistanceKm;
        long ranDuration;
        long objectiveDuration;

        final OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        if (getContext() == null) return view;

        improveHistory = outdoorWorkoutsHistoryOperationsDB.getImproveYourselfHistory(this.getContext());

        int i = 0;
        for (OutdoorWorkoutsHistory improveHistoryData : improveHistory) {

            selectedDistanceKm = improveHistoryData.getObjectiveDistanceKm();
            ranDistanceKm = improveHistoryData.getDistanceKm();

            ranDuration = improveHistoryData.getDurationMillis();
            objectiveDuration = improveHistoryData.getObjectiveTimeMillis();

            double averagePaceMinutesPerKm = 60.0 / improveHistoryData.getAverageSpeedKmPerHour();
            double improveYourselfObjectivePace = (double) objectiveDuration / selectedDistanceKm / 60000.0;

            boolean finished = ranDistanceKm > selectedDistanceKm * App.minimumPacePercentageForCompleteTraining && Math.abs(averagePaceMinutesPerKm - improveYourselfObjectivePace) / improveYourselfObjectivePace <= App.maximumPacePercentageForEquality;
            TableRow tableRow = (TableRow) View.inflate(getContext(), R.layout.row_history, null);
            tableRow.setBackgroundColor(Utils.getColor(getContext(), finished ? R.color.colorImproveOk : R.color.colorCal));
            tableRow.setId(i);
            tableRow.setClickable(true);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int row = view.getId();

                    Log.i(TAG, String.format("Clicked on row %d", row));

                    OutdoorWorkoutsHistory workout = improveHistory.get(row);
                    outdoorAppState.setWorkout(workout);

                    GPSRunPointOperationsDB gpsRunPointOperationsDB = new GPSRunPointOperationsDB();
                    List<GPSRunPoint> points = getContext() != null ? gpsRunPointOperationsDB.getPoints(workout.getId(), getContext()) : new ArrayList<GPSRunPoint>();
                    double distanceMeters = 0.0;
                    long timeMilliseconds = 0;
                    GPSRunPoint lastPoint = null;
                    for (GPSRunPoint point : points) {
                        if (lastPoint != null) {
                            distanceMeters += point.distanceTo(lastPoint);
                            timeMilliseconds += point.getTime().getTime() - lastPoint.getTime().getTime();
                        }
                        lastPoint = point;
                    }

                    Double objectivePace = workout.getObjectiveTimeMillis() / workout.getObjectiveDistanceKm() / 60000.0;
                    Double averagePace = distanceMeters == 0.0 ? 0 : timeMilliseconds / distanceMeters / 60.0;

                    Intent intent = new Intent(MainImproveFragment.this.getActivity(), OutdoorResultActivity.class);

                    intent.putExtra(App.extrasShowFinishButton, false);
                    intent.putExtra(App.extrasTotalDistanceKm, distanceMeters / 1000.0);
                    intent.putExtra(App.extrasSpeedKmPerHour, distanceMeters / timeMilliseconds * 3600.0);
                    intent.putExtra(App.extrasPaceMinPerKm, averagePace);
                    intent.putExtra(App.extrasMaxSpeedKmPerHour, workout.getMaximumSpeedKmPerHour());
                    intent.putExtra(App.extrasElapsedTimeMilliSeconds, timeMilliseconds);
                    intent.putExtra(App.extrasImproveYourselfFocus, App.focusDistance);
                    intent.putExtra(App.extrasImproveYourselfMeasure, workout.getMeasuringUnit() == LengthUnit.Meter ? App.measureMeters : App.measureKilometers);
                    intent.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, workout.getObjectiveTimeMillis());
                    intent.putExtra(App.extrasImproveYourselfObjectivePace, objectivePace);
                    intent.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, workout.getObjectiveDistanceKm());

                    startActivity(intent);
                }
            });

            tabla.addView(tableRow);

            TextView txtDistance = tableRow.findViewById(R.id.distance);
            TextView txtDuration = tableRow.findViewById(R.id.duration);

            txtDistance.setText(String.format(Locale.getDefault(), "%.0f / %.0f", selectedDistanceKm * 1000.0, ranDistanceKm * 1000.0));
            txtDuration.setText(String.format(Locale.getDefault(), "%s / %s", DateUtils.durationToMinSec(objectiveDuration), DateUtils.durationToMinSec(ranDuration)));

            i++;
        }

        if (improveHistory.size() == 0) {
            noRun.setVisibility(View.VISIBLE);
            tabla.setVisibility(View.GONE);
        } else {
            noRun.setVisibility(View.GONE);
            tabla.setVisibility(View.VISIBLE);
        }

        buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentQuickStart = new Intent(MainImproveFragment.this.getActivity(), PreRunImproveActivity.class);
                intentQuickStart.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
                intentQuickStart.putExtra(App.extrasImproveYourselfMeasure, measure);
                intentQuickStart.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, objectiveDurationMillis);
                intentQuickStart.putExtra(App.extrasImproveYourselfFocus, focus);
                intentQuickStart.putExtra(App.extrasImproveYourselfObjectivePace, recommendedPace);
                startActivity(intentQuickStart);
            }
        });
        return view;
    }
}