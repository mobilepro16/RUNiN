package com.runin.runinapp.outdoor.plans;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Phase;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:
 * Created by Usuario on 11/07/2017.
 */

public class TrainingDetailsActivity extends AppCompatActivity {
    private static final String TAG = TrainingDetailsActivity.class.getSimpleName();

    private int currentRunningStage = -1;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.plan_image)
    ImageView plan_image;

    @BindView(R.id.TablePlans)
    TableLayout phasesTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details_day);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            currentRunningStage = getIntent().getExtras().getInt(App.extrasCurrentRunningPhase, -1);
        }

        OutdoorAppState outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        Stage selectedStage = outdoorAppState.getSelectedStage();
        Training selectedTraining = outdoorAppState.getSelectedTraining();

        title.setTypeface(Utils.getFontBebasNeue(this));
        title.setText(String.format(getString(R.string.state_n_trainint_n), selectedStage.getSequence() + 1, selectedTraining.getSequence() + 1));

        TrainingGraphFragment training_plan_fragment = (TrainingGraphFragment) getSupportFragmentManager().findFragmentById(R.id.training_plan_fragment);

        plan_image.setImageResource(outdoorAppState.getPlanIcon(outdoorAppState.getSelectedPlanName()));

        training_plan_fragment.setPhases(selectedTraining.getPhases());

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
            txtTime.setText(DateUtils.durationToMinSec(phase.getDurationMillis()));

            if (currentRunningStage >= 0) {
                if (i == currentRunningStage) {
                    Log.i(TAG, "The stage is the current one");
                    imgStatusGray.setVisibility(View.GONE);
                    imgStatusGreen.setVisibility(View.GONE);
                    imgStatusYellow.setVisibility(View.VISIBLE);
                    imgStatusRed.setVisibility(View.GONE);
                }
                else {
                    switch (outdoorAppState.getStageFinishedStatus(i)) {
                        case Finished:
                            Log.i(TAG, "The stage is finished");
                            imgStatusGray.setVisibility(View.GONE);
                            imgStatusGreen.setVisibility(View.VISIBLE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.GONE);
                            break;
                        case NotFinished:
                            Log.i(TAG, "The stage is not finished");
                            imgStatusGray.setVisibility(View.VISIBLE);
                            imgStatusGreen.setVisibility(View.GONE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.GONE);
                            break;
                        case NotStarted:
                            Log.i(TAG, "The stage is not started");
                            imgStatusGray.setVisibility(View.VISIBLE);
                            imgStatusGreen.setVisibility(View.GONE);
                            imgStatusYellow.setVisibility(View.GONE);
                            imgStatusRed.setVisibility(View.GONE);
                            break;
                    }
                }
            }
            else {
                imgStatusGray.setVisibility(View.VISIBLE);
                imgStatusGreen.setVisibility(View.GONE);
                imgStatusYellow.setVisibility(View.GONE);
                imgStatusRed.setVisibility(View.GONE);
            }

            row.setBackgroundColor(Utils.getColor(this, i % 2 == 0 ? R.color.colorGraysoft : R.color.white));
            phasesTable.addView(row);
            i++;
        }
    }
}
