package com.runin.runinapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.runin.runinapp.api.APIService;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.User;
import com.runin.runinapp.data.database.GPSRunPoint;
import com.runin.runinapp.data.database.GPSRunPointOperationsDB;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.indoor.IndoorDashboardActivity;
import com.runin.runinapp.indoor.SelectLevelActivity;
import com.runin.runinapp.outdoor.OutdoorDashboardActivity;
import com.runin.runinapp.outdoor.OutdoorResultActivity;
import com.runin.runinapp.outdoor.OutdoorSelectPlanActivity;
import com.runin.runinapp.settings.ProfileActivity;
import com.runin.runinapp.tutorial.TutorialActivity;
import com.runin.runinapp.utils.ExtensionsKt;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The splash screen functionality is to select the first Activity based on the user preferences and other state, i.e. tutorial finished, user profile saved...
 * Created by Cesar on 09/08/2016
 */
public class SplashScreenActivity extends BaseActivity {
    private static String TAG = SplashScreenActivity.class.getSimpleName();

    private IndoorAppState indoorAppState;
    private OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB;
    private List<OutdoorWorkoutsHistory> incompleteWorkouts;

    private boolean userExists = false;

    @Inject
    SharedPreferences sharedPref;
    @Inject
    User userProfile;
    @Inject
    APIService apiService;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        checkUser();

