package com.runin.runinapp.outdoor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.BaseActivity;
import com.runin.runinapp.FacebookRuninActivity;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.User;
import com.runin.runinapp.indoor.BadgesFragment;
import com.runin.runinapp.indoor.IndoorDashboardActivity;
import com.runin.runinapp.indoor.SelectLevelActivity;
import com.runin.runinapp.outdoor.improve.ImproveDefineFocusActivity;
import com.runin.runinapp.outdoor.improve.MainImproveFragment;
import com.runin.runinapp.outdoor.plans.PlansListFragment;
import com.runin.runinapp.outdoor.plans.PlansMainFragment;
import com.runin.runinapp.outdoor.quickstart.QuickStartFragment;
import com.runin.runinapp.settings.DashboardSettingsFragment;
import com.runin.runinapp.settings.ProfileActivity;
import com.runin.runinapp.utils.ExtensionsKt;
import com.runin.runinapp.utils.Utils;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Main Activity to Quick Start
 * Created by Cesar on 09/08/2016
 */
public class OutdoorDashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = OutdoorDashboardActivity.class.getSimpleName();
    private NavigationView navigationView;
    private String screen = null;
    private Fragment fragment;

    @Inject
    User userProfile;

    @Inject
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_outdoor_dashboard);
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

        double objectiveDistanceKm = 0.0;
        long objectiveDurationMillis = 0;
        double recommendedPace = 0.0;
        String measure = "";
        String focus = "";

        if (getIntent().getExtras() != null) {
            screen = getIntent().getExtras().getString(App.extrasScreenName);
            objectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm, 0.0);
            measure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure, "");
            objectiveDurationMillis = getIntent().getExtras().getLong(App.extrasImproveYourselfObjectiveTimeMillis, 0);
            focus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus, "");
            recommendedPace = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectivePace, 0.0);

            Log.i(TAG, String.format("Distance: %.1f km, Measure: %s, Duration: %d, Focus: %s, Pace: %.1f", objectiveDistanceKm, measure, objectiveDurationMillis, focus, recommendedPace));
        }

        OutdoorAppState outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        switch (screen) {
            case App.screenNameQuick:
                outdoorAppState.clear();
                outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.QuickStart);
                Log.i(TAG, "Outdoor quickstart selected");

                fragment = new QuickStartFragment();
                break;
            case App.screenNameSettings:
                fragment = new DashboardSettingsFragment();
                break;
            case App.screenNamePlansMain:
                fragment = new PlansMainFragment();
                break;
            case App.screenNameBadges:
                fragment = new BadgesFragment();
                break;
            case App.screenNamePlans:
                outdoorAppState.clear();
                outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.Plans);
                Log.i(TAG, "Outdoor plans selected");

                fragment = new PlansListFragment();
                break;
            case App.screenNameImprove:
                fragment = new MainImproveFragment();
                Bundle args = new Bundle();

                args.putDouble(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
                args.putString(App.extrasImproveYourselfMeasure, measure);
                args.putLong(App.extrasImproveYourselfObjectiveTimeMillis, objectiveDurationMillis);
                args.putString(App.extrasImproveYourselfFocus, focus);
                args.putDouble(App.extrasImproveYourselfObjectivePace, recommendedPace);

                fragment.setArguments(args);
                break;
            default:
                throw new IllegalStateException("No such screen");
        }

        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        View header = navigationView.getHeaderView(0);
        TextView name_user = header.findViewById(R.id.name_user);

        name_user.setText(Utils.fromHtml(String.format(Locale.getDefault(), "<b>%s</b>", userProfile.getName())));

        String ageDrawer = String.format(Locale.getDefault(), "%s: <b>%d %s</b>", getResources().getString(R.string.age), userProfile.getAge(), getResources().getString(R.string.years));
        ((TextView) header.findViewById(R.id.age_user)).setText(Utils.fromHtml(ageDrawer));

        String weightDrawer = String.format(Locale.getDefault(), "%s: <b>%.0f kg</b>", getResources().getString(R.string.weight_placeholder), userProfile.getWeight());
        ((TextView) header.findViewById(R.id.weight_user)).setText(Utils.fromHtml(weightDrawer));

        switch (screen) {
            case App.screenNameQuick:
                findViewById(R.id.main_layout).setBackgroundColor(Utils.getColor(this, R.color.colorQS));
                findViewById(R.id.toolbar).setBackgroundResource(R.mipmap.header);
                findViewById(R.id.view2).setBackgroundColor(Utils.getColor(this, R.color.colorGradientDrawer2));
                break;
            case App.screenNameSettings:
                findViewById(R.id.main_layout).setBackgroundColor(Utils.getColor(this, R.color.colorQS));
                findViewById(R.id.toolbar).setBackgroundResource(R.mipmap.header);
                findViewById(R.id.view2).setBackgroundColor(Utils.getColor(this, R.color.colorGradientDrawer2));
                ((ImageView) header.findViewById(R.id.quick_start_image)).setImageResource(R.mipmap.quick_start);
                break;
            case App.screenNameImprove:
                findViewById(R.id.main_layout).setBackgroundColor(Utils.getColor(this, R.color.improve_middle));
                findViewById(R.id.toolbar).setBackgroundResource(R.mipmap.header_improve);
                findViewById(R.id.view2).setBackgroundColor(Utils.getColor(this, R.color.colorGradientDrawer2));
                break;
            case App.screenNamePlans:
            case App.screenNamePlansMain:
                findViewById(R.id.main_layout).setBackground(Utils.getDrawable(this, R.drawable.side_nav_bar_plans));
                findViewById(R.id.toolbar).setBackgroundResource(R.mipmap.header_plan);
                findViewById(R.id.view2).setBackgroundColor(Utils.getColor(this, R.color.colorRoseCalories));
                ((ImageView) header.findViewById(R.id.quick_start_image)).setImageResource(R.mipmap.planes);
        }
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            super.onBackPressed();
        }
    }

    private boolean closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            closeDrawer();
        } else if (id == R.id.nav_profile) {
            Intent intentProfile = new Intent(this, ProfileActivity.class);
            intentProfile.putExtra(App.extrasEdit, true);
            startActivity(intentProfile);
        } else if (id == R.id.nav_settings) {
            if (screen.equals(App.screenNameSettings)) {
                closeDrawer();
            } else {
                Intent intentSettings = new Intent(this, OutdoorDashboardActivity.class);
                intentSettings.putExtra(App.extrasScreenName, App.screenNameSettings);
                startActivity(intentSettings);
            }
        } else if (id == R.id.comm_perfil) {
            Intent intentSettings = new Intent(this, FacebookRuninActivity.class);
            startActivity(intentSettings);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("UnusedParameters")
    public void onOutDoorClick(View view) {
        ExtensionsKt.putString(sharedPref, App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeOutdoor);
        Intent intentOutdoor = new Intent(this, OutdoorSelectPlanActivity.class);
        startActivity(intentOutdoor);
    }

    @SuppressWarnings("UnusedParameters")
    public void onInDoorClick(View view) {
        ExtensionsKt.putString(sharedPref, App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeIndoor);
        IndoorAppState indoorAppState = ((App) getApplication()).getIndoorAppState();
        if (indoorAppState.getIndoorLevel() == null) {
            Intent intent = new Intent(this, SelectLevelActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, IndoorDashboardActivity.class);
            intent.putExtra(App.extrasScreenName, App.screenNameTracks);
            startActivity(intent);
        }
    }

    /**
     * Opens the Improve Yourself Activity
     *
     * @param view the calling view
     */
    @SuppressWarnings("UnusedParameters")
    public void improveP(View view) {
        if (screen.equals(App.screenNameImprove)) {
            closeDrawer();
        } else {
            Intent intentMain = new Intent(this, ImproveDefineFocusActivity.class);
            intentMain.putExtra(App.extrasScreenName, App.screenNameImprove);
            startActivity(intentMain);
            finish();
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void onQuickClick(View view) {
        if (screen.equals(App.screenNameQuick)) {
            closeDrawer();
        } else {
            ExtensionsKt.putString(sharedPref, App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeQuickstart);
            Intent intentMain = new Intent(this, OutdoorDashboardActivity.class);
            intentMain.putExtra(App.extrasScreenName, App.screenNameQuick);
            startActivity(intentMain);
            finish();
        }
    }

    /**
     * Opens the Training Plans Activity
     *
     * @param view the calling view
     */
    @SuppressWarnings("UnusedParameters")
    public void plansP(View view) {
        if (screen.equals(App.screenNamePlans)) {
            closeDrawer();
        } else {
            Intent intentSettings = new Intent(this, OutdoorDashboardActivity.class);
            intentSettings.putExtra(App.extrasScreenName, App.screenNamePlans);
            startActivity(intentSettings);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The fragment needs to process the result of the purchase
        if (fragment instanceof PlansMainFragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}