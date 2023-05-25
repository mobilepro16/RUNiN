package com.runin.runinapp.outdoor.improve;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.MusicSelectPlayer;
import com.runin.runinapp.outdoor.OutdoorRunningActivity;
import com.runin.runinapp.utils.RecyclerItemData;
import com.runin.runinapp.utils.RecyclerMusicClickListener;
import com.runin.runinapp.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 31/07/2017.
 */
public class PreRunImproveActivity extends AppCompatActivity {
    private static final String TAG = PreRunImproveActivity.class.getSimpleName();

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

    @BindView(R.id.button_start)
    Button btnStart;

    @BindView(R.id.title_improve)
    TextView title_improve;

    @BindView(R.id.subtitle_improve)
    TextView subtitle_improve;

    @BindView(R.id.layoutObtainingLocation)
    LinearLayout layoutObtainingLocation;

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
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private String measure;
    private long objectiveTimeMilis;
    private String focus;
    private double recommendedPace;
    private double objectiveDistanceKm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_run_improve);
        ButterKnife.bind(this);

        title_improve.setTypeface(Utils.getFontBebasNeue(this));
        subtitle_improve.setTypeface(Utils.getFontBebasNeue(this));
        btnStart.setTypeface(Utils.getFontBebasNeue(this));

        if (getIntent().getExtras() != null) {
            objectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);
            measure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure);
            objectiveTimeMilis = getIntent().getExtras().getLong(App.extrasImproveYourselfObjectiveTimeMillis);
            focus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
            recommendedPace = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectivePace);

            Log.i(TAG, String.format("Distance: %.3f, Measure: %s, Duration: %d, Focus: %s, Pace: %.1f", objectiveDistanceKm, measure, objectiveTimeMilis, focus, recommendedPace));
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_player_options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerItemData itemData[] = {new RecyclerItemData(getString(R.string.musicSelect)),
        };

        MusicSelectPlayer musicSelectPlayerAdapter = new MusicSelectPlayer(itemData);
        recyclerView.setAdapter(musicSelectPlayerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerMusicClickListener(this, new RecyclerMusicClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        @SuppressWarnings("deprecation") Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                        startActivity(intent);
                        break;
                }
            }
        }));


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishIntent();
            }
        });

        // Initialize the location service
        initializeLocation();

        btnStart.setEnabled(false);
        btnStart.setBackgroundColor(Utils.getColor(this, R.color.gray));
        layoutObtainingLocation.setVisibility(View.VISIBLE);
    }

    private void finishIntent() {

        Intent runningActivity = new Intent(this, OutdoorRunningActivity.class);
        runningActivity.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
        runningActivity.putExtra(App.extrasImproveYourselfMeasure, measure);
        runningActivity.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, objectiveTimeMilis);
        runningActivity.putExtra(App.extrasImproveYourselfFocus, focus);
        runningActivity.putExtra(App.extrasImproveYourselfObjectivePace, recommendedPace);

        startActivity(runningActivity);

        stopLocationUpdates();
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
                    }
                    else {
                        // Permission was denied. Display an error message.
                        Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    // Permission was denied. Display an error message.
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

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
            }
            else {
                requestLocationPermissions();
            }
        }
        else {
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
                            ActivityCompat.requestPermissions(PreRunImproveActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE);
                        }
                    }).show();
        }
        else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(PreRunImproveActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION_REQUEST_CODE);
        }
    }

    /**
     * Main location method. What to do when we get a new position of the user.
     *
     * @param locationResult The result of the periodic location service
     */
    private void gotNewLocation(LocationResult locationResult) {
        // The last location point obtained
        Location mCurrentLocation = locationResult.getLastLocation();

        // The maximum difference in milliseconds from GPS samples. Anything larger than this will be discarded
        final long maxSamplesDifference = BuildConfig.DEBUG && Utils.isEmulator() ? 150000 : 25000;
        final int maxAccuracy = BuildConfig.DEBUG && Utils.isEmulator() ? 25 : 12;

        ///////////////////// ERASE ME //////////////////////////
        if (BuildConfig.DEBUG && Utils.isEmulator()) {
            mCurrentLocation.setSpeed(1.5f);
            mCurrentLocation.setTime((new Date()).getTime());
        }

        ////////////////////////////////////////////////////////

        // Not all the samples from GPS are going to be used.
        if (!mCurrentLocation.hasAccuracy() || // GPS didn't attach yet
                !mCurrentLocation.hasSpeed() || // GPS didn't attach yet
                mCurrentLocation.getAccuracy() > maxAccuracy || // The sample is not accurate enough (m)
                mCurrentLocation.getSpeed() > 14 || // Speed is too large (m/s)
                mCurrentLocation.getTime() < (new Date()).getTime() - maxSamplesDifference) {
            Log.e(TAG, String.format("Location discarded. Accuracy: %.0f m, Speed: %.1f km/h, Time: %.1f s",
                    mCurrentLocation.getAccuracy(), mCurrentLocation.getSpeed() * 3.6, (1.0 * (new Date()).getTime() - mCurrentLocation.getTime()) / 1000));
            return;
        }

        // Do something with the new location
        Log.w(TAG, String.format("We got a good starting point for GPS.  Accuracy: %.0f m, Speed: %.1f km/h, Time: %.1f s",
                mCurrentLocation.getAccuracy(), mCurrentLocation.getSpeed() * 3.6, (1.0 * (new Date()).getTime() - mCurrentLocation.getTime()) / 1000));

        if (!btnStart.isEnabled()) {
            btnStart.setEnabled(true);
            btnStart.setBackgroundColor(Utils.getColor(this, R.color.orange));
        }

        if (layoutObtainingLocation.getVisibility() == View.VISIBLE) {
            layoutObtainingLocation.setVisibility(View.GONE);
        }

        // Just don't do nothing. We just "warmed up" the GPS for the next screen to be faster in acquiring the location
        //stopLocationUpdates();
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

                        if (ActivityCompat.checkSelfPermission(PreRunImproveActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                    rae.startResolutionForResult(PreRunImproveActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.", sie);
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Toast.makeText(PreRunImproveActivity.this, R.string.location_setting_inadequate_fix_them, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Do whatever you need to do here, like notifying the user.
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                Log.w(TAG, "Location permissions check ok now, can continue");
                startLocationUpdates();
            }
            else {
                Log.e(TAG, "Location permissions check are still bad, can't continue");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