        indoorAppState = ((App) this.getApplication()).getIndoorAppState();
        outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        MobileAds.initialize(this, "ca-app-pub-5956662992010209~4382315577");
    }


    void checkUser() {
        userExists = FirebaseAuth.getInstance().getCurrentUser() != null;
        if (userExists) {
            disposable = apiService.getUserInfo()
                    .subscribeOn(Schedulers.io())
                    .doFinally(this::hideProgress)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(user -> {
                        ExtensionsKt.saveUser(sharedPref,userProfile, user);
                        redirect(true);
                    }, throwable -> {
                        if (userNotFound(throwable)) {
                            redirect(false);
                        } else {
                            getHandleNetworkError();
                        }
                    });
        } else {
            redirect(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }

    void redirect(boolean isSavedProfileData) {
        // New Handler to start the Menu-Activity and close this Splash-Screen after some time.
        new Handler().postDelayed(() -> {
            // Obtain the saved preferences
            boolean isTutorialVisited = sharedPref.getBoolean(App.sharedPreferencesPropertyTutorialVisited, false);
//            boolean isSavedProfileData = sharedPref.getBoolean(App.sharedPreferencesPropertyProfileSaved, false);
            String typeOfRunner = sharedPref.getString(App.sharedPreferencesPropertyTypeOfRunner, null);

            // Maybe we're recovering from a crash
            incompleteWorkouts = outdoorWorkoutsHistoryOperationsDB.getIncomplete(SplashScreenActivity.this);
            boolean outdoorCrashed = incompleteWorkouts.size() > 0;

            if (userExists) {
                if (!isSavedProfileData) {
                    Intent saveProfileIntent = new Intent(SplashScreenActivity.this, ProfileActivity.class);
                    startActivity(saveProfileIntent);
                    finish();
                } else if (!isTutorialVisited) {
                    Intent tutorialIntent = new Intent(SplashScreenActivity.this, TutorialActivity.class);
                    startActivity(tutorialIntent);
                    finish();
                } else if (typeOfRunner == null) {
                    Intent placeToRunIntent = new Intent(SplashScreenActivity.this, SelectIndoorOutdoorActivity.class);
                    startActivity(placeToRunIntent);
                    finish();
                } else if (typeOfRunner.equals(App.runnerTypeIndoor)) {
                    if (indoorAppState.getIndoorLevel() == null) {
                        Intent intentOutdoor = new Intent(SplashScreenActivity.this, SelectLevelActivity.class);
                        startActivity(intentOutdoor);
                        finish();
                    } else {
                        Intent intentOutdoor = new Intent(SplashScreenActivity.this, IndoorDashboardActivity.class);
                        intentOutdoor.putExtra(App.extrasScreenName, App.screenNameTracks);
                        startActivity(intentOutdoor);
                        finish();
                    }
                } else if (outdoorCrashed) {
                    recoverSession();
                } else if (typeOfRunner.equals(App.runnerTypeOutdoor)) {
                    Intent planOutdoorIntent = new Intent(SplashScreenActivity.this, OutdoorSelectPlanActivity.class);
                    startActivity(planOutdoorIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, OutdoorDashboardActivity.class);
                    mainIntent.putExtra(App.extrasScreenName, App.screenNameQuick);
                    startActivity(mainIntent);
                    finish();
                }
            } else {
                Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }

        }, 1000);
    }

    /**
     * Check the database for any workout that has not been finished (probably the app closed before the run was done) and shows the result
     * activity.
     */
    void recoverSession() {
        Log.i(TAG, "Recovering workout session");

        Intent resultPage = new Intent(this, OutdoorResultActivity.class);

        OutdoorAppState outdoorAppState = ((App) getApplication()).getOutdoorAppState();
        OutdoorWorkoutsHistory workout = incompleteWorkouts.get(0);
        OutdoorAppState.WorkoutType workoutType = workout.getWorkoutType();

        // workout type as: quick start, improve yourself or plans
        outdoorAppState.setSelectedWorkoutType(workoutType);
        outdoorAppState.setWorkout(workout);

        // Obtain the save points of the workout and initialize some variables
        GPSRunPointOperationsDB databaseOperations = new GPSRunPointOperationsDB();
        List<GPSRunPoint> points = databaseOperations.getPoints(workout.getId(), this);

        double distanceMeters = 0.0;
        long timeMilliseconds = 0;
        GPSRunPoint lastPoint = null;

        // From the points, calculate the distance ran and the time it took to run
        for (GPSRunPoint point : points) {
            if (lastPoint != null) {
                distanceMeters += point.distanceTo(lastPoint);
                timeMilliseconds += point.getTime().getTime() - lastPoint.getTime().getTime();
            }
            lastPoint = point;
        }

        // TODO: Why for "improve yourself", we do this??
        if (workoutType == OutdoorAppState.WorkoutType.ImproveYourself && workout.getDurationMillis() > 0) {
            timeMilliseconds = 0;
        }

        // Calculate the average pace
        double averagePace = distanceMeters == 0.0 ? 0 : timeMilliseconds / distanceMeters / 60.0;

        resultPage.putExtra(App.extrasTotalDistanceKm, distanceMeters / 1000.0);
        resultPage.putExtra(App.extrasSpeedKmPerHour, distanceMeters / timeMilliseconds * 3600.0);
        resultPage.putExtra(App.extrasPaceMinPerKm, averagePace);
        resultPage.putExtra(App.extrasMaxSpeedKmPerHour, workout.getMaximumSpeedKmPerHour());
        resultPage.putExtra(App.extrasElapsedTimeMilliSeconds, timeMilliseconds);

        switch (workoutType) {
            case QuickStart:
                // For quickstart nothing much is done
                outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.QuickStart);
                break;
            case ImproveYourself:
                Double objectivePace = workout.getObjectiveTimeMillis() / workout.getObjectiveDistanceKm() / 60000.0;

                resultPage.putExtra(App.extrasImproveYourselfFocus, App.focusDistance);
                resultPage.putExtra(App.extrasImproveYourselfMeasure, workout.getMeasuringUnit() == App.LengthUnit.Meter ? App.measureMeters : App.measureKilometers);
                resultPage.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, workout.getObjectiveTimeMillis());
                resultPage.putExtra(App.extrasImproveYourselfObjectivePace, objectivePace);
                resultPage.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, workout.getObjectiveDistanceKm());

                outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.ImproveYourself);
                break;
            case Plans:
                outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.Plans);
                outdoorAppState.setSelectedPlan(outdoorAppState.getPlanById(workout.getPlanId()));
                outdoorAppState.setSelectedStage(outdoorAppState.getSelectedPlan().getStage(workout.getStageId()));
                outdoorAppState.setSelectedTraining(outdoorAppState.getSelectedStage().getTraining(workout.getTrainingId()));
                outdoorAppState.setSelectedPlanName(outdoorAppState.getSelectedPlan().getName());
                outdoorAppState.populatePhases();

                break;
        }

        resultPage.putExtra(App.extrasShowFinishButton, true);
        resultPage.putExtra(App.extrasPaceMinPerKm, averagePace);

        startActivity(resultPage);
        this.finish();
    }

}
