package com.runin.runinapp.outdoor.improve;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.outdoor.OutdoorDashboardActivity;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 23/07/2017.
 */
public class YourPaceActivity extends AppCompatActivity {

    @BindView(R.id.distance_run)
    TextView distance_run;

    @BindView(R.id.time_run)
    TextView time_run;

    @BindView(R.id.run_improve_btn)
    Button run_improve_btn;

    @BindView(R.id.return_btn)
    Button return_btn;

    @BindView(R.id.screen_title)
    TextView txtScreenTitle;

    @BindView(R.id.recoment)
    TextView recommended;

    @BindView(R.id.img_enfoque)
    ImageView imgFocus;

    private String screen;
    private double objectiveDistanceKm;
    private String measure;
    private long objectiveDurationMillis;
    private String focus;
    private double recommendedPace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_pace);
        ButterKnife.bind(this);

        txtScreenTitle.setTypeface(Utils.getFontBebasNeue(this));
        run_improve_btn.setTypeface(Utils.getFontBebasNeue(this), Typeface.ITALIC);
        recommended.setTypeface(Utils.getFontBebasNeue(this));
        return_btn.setTypeface(Utils.getFontBebasNeue(this));

        if (getIntent().getExtras() != null) {
            screen = getIntent().getExtras().getString(App.extrasScreenName);
            objectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);
            measure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure);
            objectiveDurationMillis = getIntent().getExtras().getLong(App.extrasImproveYourselfObjectiveTimeMillis);
            focus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
        }

        time_run.setText(String.format(getString(R.string.n_minutos), DateUtils.durationToMinSec(objectiveDurationMillis)));

        switch (measure) {
            case App.measureKilometers:
                distance_run.setText(getString(R.string.n_km, objectiveDistanceKm));
                break;
            case App.measureMeters:
                distance_run.setText(getString(R.string.n_meters, objectiveDistanceKm * 1000.0));
                break;
            default:
                throw new IllegalStateException("Invalid measure");
        }

        recommendedPace = (double) objectiveDurationMillis / objectiveDistanceKm / 60000.0;

        recommended.setText(DateUtils.minutesToMinSec(recommendedPace));

        run_improve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(YourPaceActivity.this, OutdoorDashboardActivity.class);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentMain.putExtra(App.extrasScreenName, screen);
                intentMain.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
                intentMain.putExtra(App.extrasImproveYourselfMeasure, measure);
                intentMain.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, objectiveDurationMillis);
                intentMain.putExtra(App.extrasImproveYourselfFocus, focus);
                intentMain.putExtra(App.extrasImproveYourselfObjectivePace, recommendedPace);
                startActivity(intentMain);
                YourPaceActivity.this.finish();
            }
        });

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(YourPaceActivity.this, ImproveDefineDistanceActivity.class);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentMain.putExtra(App.extrasImproveYourselfFocus, focus);
                startActivity(intentMain);
                YourPaceActivity.this.finish();
            }
        });
    }
}
