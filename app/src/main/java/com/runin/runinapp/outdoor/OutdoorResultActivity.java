package com.runin.runinapp.outdoor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.OutdoorAppState.WorkoutStatus;
import com.runin.runinapp.data.OutdoorAppState.WorkoutType;
import com.runin.runinapp.data.Phase;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.data.User;
import com.runin.runinapp.data.database.GPSRunPoint;
import com.runin.runinapp.data.database.GPSRunPointOperationsDB;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 */
public class OutdoorResultActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = OutdoorResultActivity.class.getSimpleName();

    @BindView(R.id.finishBtn)
    Button finishBtn;

    @BindView(R.id.title)
    TextView qs_title;

    @BindView(R.id.share)
    Button share;

    @BindView(R.id.km_qs)
    TextView kilometersQuickStart;

    @BindView(R.id.calories_qs)
    TextView caloriesQuickStart;

    @BindView(R.id.rate_qs)
    TextView paceQuickStart;

    @BindView(R.id.time_qs)
    TextView timeQuickStart;

    @BindView(R.id.speed_qs)
    TextView txtAverageSpeed;

    @BindView(R.id.max_speed_qs)
    TextView txtMaxSpeedQS;

    @BindView(R.id.txtDate)
    TextView txtDate;

    @BindView(R.id.txtObjectiveDistance)
    TextView txtObjectiveDistance;

    @BindView(R.id.txtObjectiveTime)
    TextView txtObjectiveTime;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.imgStatusGreen)
    ImageView imgStatusGreen;

    @BindView(R.id.imgStatusRed)
    ImageView imgStatusRed;

    @BindView(R.id.improveStatusText)
    TextView improveStatusText;

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @BindView(R.id.title_content)
    LinearLayout title_content;

    @BindView(R.id.values1)
    LinearLayout values1;

    @BindView(R.id.values2)
    LinearLayout values2;

    @BindView(R.id.contentQS)
    LinearLayout contentQS;

    @BindView(R.id.scroll)
    ScrollView scroll;

    @BindView(R.id.outdoor_mejorate)
    LinearLayout outdoorMejorate;

    @BindView(R.id.plans_result_status)
    LinearLayout plansResultStatus;

    @BindView(R.id.plans_title_and_status)
    TextView PlansTitleAndStatus;

    @BindView(R.id.plans_objective_vs_achieved)
    TextView PlansObjectiveVsAchieved;

    @BindView(R.id.plans_result_circle_green)
    ImageView PlansResultCircleGreen;

    @BindView(R.id.plans_result_circle_red)
    ImageView PlansResultCircleRed;

    @BindView(R.id.txtTotalDistance)
    TextView txtTotalDistance;

    @BindView(R.id.txtObjectivePace)
    TextView txtObjectivePace;

    @BindView(R.id.txtFinalPace)
    TextView txtFinalPace;

    @BindView(R.id.layoutImproveHistory)
    LinearLayout layoutImproveHistory;

    @BindView(R.id.TableImproveYourself)
    TableLayout tableImproveHistory;

    @BindView(R.id.TablePlans)
    TableLayout phasesTable;

    @BindView(R.id.outdoor_plans_include)
    LinearLayout outdoorPlansInclude;

    @BindView(R.id.outdoor_quickStart)
    LinearLayout outdoorQuickStart;

    private WorkoutType workoutType;
    private double improveYourselfObjectiveDistanceKm;
    private double totalRanDistanceKm;
    private double averageSpeedKmPerHour;
    private double maximumSpeedKmPerHour;
    private String improveYourselfMeasure;
    private long improveYourselfObjectiveTimeMillis;
    private String improveYourselfFocus;
    private double improveYourselfObjectivePace;
    private OutdoorAppState outdoorAppState;
    private long elapsedTimeMilliSeconds;

    @Inject
    User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_outdoor_result);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Obtain the google maps and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            Log.e(TAG, "No map fragment");
            return;
        }
        mapFragment.getMapAsync(this);

        // Verify that we got extras.
        if (getIntent().getExtras() == null) {
            Log.e(TAG, "No extras received");
            return;
        }

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();
        workoutType = outdoorAppState.getSelectedWorkoutType();

        totalRanDistanceKm = getIntent().getExtras().getDouble(App.extrasTotalDistanceKm, 0.0);
        averageSpeedKmPerHour = getIntent().getExtras().getDouble(App.extrasSpeedKmPerHour, 0.0);
        boolean showFinishButton = getIntent().getExtras().getBoolean(App.extrasShowFinishButton, true);
        maximumSpeedKmPerHour = getIntent().getExtras().getDouble(App.extrasMaxSpeedKmPerHour, 0.0);
        double averagePaceMinutesPerKm = getIntent().getExtras().getDouble(App.extrasPaceMinPerKm, 0.0);
        elapsedTimeMilliSeconds = getIntent().getExtras().getLong(App.extrasElapsedTimeMilliSeconds, 0);

        if (showFinishButton) {
            finishBtn.setVisibility(View.VISIBLE);
        } else {
            finishBtn.setVisibility(View.GONE);
        }

        switch (workoutType) {
            case ImproveYourself:
                //region ImproveYourself
                improveYourselfFocus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
                improveYourselfMeasure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure);
                improveYourselfObjectiveTimeMillis = getIntent().getExtras().getLong(App.extrasImproveYourselfObjectiveTimeMillis);
                improveYourselfObjectivePace = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectivePace);
                improveYourselfObjectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                qs_title.setText(R.string.improve_yourself_result);
                title_content.setBackgroundResource(R.color.improve_middle);
                contentQS.setBackgroundResource(R.drawable.side_nav_bar_improve);
                scroll.setBackgroundResource(R.color.colorGraysoft);
                values1.setBackgroundResource(R.color.colorGraysoft);
                values2.setBackgroundResource(R.color.colorGraysoft);
                toolbar.setBackgroundResource(R.mipmap.header_improve);
                txtDate.setText(formatter.format(new Date()));

                Log.i(TAG, String.format("Focus: %s, Measure: %s, Objective Time Millis: %d, Objective Pace: %.1f, Objective Distance: %.1f",
                        improveYourselfFocus, improveYourselfMeasure, improveYourselfObjectiveTimeMillis, improveYourselfObjectivePace, improveYourselfObjectiveDistanceKm));

                txtObjectiveDistance.setText(String.format(Locale.getDefault(), "%.0f", improveYourselfObjectiveDistanceKm * 1000.0));
                txtTotalDistance.setText(String.format(Locale.getDefault(), "%.0f", totalRanDistanceKm * 1000.0));

                txtTime.setText(DateUtils.millisToMinSec(elapsedTimeMilliSeconds));

                txtObjectivePace.setText(DateUtils.durationToMinSec((long) (improveYourselfObjectivePace * 60000.0)));
                txtFinalPace.setText(DateUtils.durationToMinSec((long) (averagePaceMinutesPerKm * 60000.0)));

                txtObjectiveTime.setText(DateUtils.durationToMinSec(improveYourselfObjectiveTimeMillis));

                Log.i(TAG, String.format("Total distance: %.1fkm, Objective distance: %.1fkm", totalRanDistanceKm, improveYourselfObjectiveDistanceKm));
                Log.i(TAG, String.format("Total distance: %.2fkm, Distance to complete training: %.2f, Percentage of required pace: %.4f, Maximum percentage of pace: %.4f", totalRanDistanceKm, improveYourselfObjectiveDistanceKm * App.minimumPacePercentageForCompleteTraining,
                        Math.abs(averagePaceMinutesPerKm - improveYourselfObjectivePace) / improveYourselfObjectivePace, App.maximumPacePercentageForEquality));

                if (totalRanDistanceKm > improveYourselfObjectiveDistanceKm * App.minimumPacePercentageForCompleteTraining &&
                        Math.abs(averagePaceMinutesPerKm - improveYourselfObjectivePace) / improveYourselfObjectivePace <= App.maximumPacePercentageForEquality) {
                    imgStatusGreen.setVisibility(View.VISIBLE);
                    imgStatusRed.setVisibility(View.GONE);
                    improveStatusText.setText(R.string.fulfilled);
                    improveStatusText.setTextColor(Utils.getColor(this, R.color.greenRunin));
                } else {
                    imgStatusGreen.setVisibility(View.GONE);
                    imgStatusRed.setVisibility(View.VISIBLE);
                    improveStatusText.setText(R.string.not_fulfilled);
                    improveStatusText.setTextColor(Utils.getColor(this, R.color.redRunin));
                }

                GPSRunPointOperationsDB gpsRunPointOperationsDB = new GPSRunPointOperationsDB();
                List<GPSRunPoint> gpsRunPoints = gpsRunPointOperationsDB.getPoints(outdoorAppState.getWorkout().getId(), this);

                int km = 0;
                int rows = 0;
                if (gpsRunPoints.size() > 0) {
                    double totalDistance = 0.0;
                    double nextDistance = 1000;
                    double prevDistance = 0.0;
                    GPSRunPoint oldPoint = gpsRunPoints.get(0);
                    long prevTime = oldPoint.getTime().getTime();

                    for (GPSRunPoint point : gpsRunPoints) {
                        totalDistance += point.distanceTo(oldPoint);
                        Log.i(TAG, String.format("Adding point. Total distance: %.3f, Next distance: %.3f, Date: %s", totalDistance, nextDistance, point.getTime()));
                        if (totalDistance >= nextDistance) {

                            km++;

                            double thisDistance = totalDistance - prevDistance;
                            if (thisDistance == 0.0) continue;

                            long thisTime = point.getTime().getTime() - prevTime;
                            double thisPace = (double) thisTime / thisDistance / 60.0;

                            prevDistance = totalDistance;
                            nextDistance += 1000;
                            prevTime = point.getTime().getTime();

                            TableRow tableRow = (TableRow) View.inflate(this, R.layout.row_table_history_mejorate, null);
                            tableRow.setBackgroundColor(Utils.getColor(this, km % 2 == 0 ? R.color.colorGraysoft : R.color.white));
                            rows++;

                            TextView txtKm = tableRow.findViewById(R.id.txtKm);
                            TextView txtRequiredPace = tableRow.findViewById(R.id.txtRequiredPace);
                            TextView txtAveragePace = tableRow.findViewById(R.id.txtAveragePace);
                            ImageView imgStatusGreen = tableRow.findViewById(R.id.imgStatusGreen);
                            ImageView imgStatusRed = tableRow.findViewById(R.id.imgStatusRed);

                            txtKm.setText(String.format(Locale.getDefault(), "%d", km));
                            txtRequiredPace.setText(getString(R.string.ritmo_entrenamiento_simple, DateUtils.minutesToMinSec(improveYourselfObjectivePace)));
                            txtAveragePace.setText(getString(R.string.ritmo_entrenamiento_simple, DateUtils.minutesToMinSec(thisPace)));

                            // We consider the user ran the required pace if it is within 20% of the objective.
                            if (Math.abs(thisPace - improveYourselfObjectivePace) / improveYourselfObjectivePace <= App.maximumPacePercentageForEquality) {
                                imgStatusGreen.setVisibility(View.VISIBLE);
                                imgStatusRed.setVisibility(View.GONE);
                            } else {
                                imgStatusGreen.setVisibility(View.GONE);
                                imgStatusRed.setVisibility(View.VISIBLE);
                            }

                            tableImproveHistory.addView(tableRow);
                        }
                        oldPoint = point;
                    }
                }

                if (rows > 0) {
                    layoutImproveHistory.setVisibility(View.VISIBLE);
                } else {
                    layoutImproveHistory.setVisibility(View.GONE);
                }

                outdoorMejorate.setVisibility(View.VISIBLE);
                plansResultStatus.setVisibility(View.GONE);
                outdoorPlansInclude.setVisibility(View.GONE);
                outdoorQuickStart.setVisibility(View.GONE);
                //endregion
                break;
            case Plans:
                //region Plans
                Training selectedTraining = outdoorAppState.getSelectedTraining();

                boolean completed = totalRanDistanceKm >= selectedTraining.getDistanceMeters() * App.minimumPacePercentageForCompleteTraining / 1000.0;

                // TODO: Convert to int
                qs_title.setText(getString(R.string.result_training, String.valueOf(outdoorAppState.getSelectedTraining().getSequence() + 1)));
                qs_title.setTextColor(Color.parseColor("#a905a5"));
                title_content.setBackgroundResource(R.color.colorYellowRunin);
                contentQS.setBackgroundResource(R.drawable.side_nav_bar_plans);
                scroll.setBackgroundResource(R.color.colorGraysoft);
                values1.setBackgroundResource(R.color.colorGraysoft);
                values2.setBackgroundResource(R.color.colorGraysoft);
                toolbar.setBackgroundResource(R.mipmap.header_plan);
                outdoorMejorate.setVisibility(View.GONE);
                plansResultStatus.setVisibility(View.VISIBLE);
                layoutImproveHistory.setVisibility(View.GONE);
                outdoorPlansInclude.setVisibility(View.VISIBLE);
                outdoorQuickStart.setVisibility(View.GONE);

                PlansTitleAndStatus.setText(getString(R.string.result_training_plan,
                        selectedTraining.getSequence() + 1,
                        completed ? getString(R.string.completed) : getString(R.string.entrenamiento_fallido)));
                PlansObjectiveVsAchieved.setText(getString(R.string.plan_objective_vs_achieved,
                        selectedTraining.getDistanceMeters() / 1000.0, totalRanDistanceKm, DateUtils.millisToMinSec(elapsedTimeMilliSeconds)));
                PlansResultCircleGreen.setVisibility(completed ? View.VISIBLE : View.GONE);
                PlansResultCircleRed.setVisibility(completed ? View.GONE : View.VISIBLE);

                int i = 0;
                for (Phase phase : selectedTraining.getPhases()) {
                    TableRow row = (TableRow) View.inflate(this, R.layout.adapter_phase_list, null);
                    TextView txtPhase = row.findViewById(R.id.txtPhase);
                    TextView txtInstruction = row.findViewById(R.id.txtInstruction);
                    TextView txtPace = row.findViewById(R.id.txtPace);
                    TextView txtTime = row.findViewById(R.id.txtTime);
                    ImageView imgStatusGreen = row.findViewById(R.id.imgStatusGreen);
                    ImageView imgStatusYellow = row.findViewById(R.id.imgStatusYellow);
                    ImageView imgStatusRed = row.findViewById(R.id.imgStatusRed);
                    ImageView imgStatusGray = row.findViewById(R.id.imgStatusGray);

                    txtPhase.setText(phase.getKindName());
                    txtInstruction.setText(phase.getInstructionName());
                    txtPace.setText(getString(R.string.ritmo_entrenamiento_simple, DateUtils.minutesToMinSec(phase.getPaceMinPerKm())));
                    txtTime.setText(DateUtils.millisToMinSec(phase.getDurationMillis()));

                    switch (outdoorAppState.getStageFinishedStatus(i)) {
                        case NotStarted:
                            Log.i(TAG, "The stage is not started");
                            imgStatusGray.setVisibility(View.VISIBLE);
                            imgStatusGreen.setVisibility(View.GONE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.GONE);
                            break;
                        case Finished:
                            Log.i(TAG, "The stage is finished");
                            imgStatusGray.setVisibility(View.GONE);
                            imgStatusGreen.setVisibility(View.VISIBLE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.GONE);
                            break;
                        case NotFinished:
                            Log.i(TAG, "The stage is not finished");
                            imgStatusGray.setVisibility(View.GONE);
                            imgStatusGreen.setVisibility(View.GONE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.VISIBLE);
                            break;
                    }

                    row.setBackgroundColor(Utils.getColor(this, i % 2 == 0 ? R.color.colorGraysoft : R.color.white));
                    phasesTable.addView(row);
                    i++;
                }
                //endregion
                break;
            case QuickStart:
                //region QuickStart
                outdoorMejorate.setVisibility(View.GONE);
                plansResultStatus.setVisibility(View.GONE);
                layoutImproveHistory.setVisibility(View.GONE);
                outdoorPlansInclude.setVisibility(View.GONE);
                outdoorQuickStart.setVisibility(View.VISIBLE);

                qs_title.setText(R.string.qs_results);
                //endregion
                break;
            default:
                throw new IllegalStateException("No such workout type");
        }

        kilometersQuickStart.setText(getString(R.string.n_km_short, totalRanDistanceKm));
        paceQuickStart.setText(getString(R.string.ritmo_entrenamiento_simple, DateUtils.minutesToMinSec(averagePaceMinutesPerKm)));

        double calories = userProfile.getWeight() * totalRanDistanceKm;
        caloriesQuickStart.setText(String.format(Locale.getDefault(), "%.1f cal", calories));

        timeQuickStart.setText(DateUtils.millisToMinSec(elapsedTimeMilliSeconds));
        txtAverageSpeed.setText(getString(R.string.speed_km_h, averageSpeedKmPerHour));
        txtMaxSpeedQS.setText(getString(R.string.speed_km_h, maximumSpeedKmPerHour));

        share.setTypeface(Utils.getFontBebasNeue(this));
        finishBtn.setTypeface(Utils.getFontBebasNeue(this));
        qs_title.setTypeface(Utils.getFontBebasNeue(this));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GPSRunPointOperationsDB gpsRunPointOperationsDB = new GPSRunPointOperationsDB();

        List<GPSRunPoint> points = gpsRunPointOperationsDB.getPoints(outdoorAppState.getWorkout().getId(), this);
        List<LatLng> list = new ArrayList<>();

        if (points.size() == 0) return;

        try {
            LatLng startLocation = new LatLng((float) points.get(0).getLatitude(), (float) points.get(0).getLongitude());

            for (GPSRunPoint point : points) {
                LatLng myLatLng = new LatLng((float) point.getLatitude(), (float) point.getLongitude());
                list.add(myLatLng);
            }

            Log.i(TAG, String.format("Number of points:  %d", list.size()));

            PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE);
            rectLine.addAll(list);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            googleMap.clear();
            googleMap.addPolyline(rectLine);
            googleMap.addMarker(new MarkerOptions().position(list.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(""));
            googleMap.addMarker(new MarkerOptions().position(list.get(list.size() - 1)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(""));
        } catch (SecurityException e) {
            Log.e(TAG, "Security error", e);
        } catch (Exception e) {
            Log.e(TAG, "Error drawing map", e);
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void closeResult(View view) {
        String newTypeOfRunner;
        Intent intent;

        switch (workoutType) {
            case ImproveYourself:
                saveToImproveHistory();
                newTypeOfRunner = App.runnerTypeOutdoor;
                intent = new Intent(this, OutdoorDashboardActivity.class);
                intent.putExtra(App.extrasScreenName, App.screenNameImprove);
                intent.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, improveYourselfObjectiveDistanceKm);
                intent.putExtra(App.extrasImproveYourselfMeasure, improveYourselfMeasure);
                intent.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, improveYourselfObjectiveTimeMillis);
                intent.putExtra(App.extrasImproveYourselfFocus, improveYourselfFocus);
                intent.putExtra(App.extrasImproveYourselfObjectivePace, improveYourselfObjectivePace);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case Plans:
                saveToPlansHistory();
                newTypeOfRunner = App.runnerTypeOutdoor;
                intent = new Intent(this, OutdoorDashboardActivity.class);
                intent.putExtra(App.extrasScreenName, App.screenNamePlansMain);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case QuickStart:
                saveToQuickStartHistory();
                newTypeOfRunner = App.runnerTypeQuickstart;
                intent = new Intent(this, OutdoorDashboardActivity.class);
                intent.putExtra(App.extrasScreenName, App.screenNameQuick);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            default:
                throw new IllegalStateException("No such workout type");
        }

        // Redefine the type of runner we are
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(App.sharedPreferencesPropertyTypeOfRunner, newTypeOfRunner);
        editor.apply();

        startActivity(intent);
        finish();
    }

    public void setShare(@SuppressWarnings("unused") View view) {

        String packageName = "com.facebook.katana";
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent == null) {
            // Cannot open application
            Intent intent2 = new Intent(Intent.ACTION_SEND);
            intent2.putExtra(Intent.EXTRA_TEXT, "*RUNiN");
            intent2.setType("text/plain");
            startActivity(Intent.createChooser(intent2, getResources().getText(R.string.send_to)));
        } else {

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(icon)
                    .setUserGenerated(true)
                    .build();

            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "fitness.course")
                    .putString("og:url", "http://www.runinapp.com.mx/")
                    .putString("og:title", "*RUNiN APP")
                    .putString("og:description", getString(R.string.a_new_style_of_training))
                    .build();


            // Create an action
            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("fitness.runs")
                    .putObject("course", object)
                    .putPhoto("image", photo)
                    .build();

            // Create the content
            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("course")
                    .setAction(action)
                    .build();

            ShareDialog.show(this, content);
        }
    }

    private void saveToImproveHistory() {
        OutdoorWorkoutsHistory outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.ImproveYourself);
        outdoorWorkoutsHistory.setDistanceKm(totalRanDistanceKm);
        outdoorWorkoutsHistory.setDurationMillis(elapsedTimeMilliSeconds);
        outdoorWorkoutsHistory.setCompleted(totalRanDistanceKm >= improveYourselfObjectiveDistanceKm);
        outdoorWorkoutsHistory.setAverageSpeedKmPerHour(averageSpeedKmPerHour);
        outdoorWorkoutsHistory.setMaximumSpeedKmPerHour(maximumSpeedKmPerHour);
        outdoorWorkoutsHistory.setStatus(WorkoutStatus.Finished);

        OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();
        outdoorWorkoutsHistoryOperationsDB.update(outdoorWorkoutsHistory.getUpdateContentValues(), outdoorAppState.getWorkout().getId(), this);
    }

    private void saveToPlansHistory() {
        boolean completed = totalRanDistanceKm >= outdoorAppState.getSelectedTraining().getDistanceMeters() * App.minimumPacePercentageForCompleteTraining / 1000.0;

        OutdoorWorkoutsHistory outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.Plans);
        outdoorWorkoutsHistory.setDistanceKm(totalRanDistanceKm);
        outdoorWorkoutsHistory.setDurationMillis(elapsedTimeMilliSeconds);
        outdoorWorkoutsHistory.setCompleted(completed);
        outdoorWorkoutsHistory.setAverageSpeedKmPerHour(averageSpeedKmPerHour);
        outdoorWorkoutsHistory.setMaximumSpeedKmPerHour(maximumSpeedKmPerHour);
        outdoorWorkoutsHistory.setStatus(WorkoutStatus.Finished);

        OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();
        outdoorWorkoutsHistoryOperationsDB.update(outdoorWorkoutsHistory.getUpdateContentValues(), outdoorAppState.getWorkout().getId(), this);


        if (completed) {
            ((App) getApplication()).speakOut(getString(R.string.distance_larger_than_required));
        } else {
            ((App) getApplication()).speakOut(getString(R.string.distance_lesser_than_required));
        }
    }

    private void saveToQuickStartHistory() {
        OutdoorWorkoutsHistory outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.QuickStart);
        outdoorWorkoutsHistory.setDistanceKm(totalRanDistanceKm);
        outdoorWorkoutsHistory.setCompleted(true);
        outdoorWorkoutsHistory.setDurationMillis(elapsedTimeMilliSeconds);
        outdoorWorkoutsHistory.setAverageSpeedKmPerHour(averageSpeedKmPerHour);
        outdoorWorkoutsHistory.setMaximumSpeedKmPerHour(maximumSpeedKmPerHour);
        outdoorWorkoutsHistory.setStatus(WorkoutStatus.Finished);

        OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();
        outdoorWorkoutsHistoryOperationsDB.update(outdoorWorkoutsHistory.getUpdateContentValues(), outdoorAppState.getWorkout().getId(), this);
    }
}
