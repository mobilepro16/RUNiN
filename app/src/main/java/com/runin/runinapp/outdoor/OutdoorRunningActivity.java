package com.runin.runinapp.outdoor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.OutdoorAppState.WorkoutStatus;
import com.runin.runinapp.data.OutdoorAppState.WorkoutType;
import com.runin.runinapp.data.Phase;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.data.database.GPSRunPoint;
import com.runin.runinapp.data.database.GPSRunPointOperationsDB;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.outdoor.plans.TrainingDetailsActivity;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.ExtensionsKt;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.iabHelper.IabHelper;
import com.runin.runinapp.utils.iabHelper.IabResult;
import com.runin.runinapp.utils.iabHelper.Purchase;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.runin.runinapp.App.LengthUnit.Kilometer;
import static com.runin.runinapp.App.LengthUnit.Meter;

/**
 * Activity to get the distance, velocity and calories in a quick start routine
 * Created by Cesar on 09/08/2016
 * Modified by Samuel Kobelkowsky on 10/11/2017
 * <p>
 * TODO: After Google API 12.0.0 (early 2018) remove the deprecation warning and update to the last library.
 */
public class OutdoorRunningActivity extends AppCompatActivity {

    // region Declaration of variables
    /**
     * For logging
     */
    private static final String TAG = OutdoorRunningActivity.class.getSimpleName();

    /**
     * Code used in requesting runtime permissions.  Value is not arbitrary but not repeated.
     */
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE = 12;

    /**
     * Constant used in the location settings dialog.  Value is not arbitrary but not repeated.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2500;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Key for storing the current location when the activity is destroyed but state will be restored when started again.
     */
    private final static String KEY_CURRENT_LOCATION = "current_location";

    /**
     * Used with the timer that counts the time of the user running
     */
    private final Handler handlerCounterTime = new Handler();

    @BindView(R.id.txtDistanceCounter)
    TextView txtDistanceCounter;

    @BindView(R.id.layoutObtainingLocation)
    LinearLayout layoutObtainingLocation;

    @BindView(R.id.txtTimeCounter)
    TextView txtTimeCounter;

    @BindView(R.id.txtPaceCounter)
    TextView txtPaceCounter;

    @BindView(R.id.txtDistanceLabel)
    TextView txtDistanceLabel;

    @BindView(R.id.txtSongName)
    TextView txtSongName;

    @BindView(R.id.motivation_message)
    TextView txtMotivationMessage;

    @BindView(R.id.buttonStop)
    Button buttonStop;

    @BindView(R.id.buttonShowProgram)
    Button buttonShowProgram;

    @BindView(R.id.imageDiagonalLine)
    ImageView imageDiagonalLine;

    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;

    @BindView(R.id.layoutAdvertising)
    RelativeLayout layoutAdvertising;

    @BindView(R.id.layoutInfoWindow)
    LinearLayout layoutInfoWindow;

    @BindView(R.id.adView)
    AdView adView;
    /**
     * Database manager for the workouts
     */
    OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB;
    /**
     * Tells if we are currently running
     */
    private boolean isRunning = false;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;
    /**
     * The last location point obtained
     */
    private Location mCurrentLocation;
    /**
     * The routine mode controls the aspect of the UI: quickstart, improve yourself, training plans, etc.
     */
    private WorkoutType workoutType;
    /**
     * The time we started to run
     */
    private long startTime;
    /**
     * Used in Training Plans mode
     */
    private TextView txtRoutineNumber;
    /**
     * Used in Training Plans mode
     */
    private TextView txtRoutinePhase;
    /**
     * Used in Training Plans mode
     */
    private TextView txtRoutineInstructions;
    /**
     * Used in Training Plans mode
     */
    private TextView txtRoutinePace;
    /**
     * Used in Training Plans mode
     */
    private TextView txtRoutineTime;
    /**
     * The distance ran in this training
     */
    private double totalDistanceMeters;
    /**
     * The maximum speed in km/h
     */
    private double maximumSpeedKmPerHour = 0.0;
    /**
     * The time ran in this training
     */
    private long elapsedTimeMilliSeconds;
    /**
     * The main routine time, it updates the values on screen (distance, pace)
     */
    private Runnable runnableTimer;
    /**
     * False if can't access Google Play Store
     */
    private boolean billingAvailable = false;
    /**
     * The object that processes the in app purchase
     */
    private IabHelper mIabHelper;
    /**
     * For "Improve yourself", this is the Distance the user wants to run.
     */
    private double improveYourselfObjectiveDistanceKm = 0.0;
    /**
     * "Improve yourself" sends meters or kilometers. Not very clever.
     */
    private String improveYourselfMeasure = "";
    /**
     * For "Improve yourself", the objective time the user wants to run.
     */
    private long improveYourselfObjectiveTimeMillis;
    /**
     * "Improve yourself" objectives can be about distance or calories.
     */
    private String improveYourselfFocus;
    /**
     * Recommended pace for "Improve yourself".
     */
    private double improveYourselfObjectivePace;
    /**
     * Contains the total time that the workout has been paused
     */
    private long pausedTotalTime;
    /**
     * The time the last workout pause started
     */
    private long pauseStartTime;
    /**
     * In certain plans there is a voice that tells the user that he/she has ran certain distance
     */
    private long nextSonoCoach;
    /**
     * For training plans
     */
    private long nextSonoCoach2;
    /**
     * For training plans
     */
    private long nextSonoCoach3;
    /**
     * For training plans
     */
    private List<Phase> phases;
    /**
     * The current phase index for plans running.
     * TODO: Use only the currentPhase vairable. Maybe the field sequence or is position in the List<Phase> phases
     */
    private int currentPhaseIndex = 0;
    /**
     * Object used for GPS points database insertion
     */
    private GPSRunPointOperationsDB gpsRunPointOperationsDB;
    /**
     * Application state for outdoor workouts
     */
    private OutdoorAppState outdoorAppState;
    /**
     * The last GPS point obtained
     */
    private GPSRunPoint oldGPSPoint = null;

