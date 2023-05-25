package com.runin.runinapp.outdoor.improve;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Usuario on 08/06/2017.
 */
public class ImproveDefineDistanceActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = ImproveDefineDistanceActivity.class.getSimpleName();

    @BindView(R.id.screen_title)
    TextView txtScreenTitle;

    @BindView(R.id.run_improve_btn)
    Button buttonRunImprove;

    @BindView(R.id.spinnerUnits)
    Spinner spinnerUnits;

    @BindView(R.id.editTextDistance)
    EditText editTextDistance;

    @BindView(R.id.cuestion_title)
    TextView txtQuestionTitle;

    @BindView(R.id.desc_alert)
    TextView txtAlertDescription;

    private String measure = "--";
    private String focus;

    private int selectedUnits = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_improve);
        ButterKnife.bind(this);

        txtScreenTitle.setTypeface(Utils.getFontBebasNeue(this));
        buttonRunImprove.setTypeface(Utils.getFontBebasNeue(this));
        editTextDistance.setTypeface(Utils.getFontBebasNeue(this));
        if (getIntent().getExtras() != null) {
            focus = getIntent().getExtras().getString(App.extrasImproveYourselfFocus);
        }
        Log.e(TAG, String.format("Focus: %s", focus));


        if (focus.equals(App.focusDistance)) {
            ArrayAdapter<CharSequence> adapterUnits = ArrayAdapter.createFromResource(this, R.array.unitsDistance, R.layout.spinner_view_item);
            adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnits.setAdapter(adapterUnits);

            spinnerUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedUnits = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        else if (focus.equals(App.focusCalories)) {
            spinnerUnits.setVisibility(View.GONE);
            txtQuestionTitle.setText(getResources().getString(R.string.cuestion_calories));
            txtAlertDescription.setText(getResources().getString(R.string.desc_calories));
        }

        buttonRunImprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double value;
                double objectiveDistanceKm = 0.0;

                try {
                    value = Double.parseDouble(editTextDistance.getText().toString());
                } catch (NumberFormatException e) {
                    // User didn't fill the field
                    value = 0.0;
                }

                if (focus.equals(App.focusDistance)) {
                    if (selectedUnits == 0) {
                        measure = App.measureMeters;
                        objectiveDistanceKm = value / 1000;
                    }
                    else if (selectedUnits == 1) {
                        measure = App.measureKilometers;
                        objectiveDistanceKm = value;
                    }
                }

                if (value == 0.0) {
                    Toast.makeText(getApplicationContext(), R.string.fill_all_the_fields, Toast.LENGTH_LONG).show();
                }
                else if (value < 0.0) {
                    Toast.makeText(getApplicationContext(), R.string.incorrect_value, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intentMain = new Intent(ImproveDefineDistanceActivity.this, ImproveDefineTimeActivity.class);
                    intentMain.putExtra(App.extrasScreenName, App.screenNameImprove);
                    intentMain.putExtra(App.extrasImproveYourselfObjectiveDistanceKm, objectiveDistanceKm);
                    intentMain.putExtra(App.extrasImproveYourselfMeasure, measure);
                    intentMain.putExtra(App.extrasImproveYourselfFocus, focus);
                    startActivity(intentMain);
                    ImproveDefineDistanceActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onInit(int i) {
    }
}