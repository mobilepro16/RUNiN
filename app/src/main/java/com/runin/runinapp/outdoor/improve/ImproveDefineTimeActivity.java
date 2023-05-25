package com.runin.runinapp.outdoor.improve;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 27/06/2017.
 */
public class ImproveDefineTimeActivity extends AppCompatActivity {
    @BindView(R.id.screen_title)
    TextView txtScreenTitle;

    @BindView(R.id.run_improve_btn)
    Button buttonRunImprove;

    @BindView(R.id.title_alert)
    TextView txtAlertTitle;

    @BindView(R.id.desc_alert)
    TextView txtAlertDescription;

    @BindView(R.id.editTextTime)
    EditText editTextTime;

    private String screen;
    private double objectiveDistanceKm;
    private long objectiveTimeMillis = 0;
    private String measure;
    private String focus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improve_alert);
        ButterKnife.bind(this);

        txtScreenTitle.setTypeface(Utils.getFontBebasNeue(this));
        buttonRunImprove.setTypeface(Utils.getFontBebasNeue(this));
        editTextTime.setTypeface(Utils.getFontBebasNeue(this));

        if (getIntent().getExtras() != null) {
            screen = getIntent().getExtras().getString(App.extrasScreenName);
            objectiveDistanceKm = getIntent().getExtras().getDouble(App.extrasImproveYourselfObjectiveDistanceKm);
            measure = getIntent().getExtras().getString(App.extrasImproveYourselfMeasure);
            focus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
        }

        txtAlertTitle.setText(this.getResources().getString(R.string.time));
        txtAlertDescription.setText(this.getResources().getString(R.string.alert_select_time));

        buttonRunImprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    objectiveTimeMillis = (long) (Double.parseDouble(editTextTime.getText().toString()) * 60000.0);
                } catch (NumberFormatException e) {
                    // User didn't fill the field
                    objectiveTimeMillis = 0;
                }

                if (objectiveTimeMillis == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_the_fields), Toast.LENGTH_LONG).show();
                }
                // No less than one minute and no more than 4 hours
                else if (objectiveTimeMillis < 60 * 1000 || objectiveTimeMillis > 4 * 60 * 60 * 1000) {
                    Toast.makeText(getApplicationContext(), R.string.incorrect_value, Toast.LENGTH_LONG).show();
                }
                else {

                    Intent intentMain = new Intent(ImproveDefineTimeActivity.this, YourPaceActivity.class);
                    intentMain.putExtra(App.extrasScreenName, screen);
                    intentMain.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
                    intentMain.putExtra(App.extrasImproveYourselfMeasure, measure);
                    intentMain.putExtra(App.extrasImproveYourselfObjectiveTimeMillis, objectiveTimeMillis);
                    intentMain.putExtra(App.extrasImproveYourselfFocus, focus);
                    startActivity(intentMain);
                    ImproveDefineTimeActivity.this.finish();
                }
            }
        });
    }
}