    @Inject
    SharedPreferences sharedPreferences;
    // endregion

    //region Activity life cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        setContentView(R.layout.activity_outdoor_main);

        ButterKnife.bind(this);

        // Update the UI based on parameters passed to the Activity. Also add actions to buttons, etc.
        initializeUI();

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Initialize the location service
        initializeLocation();

        // Initialize the ad service. If user opted out of ads, then show a motivation message
        initializeAds();

        // Let the user opt out of the ads by purchasing the "no ads" product
        initializeInAppPurchase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");

        stopLocationUpdates();

        disposeAds();
    }

    @Override
    public void onBackPressed() {
        pauseRoutine();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checks if we got permission to use location services. If so, start using them.
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE:
                if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdates();
                    } else {
                        // Permission was denied. Display an error message.
                        Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Permission was denied. Display an error message.
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");

        // Then process the In App Purchase
        // Pass on the activity result to the helper for handling
        if (mIabHelper == null || !mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            Log.i(TAG, "Not an mIabHelper result");
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...

            // First process the settings for location
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (resultCode == Activity.RESULT_OK) {
                    Log.w(TAG, "Location permissions check ok now, can continue");
                    startLocationUpdates();
                } else {
                    Log.e(TAG, "Location permissions check are still bad, can't continue");
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Restore app state when activity was terminated
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_CURRENT_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
            }
        }
    }

    // endregion

    // region Advertising and In App Purchase of Ad Free

    /**
     * Initialize the Ad System
     */
    private void initializeAds() {
        // Obtain whether the user has purchased the Ad Free product
        String showAddsStatusSaved = sharedPreferences.getString(App.sharedPreferencesPropertyAdsFreeStatus, App.adFreeNotPurchased);

        // Update the UI depending on the Ad Free product purchased or not
        adStatusDependent(showAddsStatusSaved.equals(App.adFreeNotPurchased));
    }

    /**
     * Depending if the user purchased the Ad Free product, some layouts show and some others not.
     *
     * @param showAds If the user purchased the Ad Free product
     */
    private void adStatusDependent(boolean showAds) {

        // Plans never show ads
        if (workoutType == WorkoutType.Plans) {
            showAds = false;
        }

        // Initialize the Ad system
        if (showAds) {
            Log.i(TAG, "Ad Status: show");

            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            layoutAdvertising.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "Ad Status: hide");

            adView.destroy();

            switch (workoutType) {
                case ImproveYourself:
                    findViewById(R.id.fragmentBottomImproveYourself).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentBottomQuickstart).setVisibility(View.GONE);
                    findViewById(R.id.fragmentBottomTrainingPlans).setVisibility(View.GONE);
                    buttonShowProgram.setVisibility(View.GONE);
                    break;
                case Plans:
                    findViewById(R.id.fragmentBottomImproveYourself).setVisibility(View.GONE);
                    findViewById(R.id.fragmentBottomQuickstart).setVisibility(View.GONE);
                    findViewById(R.id.fragmentBottomTrainingPlans).setVisibility(View.VISIBLE);
                    buttonShowProgram.setVisibility(View.VISIBLE);
                    break;
                case QuickStart:
                    findViewById(R.id.fragmentBottomImproveYourself).setVisibility(View.GONE);
                    findViewById(R.id.fragmentBottomQuickstart).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentBottomTrainingPlans).setVisibility(View.GONE);
                    buttonShowProgram.setVisibility(View.GONE);
                    break;
                default:
                    throw new IllegalStateException("No such workout type");
            }

            layoutBottom.setVisibility(View.VISIBLE);
            layoutAdvertising.setVisibility(View.GONE);
        }

        txtMotivationMessage.setText(getRandomMotivationalMessage());
    }

    /**
     * Called when destroying the Activity
     */
    private void disposeAds() {
        // For In App Purchase
        if (mIabHelper != null) {
            try {
                mIabHelper.disposeWhenFinished();
            } catch (Exception e) {
                Log.e(TAG, "Cannot dispose ads");
            }
            mIabHelper = null;
        }
    }

    /**
     * Initialize the In App Purchase
     */
    private void initializeInAppPurchase() {
        // Initializes the store
        mIabHelper = new IabHelper(this, getString(R.string.base64EncodedPublicKey));
        mIabHelper.enableDebugLogging(true);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                // Checks that we can access the store
                billingAvailable = result.isSuccess();
                if (!billingAvailable) {
                    Log.e(TAG, "InAppPurchase isn't available");
                    return;
                }

                // Maybe another purchase process has finished while we're at it
                if (mIabHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                try {
                    mIabHelper.queryInventoryAsync((result1, inventory) -> {

                        // Maybe another purchase process has finished while we're at it
                        if (mIabHelper == null) return;

                        if (result1.isFailure()) {
                            Log.e(TAG, "Failed to get Inventory: " + result1);
                            return;
                        }

                        Log.i(TAG, "Query inventory was successful");
                        boolean hasPurchased = inventory.hasPurchase(App.googlePlaySkuAddFree);

                        Log.i(TAG, "Product is " + (hasPurchased ? "" : "not ") + "purchased");
                        adStatusDependent(!hasPurchased); // Show ads if product is not purchased

                        String status = hasPurchased ? App.adFreePurchased : App.adFreeNotPurchased;
                        ExtensionsKt.putString(sharedPreferences, App.sharedPreferencesPropertyAdsFreeStatus, status);
                    });
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    /**
     * Asks the user if he wants to purchase the Ad Free product
     *
     * @param view Used in the layout
     */
    @SuppressWarnings("UnusedParameters")
    public void quitAd(View view) {
        AlertDialog.Builder quitAddBuilder = new AlertDialog.Builder(this);
        quitAddBuilder.setTitle(getString(R.string.addCloseTitle));
        quitAddBuilder.setMessage(getString(R.string.addCloseBody));
        quitAddBuilder.setPositiveButton(R.string.addClosePositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                purchaseAdFree();
            }
        });
        quitAddBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog quitAddDialog = quitAddBuilder.create();
        quitAddDialog.show();
    }

    /**
     * The user wants to purchase the Ad Free product
     */
    private void purchaseAdFree() {
        Log.i(TAG, "About to purchase product with SKU: " + App.googlePlaySkuAddFree);

        try {
            if (mIabHelper != null) {
                mIabHelper.launchPurchaseFlow(this, App.googlePlaySkuAddFree, 10003, new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        Log.i(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                        // if we were disposed of in the meantime, quit.
                        if (mIabHelper == null) return;

                        if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                            Toast.makeText(OutdoorRunningActivity.this, getString(R.string.product_already_purchased), Toast.LENGTH_SHORT).show();
                            adStatusDependent(false);
                        } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                            Toast.makeText(OutdoorRunningActivity.this, getString(R.string.resultAddCloseNegativeBody), Toast.LENGTH_SHORT).show();
                        } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                            Toast.makeText(OutdoorRunningActivity.this, getString(R.string.purchase_canceled), Toast.LENGTH_SHORT).show();
                        } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_OK && !result.isFailure() && purchase != null) {
                            String sku = purchase.getSku();

                            if (!App.googlePlaySkuAddFree.equals(sku)) {
                                Log.e(TAG, "Purchased product is different than requested");
                            }

                            Toast.makeText(OutdoorRunningActivity.this, getString(R.string.thanks_for_purchasing), Toast.LENGTH_SHORT).show();

                            adStatusDependent(false);

                            Log.i(TAG, "purchased: " + sku);
                        } else {
                            Log.i(TAG, "Failure, code: " + result.getMessage());

                            adStatusDependent(true);

                            Toast.makeText(OutdoorRunningActivity.this, getString(R.string.purchase_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "Error in purchase", e);
            Toast.makeText(this, getString(R.string.purchase_failure), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * If the user purchased the AdFree then we fill the space with a motivational message.
     *
     * @return The resource id of a random motivational message.
     */
    private String getRandomMotivationalMessage() {
        Random r = new Random();

        int index = r.nextInt(5) + 1;
        int id;
        switch (index) {
            case 1:
                id = R.string.motivation_message1;
                break;
            case 2:
                id = R.string.motivation_message2;
                break;
            case 3:
                id = R.string.motivation_message3;
                break;
            case 4:
                id = R.string.motivation_message4;
                break;
            case 5:
                id = R.string.motivation_message5;
                break;
            default:
                id = R.string.motivation_message1;
        }

        return getString(id);
    }

    // endregion

    // region Location updates

    /**
     * Initialize the user location
     */
    private void initializeLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                gotNewLocation(locationResult);
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationSettingsRequest = (new LocationSettingsRequest.Builder()).addLocationRequest(mLocationRequest).build();

        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                requestLocationPermissions();
            }
        } else {
            startLocationUpdates();
        }
    }

    /**
     * Check permissions and if necessary asks for them
     */
    private void requestLocationPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            Snackbar.make(findViewById(android.R.id.content), R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE).
                    setAction(getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(OutdoorRunningActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE);
                        }
                    }).show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(OutdoorRunningActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * Main location method. What to do when we get a new position of the user.
     *
     * @param locationResult The result of the periodic location service
     */
    private void gotNewLocation(LocationResult locationResult) {
        mCurrentLocation = locationResult.getLastLocation();

        // The maximum difference in milliseconds from GPS samples. Anything larger than this will be discarded
        final long maxSamplesDifference = BuildConfig.DEBUG && Utils.isEmulator() ? 150000 : 25000;
        final int maxAccuracy = BuildConfig.DEBUG && Utils.isEmulator() ? 25 : 17;

        ///////////////////// ERASE ME //////////////////////////
        if (BuildConfig.DEBUG && Utils.isEmulator()) {
            mCurrentLocation.setSpeed(1.5f);
            mCurrentLocation.setTime((new Date()).getTime());
        }

        ////////////////////////////////////////////////////////

        // Not all the samples from GPS are going to be used.
        if (!mCurrentLocation.hasAccuracy() || // GPS didn't attach yet
                mCurrentLocation.getAccuracy() > maxAccuracy || // The sample is not accurate enough (m)
                mCurrentLocation.getSpeed() > 14 || // Speed is too large (m/s)
                mCurrentLocation.getTime() < (new Date()).getTime() - maxSamplesDifference) // Sample is too old
        {
            Log.e(TAG, String.format("Location discarded. Accuracy: %.0f m, Speed: %.1f km/h, Time: %.1f s",
                    mCurrentLocation.getAccuracy(), mCurrentLocation.getSpeed() * 3.6, (1.0 * (new Date()).getTime() - mCurrentLocation.getTime()) / 1000));
            return;
        }

        // Do something with the new location
        if (!isRunning) {
            startRunning();
        } else {
            updateDistancePaceAndSpeed();
        }

        // Hide the obtaining location status
        if (layoutObtainingLocation.getVisibility() == View.VISIBLE) {
            layoutObtainingLocation.setVisibility(View.GONE);
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        if (ActivityCompat.checkSelfPermission(OutdoorRunningActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                        // If we want to notify the user that we started location, this is the place to do it.
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(OutdoorRunningActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.", sie);
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Toast.makeText(OutdoorRunningActivity.this, R.string.location_setting_inadequate_fix_them, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Do whatever you need to do here, like notifying the user.
                }
            });
        }
    }

    // endregion

    // region Main routine and UI logic

    /**
     * Initialize the UI depending on the routine mode selected by the user, that controls the aspect of the UI:
     * quickstart, improve yourself, training plans, etc.
     */
    private void initializeUI() {
        // Do we have a toolbar?
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        workoutType = outdoorAppState.getSelectedWorkoutType();

        // Object to saving into the database
        OutdoorWorkoutsHistory outdoorWorkoutsHistory;

        // Show de banner of obtaining your position
        layoutObtainingLocation.setVisibility(View.VISIBLE);

        switch (workoutType) {
            case Plans:
                Log.i(TAG, "Running a plan");

                outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.Plans);
                outdoorWorkoutsHistory.setPlanId(outdoorAppState.getSelectedPlan().getId());
                outdoorWorkoutsHistory.setStageId(outdoorAppState.getSelectedStage().getSequence());
                outdoorWorkoutsHistory.setTrainingId(outdoorAppState.getSelectedTraining().getSequence());
                outdoorWorkoutsHistory.setObjectiveDistanceKm(outdoorAppState.getSelectedTraining().getDistanceMeters() / 1000);
                outdoorWorkoutsHistory.setObjectiveTimeMillis(outdoorAppState.getSelectedTraining().getDuration());
                outdoorWorkoutsHistory.setStatus(OutdoorAppState.WorkoutStatus.NotStarted);

                break;
            case ImproveYourself:
                Log.i(TAG, "Running an improve yourself");

                // Obtain parameters passed to the Activity
                if (getIntent().getExtras() != null) {
                    improveYourselfObjectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);
                    improveYourselfMeasure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure);
                    improveYourselfObjectiveTimeMillis = getIntent().getExtras().getLong(App.extrasImproveYourselfObjectiveTimeMillis);
                    improveYourselfFocus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
                    improveYourselfObjectivePace = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectivePace);

                    Log.i(TAG, String.format("Distance: %.3f, Measure: %s, Duration: %d, Focus: %s, Pace: %.1f", improveYourselfObjectiveDistanceKm, improveYourselfMeasure, improveYourselfObjectiveTimeMillis, improveYourselfFocus, improveYourselfObjectivePace));
                }

                App.LengthUnit measuringUnit;
                switch (improveYourselfMeasure) {
                    case App.measureMeters:
                        measuringUnit = Meter;
                        Log.i(TAG, String.format("%.0f m", improveYourselfObjectiveDistanceKm * 1000.0));
                        break;
                    case App.measureKilometers:
                        measuringUnit = Kilometer;
                        Log.i(TAG, String.format("%.1f km", improveYourselfObjectiveDistanceKm));
                        break;
                    default:
                        throw new IllegalStateException("Invalid measuring unit");
                }

                outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.ImproveYourself);
                outdoorWorkoutsHistory.setMeasuringUnit(measuringUnit);
                outdoorWorkoutsHistory.setObjectiveDistanceKm(improveYourselfObjectiveDistanceKm);
                outdoorWorkoutsHistory.setObjectiveTimeMillis(improveYourselfObjectiveTimeMillis);
                outdoorWorkoutsHistory.setStatus(OutdoorAppState.WorkoutStatus.NotStarted);

                break;
            case QuickStart:
                Log.i(TAG, "Running a quickstart");

                outdoorWorkoutsHistory = new OutdoorWorkoutsHistory(new Date(), WorkoutType.QuickStart);
                outdoorWorkoutsHistory.setObjectiveTimeMillis(0L);
                outdoorWorkoutsHistory.setStatus(OutdoorAppState.WorkoutStatus.NotStarted);

                break;
            default:
                throw new IllegalStateException("No such workout type");
        }

        outdoorWorkoutsHistory.setCompleted(false);
        outdoorWorkoutsHistory.setDistanceKm(0.0);

        // Obtain an ID from the database
        long id = outdoorWorkoutsHistoryOperationsDB.add(outdoorWorkoutsHistory.getAddContentValues(), this);

        outdoorWorkoutsHistory.setId(id);

        // Let some elements show a nice font
        txtDistanceCounter.setTypeface(Utils.getFontBebasNeue(this));
        txtDistanceLabel.setTypeface(Utils.getFontBebasNeue(this));
        buttonStop.setTypeface(Utils.getFontBebasNeue(this));

        // Add actions to buttons
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseRoutine();
            }
        });

        // Obtain an ID from the database
        gpsRunPointOperationsDB = new GPSRunPointOperationsDB();
        outdoorAppState.setWorkout(outdoorWorkoutsHistory);

        // Update the UI depending on the routine mode that the user selected.
        Log.i(TAG, "Routine mode: " + workoutType);
        switch (workoutType) {
            case ImproveYourself:
                imageDiagonalLine.setImageResource(R.mipmap.linea_diagonal_improve);
                layoutInfoWindow.setBackground(Utils.getDrawable(this, R.drawable.side_nav_bar_improve));
                toolbar.setBackground(Utils.getDrawable(this, R.mipmap.header_improve));
                break;
            case Plans:
                txtRoutineNumber = findViewById(R.id.num_entrenamiento);
                txtRoutinePhase = findViewById(R.id.fase_entrenamiento);
                txtRoutineInstructions = findViewById(R.id.instruccion_entrenamiento);
                txtRoutinePace = findViewById(R.id.ritmo_entrenamiento);
                txtRoutineTime = findViewById(R.id.time_training);

                imageDiagonalLine.setImageResource(R.mipmap.linea_diagonal_plans);
                layoutInfoWindow.setBackground(Utils.getDrawable(this, R.drawable.side_nav_bar_plans));
                toolbar.setBackground(Utils.getDrawable(this, R.mipmap.header_plan));

                Training selectedTraining = outdoorAppState.getSelectedTraining();
                phases = selectedTraining.getPhases();

                if (phases.size() == 0) throw new IllegalStateException("At least a goal needed");

                // Warm up, training, cool down, etc...
                String phaseTitle = getString(phases.get(0).getKindName());

                // Initial walk, walk, fast walk, etc...
                String phaseInstruction = getString(phases.get(0).getInstructionName());

                // Time in 00:00 format
                String phaseDuration = DateUtils.millisToMinSec(phases.get(0).getDurationMillis());

                // Time in 0 minutes 0 seconds
                String phaseDurationFormatted = DateUtils.millisToLongTextString(this, phases.get(0).getDurationMillis());
                String phasePaceFormatted = DateUtils.minutesToLongTextString(this, phases.get(0).getPaceMinPerKm());

                txtRoutinePhase.setText(getString(R.string.fase_entrenamiento, phaseTitle));
                txtRoutineInstructions.setText(getString(R.string.instruccion_entrenamiento, phaseInstruction));
                txtRoutinePace.setText(getString(R.string.ritmo_entrenamiento, DateUtils.minutesToMinSec(phases.get(0).getPaceMinPerKm())));
                txtRoutineTime.setText(getString(R.string.training_duration, phaseDuration));
                txtRoutineNumber.setText(getString(R.string.num_entre, 1, phases.size()));

                String sonoText = String.format(Locale.getDefault(), getString(R.string.step_n_of_m_description),
                        1, phases.size(), phaseInstruction, phaseDurationFormatted, phasePaceFormatted);

                Log.i(TAG, "SonoText: " + sonoText);
                ((App) getApplication()).speakOut(sonoText);
                break;
            case QuickStart:
                break;
            default:
                throw new IllegalStateException("No such workout type");
        }
    }

    /**
     * Starts the timer for running
     */
    private void startRunning() {
        // Make sure we only start running once
        if (isRunning) return;
        isRunning = true;

        totalDistanceMeters = 0.0;
        pausedTotalTime = 0;

        // For training plans we keep two sono coaches, one for distances and the other each time the goal is achieved
        switch (workoutType) {
            case Plans:
                nextSonoCoach = phases.get(0).getDurationMillis();
                nextSonoCoach2 = 1000;
                nextSonoCoach3 = phases.get(0).getDurationMillis() / 2;
                break;
            case ImproveYourself:
                nextSonoCoach = 250;
                nextSonoCoach2 = (long) (improveYourselfObjectiveDistanceKm * 1000);
                break;
            case QuickStart:
                nextSonoCoach = 1000;
                break;
        }

        outdoorAppState.getWorkout().setStatus(WorkoutStatus.InProgress);
        outdoorWorkoutsHistoryOperationsDB.update(outdoorAppState.getWorkout().getUpdateContentValues(), outdoorAppState.getWorkout().getId(), this);

        ((App) getApplication()).speakOut(getString(R.string.starting_workout));

        startTime = (new Date()).getTime();

        runnableTimer = new Runnable() {
            public void run() {
                elapsedTimeMilliSeconds = (new Date()).getTime() - startTime - pausedTotalTime;

                txtTimeCounter.setText(DateUtils.millisToMinSec(elapsedTimeMilliSeconds));
                handlerCounterTime.postDelayed(this, 1000);
            }
        };
        handlerCounterTime.postDelayed(runnableTimer, 1);
    }

    /**
     * The user is taking a break from his workout
     */
    private void pauseRoutine() {
        ((App) getApplication()).cancelSpeak();
        ((App) getApplication()).speakOut(getString(R.string.workout_paused));

        // Stop location updates when Activity is no longer active
        stopLocationUpdates();

        // Stop the timer
        handlerCounterTime.removeCallbacks(runnableTimer);

        // Later on, when we resume, we use this to calculate the total pause time
        pauseStartTime = (new Date()).getTime();

        // Build the pause dialog
        final Dialog finishRoutineDialog = new Dialog(this, android.R.style.Theme_Dialog);
        View promptView = View.inflate(this, R.layout.popup_finish_routine, null);

        Button continueButton = promptView.findViewById(R.id.continue_btn);
        Button finishButton = promptView.findViewById(R.id.finish_btn);

        continueButton.setTypeface(Utils.getFontBebasNeue(this));
        finishButton.setTypeface(Utils.getFontBebasNeue(this));

        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((App) getApplication()).speakOut(getString(R.string.cool_lets_continue));
                restartRoutine();
                finishRoutineDialog.dismiss();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishRoutineDialog.dismiss();
                finishRoutine();
            }
        });

        if (finishRoutineDialog.getWindow() != null) {
            finishRoutineDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            finishRoutineDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        finishRoutineDialog.setCancelable(true);
        finishRoutineDialog.setContentView(promptView);
        finishRoutineDialog.show();
    }

    /**
     * The user paused his training and is now resuming it
     */
    private void restartRoutine() {
        // Calculate the total time we were out. Add to other pauses that we might had beforehand.
        pausedTotalTime += (new Date()).getTime() - pauseStartTime;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Restart the location updates
            startLocationUpdates();
        }

        // Restart the main timer
        handlerCounterTime.postDelayed(runnableTimer, 1);
    }

    /**
     * Finish the current workout
     */
    private void finishRoutine() {
        ((App) getApplication()).speakOut(getString(R.string.finished_race));

        final double averageSpeedKmPerHour = 3600.0 * totalDistanceMeters / (double) elapsedTimeMilliSeconds;
        final double averagePaceMinutesPerKm = 60.0 / averageSpeedKmPerHour;

        // A big try...
        try {
            Intent intentResult = new Intent(this, OutdoorResultActivity.class);

            intentResult.putExtra(App.extrasPaceMinPerKm, averagePaceMinutesPerKm);
            intentResult.putExtra(App.extrasSpeedKmPerHour, averageSpeedKmPerHour);
            intentResult.putExtra(App.extrasMaxSpeedKmPerHour, maximumSpeedKmPerHour);
            intentResult.putExtra(App.extrasTotalDistanceKm, totalDistanceMeters / 1000);
            intentResult.putExtra(App.extrasElapsedTimeMilliSeconds, elapsedTimeMilliSeconds);
            intentResult.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, improveYourselfObjectiveDistanceKm);
            intentResult.putExtra(App.extrasImproveYourselfMeasure, improveYourselfMeasure);

            switch (workoutType) {
                case Plans:
                    intentResult.putExtra(App.extrasPlansElapsedTimeMilliSeconds, elapsedTimeMilliSeconds);
                    break;
                case ImproveYourself:
                    intentResult.putExtra(App.extrasImproveYourselfFocus, improveYourselfFocus);
                    intentResult.putExtra(App.extrasImproveYourselfObjectivePace, improveYourselfObjectivePace);
                    intentResult.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, improveYourselfObjectiveTimeMillis);
                    break;
                case QuickStart:
                    break;
                default:
                    throw new IllegalStateException("No such workout type");
            }

            startActivity(intentResult);
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Compare two paces, one comes as a string (WHY??????)
     *
     * @param recommendedPace The recommended pace
     * @param actualPace      The user pace
     * @return A string telling if the user has exceeded or not the recommended pace.
     */
    private String comparePace(Double recommendedPace, Double actualPace) {
        String paceStatus;

        double difference = (recommendedPace - actualPace) / recommendedPace;
        Log.i(TAG, String.format("The difference in actual (%.2f) and recommended (%.2f) pace percentage is: %s", actualPace, recommendedPace, difference));

        if (difference > App.maximumPacePercentageForEquality) {
            paceStatus = getResources().getString(R.string.lest_pace);
        } else if (difference < -App.maximumPacePercentageForEquality) {
            paceStatus = getResources().getString(R.string.more_pace);
        } else {
            paceStatus = getResources().getString(R.string.equal_pace);
        }

        return paceStatus;
    }

    /**
     * Shows a screen containing the training details
     *
     * @param view The view of the control
     */
    @SuppressWarnings("UnusedParameters")
    public void TrainingDetails(View view) {
        Intent intent = new Intent(this, TrainingDetailsActivity.class);
        intent.putExtra(App.extrasCurrentRunningPhase, currentPhaseIndex);
        startActivity(intent);
    }

    /**
     * Called each time we have a new Location. Do something useful with it.
     */
    private void updateDistancePaceAndSpeed() {
        // Obtain a new point from GPS System
        GPSRunPoint point = new GPSRunPoint(outdoorAppState.getWorkout().getId(), mCurrentLocation);

        // Save the new point in the database
        gpsRunPointOperationsDB.addPoint(point, this);

        // If we don't have enough points, initialize the old point.
        if (oldGPSPoint == null) oldGPSPoint = point;

        // For the instantaneous speed and pace.
        final double distanceMeters = oldGPSPoint.distanceTo(point);

        // Calculate time between samples and instant pace and speed
        final long timeMillis = point.getTime().getTime() - oldGPSPoint.getTime().getTime();

        // We didn't move, wait until next point.
        if (distanceMeters == 0.0 || timeMillis == 0) return;

        // Update the total distance counter
        totalDistanceMeters += distanceMeters;

        // Calculate the instant pace (from last sample to current sample)
        final double instantPaceMinPerKm = (double) timeMillis / distanceMeters / 60.0;

        // Calculate the instant speed (from last sample to current sample)
        final double instantSpeedKmPerHour = 60.0 / instantPaceMinPerKm;

        // Obtain the one-minute average pace, this is what we will show to the user
        final double averagePaceMinutesPerKm = gpsRunPointOperationsDB.getRunningWindowPaceOverTime(outdoorAppState.getWorkout().getId(), this, 20000);

        // Obtain the one-minute average speed, this is what we will show to the user
        final double averageSpeedKmPerHour = 60.0 / averagePaceMinutesPerKm;

        // elapsedTimeMilliSeconds is taken from the main routine timer, not based on GPS samples. Does that mean we have little discrepancies?
        final double elapsedTimeSeconds = (double) elapsedTimeMilliSeconds / 1000.0;

        // Ready for next point
        oldGPSPoint = point;

        // Update the maximum speed variable
        if (instantSpeedKmPerHour > maximumSpeedKmPerHour) {
            maximumSpeedKmPerHour = instantSpeedKmPerHour;
            outdoorAppState.getWorkout().setMaximumSpeedKmPerHour(maximumSpeedKmPerHour);
            outdoorWorkoutsHistoryOperationsDB.update(outdoorAppState.getWorkout().getUpdateContentValues(), outdoorAppState.getWorkout().getId(), this);
        }

        Log.i(TAG, String.format("Last sample distance: %.0f m, Total distance: %.0f m, Total time: %.0f s, Instant Pace: %.2f min/km, Average 1min Pace: %.2f min/km, " +
                        "Maximum Speed: %.2f km/h, Average Speed: %.2f km/h, Instant speed: %.2f km/h",
                distanceMeters, totalDistanceMeters, elapsedTimeSeconds, instantPaceMinPerKm, averagePaceMinutesPerKm, maximumSpeedKmPerHour, averageSpeedKmPerHour, instantSpeedKmPerHour));

        txtDistanceCounter.setText(String.format(Locale.getDefault(), "%.2f", totalDistanceMeters / 1000.0));
        txtPaceCounter.setText(getString(R.string.ritmo_entrenamiento_simple, DateUtils.minutesToMinSec(averagePaceMinutesPerKm)));

        // Update tue UI and so on.
        String sonoCoachText = "";
        String sonoCoachText2 = "";
        String sonoCoachText3 = "";

        switch (workoutType) {
            case ImproveYourself:
                // vocal coach for improve yourself
                if (totalDistanceMeters >= nextSonoCoach) {
                    String paceStatus = comparePace(improveYourselfObjectivePace, averagePaceMinutesPerKm);

                    sonoCoachText = getString(R.string.You_ran_x_meters_in_x_minutes_with_pace, (int) nextSonoCoach, DateUtils.minutesToLongTextString(this, 1.0 * elapsedTimeSeconds / 60), DateUtils.minutesToLongTextString(this, averagePaceMinutesPerKm)) + " " + paceStatus;

                    nextSonoCoach += 250.0;
                }

                if (totalDistanceMeters >= improveYourselfObjectiveDistanceKm * 1000 && nextSonoCoach2 != 0.0) {
                    // Dirty hack to make it sound just once
                    sonoCoachText = "";
                    sonoCoachText2 = "";
                    nextSonoCoach2 = 0;

                    ((App) getApplication()).cancelSpeak();
                    ((App) getApplication()).speakOut(getString(R.string.ultimo_paso));

                    finishRoutine();
                }

                break;
            case Plans:
                // For training plans we use three sono coaches, one for each stage and the other for distance
                if (elapsedTimeMilliSeconds >= nextSonoCoach) {
                    currentPhaseIndex++;

                    // We started at 0 in the setup, so the first time we enter here, currentPhaseIndex will hava a value of 1, that is, we already have ran one phase.
                    if (currentPhaseIndex > 0 && currentPhaseIndex < phases.size()) {

                        final Phase currentPhase = phases.get(currentPhaseIndex);

                        // Warm up, training, cool down, etc...
                        final String phaseTitle = getString(currentPhase.getKindName());

                        // Initial walk, walk, fast walk, etc...
                        final String phaseInstruction = getString(currentPhase.getInstructionName());

                        // Time in 00:00 format
                        final String phaseDuration = DateUtils.millisToMinSec(currentPhase.getDurationMillis());

                        // Time in 0 minutes 0 seconds
                        final String phaseDurationFormatted = DateUtils.millisToLongTextString(this, currentPhase.getDurationMillis());
                        final String phasePaceFormatted = DateUtils.minutesToLongTextString(this, currentPhase.getPaceMinPerKm());

                        txtRoutinePhase.setText(getString(R.string.fase_entrenamiento, phaseTitle));
                        txtRoutineInstructions.setText(getString(R.string.instruccion_entrenamiento, phaseInstruction));
                        txtRoutinePace.setText(getString(R.string.ritmo_entrenamiento, DateUtils.minutesToMinSec(currentPhase.getPaceMinPerKm())));
                        txtRoutineTime.setText(getString(R.string.training_duration, phaseDuration));
                        txtRoutineNumber.setText(getString(R.string.num_entre, currentPhaseIndex + 1, phases.size()));

                        sonoCoachText2 = getString(R.string.step_n_of_m_description,
                                currentPhaseIndex + 1, phases.size(), phaseInstruction, phaseDurationFormatted, phasePaceFormatted);

                        final long duration = currentPhase.getDurationMillis();

                        nextSonoCoach += duration;
                        nextSonoCoach3 = elapsedTimeMilliSeconds + duration / 2;
                    } else if (currentPhaseIndex == phases.size()) {
                        // Dirty hack to make it sound just once
                        //sonoCoachText = "";
                        //sonoCoachText2 = "";
                        //sonoCoachText3 = "";
                        nextSonoCoach2 = 0;
                        nextSonoCoach3 = 0;

                        ((App) getApplication()).cancelSpeak();
                        ((App) getApplication()).speakOut(getString(R.string.ultimo_paso));

                        finishRoutine();
                    }
                }

                if (nextSonoCoach2 > 0 && totalDistanceMeters >= nextSonoCoach2) {
                    final double runningWindowPaceOverDistance = gpsRunPointOperationsDB.getRunningWindowPaceOverDistance(outdoorAppState.getWorkout().getId(), this, 1000);
                    sonoCoachText = getString(R.string.pace_one_km, DateUtils.minutesToLongTextString(this, runningWindowPaceOverDistance));
                    nextSonoCoach2 += 1000.0;
                }

                if (nextSonoCoach3 > 0 && elapsedTimeMilliSeconds >= nextSonoCoach3) {
                    final Phase currentPhase = phases.get(currentPhaseIndex);
                    sonoCoachText3 = getString(R.string.your_pace_and_time,
                            DateUtils.minutesToLongTextString(this, averagePaceMinutesPerKm),
                            DateUtils.millisToLongTextString(this, currentPhase.getDurationMillis() / 2));
                    nextSonoCoach3 = 0;
                }
                break;
            case QuickStart:
                // coach vocal for quick start
                if (totalDistanceMeters >= nextSonoCoach) {
                    final double runningWindowPaceOverDistance = gpsRunPointOperationsDB.getRunningWindowPaceOverDistance(outdoorAppState.getWorkout().getId(), this, 1000);
                    sonoCoachText = getString(R.string.pace_one_km, DateUtils.minutesToLongTextString(this, runningWindowPaceOverDistance));
                    nextSonoCoach += 1000;
                }
                break;
            default:
                throw new IllegalStateException("No such workout type");
        }

        if (!sonoCoachText.isEmpty()) {
            Log.i(TAG, "Sono Couch Alert 1: " + sonoCoachText);
            ((App) getApplication()).speakOut(sonoCoachText);
        }

        if (!sonoCoachText2.isEmpty()) {
            Log.i(TAG, "Sono Couch Alert 2: " + sonoCoachText2);
            ((App) getApplication()).speakOut(sonoCoachText2);
        }

        if (!sonoCoachText3.isEmpty()) {
            Log.i(TAG, "Sono Couch Alert 3: " + sonoCoachText3);
            ((App) getApplication()).speakOut(sonoCoachText3);
        }

    }
    // endregion
}
