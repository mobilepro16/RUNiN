package com.runin.runinapp.indoor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.runin.runinapp.FacebookRuninActivity;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.data.User;
import com.runin.runinapp.outdoor.OutdoorDashboardActivity;
import com.runin.runinapp.outdoor.OutdoorSelectPlanActivity;
import com.runin.runinapp.outdoor.improve.ImproveDefineFocusActivity;
import com.runin.runinapp.settings.ProfileActivity;
import com.runin.runinapp.utils.Utils;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class IndoorDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Main Activity to Quick Start
     * Created by Cesar on 09/08/2016
     */
    private final static String TAG = IndoorDashboardActivity.class.getSimpleName();
    private NavigationView navigationView;
    private SelectTrackFragment selectTrackFragment;
    private IndoorAppState indoorAppState;
    @Inject
    User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        indoorAppState = ((App) this.getApplication()).getIndoorAppState();
        setContentView(R.layout.layout_selecttrackactivity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ButterKnife.bind(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        selectTrackFragment = new SelectTrackFragment();

        // Start the requested fragment
        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, selectTrackFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        View header = navigationView.getHeaderView(0);
        TextView name_user = header.findViewById(R.id.name_user);

        name_user.setText(Utils.fromHtml(String.format("<b>%s</b>", userProfile.getName())));

        String ageDrawer = String.format(Locale.getDefault(), getString(R.string.drawer_years), getResources().getString(R.string.age), userProfile.getAge());
        ((TextView) header.findViewById(R.id.age_user)).setText(Utils.fromHtml(ageDrawer));

        String weightDrawer = String.format(Locale.getDefault(), "%s: <b>%.0f kg</b>", getResources().getString(R.string.weight_placeholder), userProfile.getWeight());
        ((TextView) header.findViewById(R.id.weight_user)).setText(Utils.fromHtml(weightDrawer));
        String levelDrawer = "";

        if (indoorAppState.getIndoorLevel() != null) {
            levelDrawer = getResources().getString(indoorAppState.getIndoorLevel().getTitleId());
        }

        levelDrawer = getResources().getString(R.string.level) + ": <b> " + levelDrawer + "</b>";

        ((TextView) header.findViewById(R.id.user_level)).setText(Utils.fromHtml(levelDrawer));

        selectTrackFragment.updateData(userProfile);
    }

    /**
     * Close the Menu drawer if it is open, otherwise, do the regular back
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            cancelDownloads(new MyCallback() {
                @Override
                public void onFinish() {
                    IndoorDashboardActivity.super.onBackPressed();
                }
            });
        }
    }

    /**
     * Executes the given callback but first asks the user to cancel any download that is in progress
     *
     * @param callback the callback
     */
    private void cancelDownloads(final MyCallback callback) {

        // Cancel downloads.
        boolean downloading = false;

        for (Track track : indoorAppState.getIndoorTracks()) {
            if (track.isDownloading()) {
                downloading = true;
            }
        }

        if (downloading) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.downloadsInProgressCancel).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (Track track : indoorAppState.getIndoorTracks()) {
                        if (track.isDownloading()) {
                            track.cancelDownload();
                        }
                    }

                    callback.onFinish();
                }
            }).setNegativeButton(R.string.cancel, null).show();
        } else {
            callback.onFinish();
        }
    }

    /**
     * Go to the Pre-Run Activity
     */
    public void startTrack() {
        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentOutdoor = new Intent(IndoorDashboardActivity.this, PreIndoorActivity.class);
                startActivity(intentOutdoor);
            }
        });
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu The menu
     * @return always true (wtf!)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here.
     *
     * @param item the menu item
     * @return (please explain)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        final Intent intent;

        if (id == R.id.nav_dashboard) {
            intent = null;
        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(App.extrasEdit, true);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, OutdoorDashboardActivity.class);
            intent.putExtra(App.extrasScreenName, App.screenNameSettings);
        } else if (id == R.id.comm_perfil) {
            intent = new Intent(this, FacebookRuninActivity.class);
        } else if (id == R.id.badges_nav) {
            intent = new Intent(this, IndoorBadgesActivity.class);
        } else {
            intent = null;
        }

        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("UnusedParameters")
    public void onOutDoorClick(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeOutdoor);
        editor.apply();

        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentOutdoor = new Intent(IndoorDashboardActivity.this, OutdoorSelectPlanActivity.class);
                startActivity(intentOutdoor);
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    public void onQuickClick(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeQuickstart);
        editor.apply();

        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentMain = new Intent(IndoorDashboardActivity.this, OutdoorDashboardActivity.class);
                intentMain.putExtra(App.extrasScreenName, App.screenNameQuick);
                startActivity(intentMain);
            }
        });
    }

    /**
     * Process the activity result if needed.
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The fragment needs to process the result of the purchase
        selectTrackFragment.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Opens the Training Plans Activity
     *
     * @param view the calling view
     */
    @SuppressWarnings("UnusedParameters")
    public void plansP(View view) {
        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentSettings = new Intent(IndoorDashboardActivity.this, OutdoorDashboardActivity.class);
                intentSettings.putExtra(App.extrasScreenName, App.screenNamePlans);
                refresh();
                startActivity(intentSettings);

            }
        });
    }

    /**
     * Opens the Improve Yourself Activity
     *
     * @param view the calling view
     */
    @SuppressWarnings("UnusedParameters")
    public void improveP(View view) {
        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentMain = new Intent(IndoorDashboardActivity.this, ImproveDefineFocusActivity.class);
                refresh();
                startActivity(intentMain);
            }
        });
    }

    public void showAllBadges() {
        cancelDownloads(new MyCallback() {
            @Override
            public void onFinish() {
                Intent intentBadges = new Intent(IndoorDashboardActivity.this, IndoorBadgesActivity.class);
                startActivity(intentBadges);
            }
        });
    }

    /**
     * The activity is being destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (Track track : indoorAppState.getIndoorTracks()) {
            if (track.isDownloading()) {
                Log.e(TAG, "Stopping download of " + track.getId());
                track.cancelDownload();
            }
        }
    }

    private void refresh() {
        finish();
        startActivity(getIntent());
    }

    private interface MyCallback {
        void onFinish();
    }
}