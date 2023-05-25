package com.runin.runinapp.outdoor.plans;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.Phase;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.TrainingPlanView;

import java.util.List;
import java.util.Locale;

public class TrainingGraphFragment extends Fragment {
    private static final String TAG = TrainingGraphFragment.class.getSimpleName();
    private double factor = 0.0;
    private List<Phase> phases = null;
    private TextView trainingPhase;
    private TextView trainingSegment;
    private TextView trainingInstruction;
    private TrainingPlanView trainingPlanView;
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    public TrainingGraphFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createListener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_plan_details, container, false);

        trainingSegment = view.findViewById(R.id.training_segment);
        trainingPhase = view.findViewById(R.id.training_phase);
        trainingInstruction = view.findViewById(R.id.training_instruction);
        trainingPlanView = view.findViewById(R.id.training_plan_view);
        SeekBar seekBar = view.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        trainingInstruction.setText("");
        trainingPhase.setText("");
        trainingSegment.setText("");

        return view;
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;

        trainingPlanView.setPhases(phases);
        calculateFactor();
        updateTable(0);
    }

    private void createListener() {
        onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateTable(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private void updateTable(int trainingPercentage) {
        if (phases == null) return;

        int index = getGoalByPercentage(trainingPercentage);
        Phase phase;
        if (index >= 0) {
            phase = phases.get(index);
            String instruction = getString(phase.getInstructionName());
            String kind = getString(phase.getKindName());
            Double pace = phase.getPaceMinPerKm();
            Double duration = (double) phase.getDurationMillis() / 60000.0;

            Log.i(TAG, String.format("Phase info: %s %s %.1f %.1f", instruction, kind, pace, duration));

            trainingSegment.setText(String.format(Locale.getDefault(), getString(R.string.stage_n), index + 1));
            trainingPhase.setText(String.format(Locale.getDefault(), getString(R.string.phase_of_string), kind));
            trainingInstruction.setText(getString(R.string.instruction_pace_distance, instruction, DateUtils.minutesToMinSec(duration), DateUtils.minutesToMinSec(pace)));
        }
        else {
            trainingSegment.setText("");
            trainingInstruction.setText("");
            trainingPhase.setText("");
        }
    }

    private void calculateFactor() {
        if (phases == null) {
            factor = 0.0;
            return;
        }

        int totalWidth = 0;
        for (Phase phase : phases) {
            if (phase.getKind() == Phase.Kind.WARM_UP || phase.getKind() == Phase.Kind.COOL_DOWN)
            // Warm up and cool down are wider than the others
            {
                totalWidth += 30;
            }
            else {
                totalWidth += 20;
            }
        }
        totalWidth += 5 * (phases.size() - 1);
        factor = 100.0 / totalWidth;
    }

    private int getGoalByPercentage(int percentage) {
        int totalWidth = 0;
        int i = 0;

        for (Phase goal : phases) {
            if (goal.getKind() == Phase.Kind.WARM_UP || goal.getKind() == Phase.Kind.COOL_DOWN)
            // Warm up and cool down are wider than the others
            {
                totalWidth += 30;
            }
            else {
                totalWidth += 20;
            }

            if (factor * totalWidth >= 1.0 * percentage) {
                Log.i(TAG, String.format("Calculated an index of: %d when accumulated width is: %d", i, totalWidth));
                return i;
            }

            totalWidth += 5;
            i++;
        }

        return 0;
    }
}
