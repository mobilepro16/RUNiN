package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.TrainingListAdapter;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.outdoor.OutdoorResultActivity;
import com.runin.runinapp.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Usuario on 11/07/2017.
 */

public class SelectTrainingActivity extends AppCompatActivity {
    private static final String TAG = SelectTrainingActivity.class.getSimpleName();

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.plan_image)
    ImageView plan_image;

    @BindView(R.id.lstTraining)
    ListView lstTraining;

    private OutdoorAppState outdoorAppState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detalles);
        ButterKnife.bind(this);

        final OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        final Plan selectedPlan = outdoorAppState.getSelectedPlan();
        final Stage selectedStage = outdoorAppState.getSelectedStage();

        final List<Training> stageTrainings = selectedPlan.getStage(selectedStage.getSequence()).getTrainings();
        for (Training training : stageTrainings) {
            final boolean previouslySelected = outdoorWorkoutsHistoryOperationsDB.workoutExists(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), this);
            final OutdoorWorkoutsHistory workoutCompleted = outdoorWorkoutsHistoryOperationsDB.workoutCompleted(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), this);
            training.setPreviouslySelected(previouslySelected);
            training.setPreviouslyCompleted(workoutCompleted);
            Log.i(TAG, String.format("Training: %d was %s previously selected and %s completed", training.getSequence(), previouslySelected ? "" : "not", training.isPreviouslyCompleted() ? "" : "not"));
        }

        TrainingListAdapter levelRectAdapter = new TrainingListAdapter(this, selectedPlan, selectedStage, stageTrainings);
        lstTraining.setAdapter(levelRectAdapter);
        lstTraining.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                outdoorAppState.setSelectedTraining(stageTrainings.get(i));

                Training training = stageTrainings.get(i);

                double trainingsCompleted = (double) outdoorWorkoutsHistoryOperationsDB.getTrainingsCompleted(selectedPlan.getId(), selectedStage.getSequence(), SelectTrainingActivity.this);
                double minimumTrainingsCompleted = (double) (stageTrainings.size() - 1) * App.minimumPacePercentageForCompleteStage;

                if (training.isPreviouslyCompleted()) {
                    double totalRanDistanceKm = outdoorWorkoutsHistoryOperationsDB.getLastRanDistanceKm(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), SelectTrainingActivity.this);
                    long elapsedTimeMilliSeconds = outdoorWorkoutsHistoryOperationsDB.getLastRanElapsedTimeMilliSeconds(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), SelectTrainingActivity.this);

                    Log.i(TAG, String.format("Selected training: (%d, %d).  Ran distance: %.1f, elapsed time: %d", selectedPlan.getId(), selectedStage.getSequence(), totalRanDistanceKm, elapsedTimeMilliSeconds));

                    Intent intent = new Intent(SelectTrainingActivity.this, OutdoorResultActivity.class);
                    intent.putExtra(App.extrasShowFinishButton, false);
                    intent.putExtra(App.extrasTotalDistanceKm, totalRanDistanceKm);
                    intent.putExtra(App.extrasElapsedTimeMilliSeconds, elapsedTimeMilliSeconds);

                    OutdoorWorkoutsHistory workout = outdoorAppState.getSelectedTraining().getPreviouslyCompleted();
                    outdoorAppState.setWorkout(workout);
                    startActivity(intent);
                }
                else if (!BuildConfig.ALL_TRACKS_AVAILABLE && training.isTest() && trainingsCompleted < minimumTrainingsCompleted) {
                    Log.w(TAG, String.format("Have %.0f trainings of %.0f needed in order to make test", trainingsCompleted, minimumTrainingsCompleted));
                    Toast.makeText(SelectTrainingActivity.this, getString(R.string.you_didnt_complete_trainings_to_make_test, App.minimumPacePercentageForCompleteStage * 100.0), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(SelectTrainingActivity.this, PreRunPlanActivity.class);
                    startActivity(intent);
                }
            }
        });

        title.setTypeface(Utils.getFontBebasNeue(this));
        title.setText(selectedStage.getTitle());

        plan_image.setImageResource(outdoorAppState.getPlanIcon(selectedPlan.getTitle()));
    }
}