package com.runin.runinapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.runin.runinapp.indoor.SelectLevelActivity;
import com.runin.runinapp.outdoor.OutdoorSelectPlanActivity;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Select place to run outdoor or indoor
 * Created by Cesar on 09/08/2016.
 */
public class SelectIndoorOutdoorActivity extends AppCompatActivity {
//    private static final String TAG = SelectIndoorOutdoorActivity.class.getSimpleName();

    @BindView(R.id.runner_title_one)
    TextView runnerTitleOne;

    @BindView(R.id.runner_title_two)
    TextView runnerTitleTwo;

    @BindView(R.id.title_place_one)
    TextView placeTitleOne;

    @BindView(R.id.title_place_two)
    TextView placeTitleTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_indoor_outdoor);

        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        runnerTitleOne.setTypeface(Utils.getFontChunk(this));
        runnerTitleTwo.setTypeface(Utils.getFontChunk(this));

        placeTitleOne.setTypeface(Utils.getFontBebasNeue(this));
        placeTitleTwo.setTypeface(Utils.getFontBebasNeue(this));
    }

    @SuppressWarnings("UnusedParameters")
    public void selectOutdoor(View view) {
        //Save the status of the tutorial
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeOutdoor);
        preferencesEditor.apply();

        Intent intentOutdoor = new Intent(this, OutdoorSelectPlanActivity.class);
        startActivity(intentOutdoor);
        SelectIndoorOutdoorActivity.this.finish();
    }

    @SuppressWarnings("UnusedParameters")
    public void selectIndoor(View v) {
        //Save the status of the tutorial
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeIndoor);
        preferencesEditor.apply();

        Intent intentOutdoor = new Intent(this, SelectLevelActivity.class);
        startActivity(intentOutdoor);
        SelectIndoorOutdoorActivity.this.finish();
    }
}
