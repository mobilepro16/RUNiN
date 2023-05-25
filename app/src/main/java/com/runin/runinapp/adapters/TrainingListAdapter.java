package com.runin.runinapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import java.util.List;

/**
 * Adapter for selecting a training from a list
 * Created by Samuel Kobelkowsky on 11/26/2017.
 */
public class TrainingListAdapter extends BaseAdapter {
    @NonNull
    private final List<Training> trainings;

    private final OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB;

    @NonNull
    private final Context context;

    @NonNull
    private final Plan selectedPlan;

    @NonNull
    private final Stage selectedStage;

    private OutdoorAppState outdoorAppState;

    public TrainingListAdapter(@NonNull Context context, @NonNull Plan selectedPlan, @NonNull Stage selectedStage, @NonNull List<Training> trainings) {
        this.trainings = trainings;
        this.context = context;
        this.selectedPlan = selectedPlan;
        this.selectedStage = selectedStage;
        this.outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        outdoorAppState = ((App) context.getApplicationContext()).getOutdoorAppState();
    }

    @Override
    public int getCount() {
        return trainings.size();
    }

    @Override
    public Object getItem(int i) {
        return trainings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = View.inflate(context, R.layout.adapter_training_list, null);

            holder = new ViewHolder();

            holder.title = view.findViewById(R.id.title);
            holder.subtitle = view.findViewById(R.id.subtitle);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        Training training = trainings.get(i);

        // Using this to populate the phases
        outdoorAppState.setSelectedTraining(training);

        StringBuilder text = new StringBuilder();
        text.append(context.getString(R.string.training)).append(" ").append(training.getSequence() + 1);

        if (training.isTest()) {
            text.append(" - ").append(context.getString(R.string.test));
        }

        if (training.isPreviouslySelected()) {
            if (training.isPreviouslyCompleted()) {
                holder.title.setTextColor(Utils.getColor(context, R.color.greenRunin));
                text.append(" - ").append(context.getString(R.string.entrenamiento_logrado));
            }
            else {
                holder.title.setTextColor(Utils.getColor(context, R.color.redRunin));
                text.append(" - ").append(context.getString(R.string.entrenamiento_fallido));
            }

            double totalRanDistanceKm = outdoorWorkoutsHistoryOperationsDB.getLastRanDistanceKm(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), context);
            long elapsedTimeMilliSeconds = outdoorWorkoutsHistoryOperationsDB.getLastRanElapsedTimeMilliSeconds(selectedPlan.getId(), selectedStage.getSequence(), training.getSequence(), context);


            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(context.getString(R.string.plan_objective_vs_achieved,
                    training.getDistanceMeters() / 1000.0, totalRanDistanceKm, DateUtils.millisToMinSec(elapsedTimeMilliSeconds)));
        }
        else {
            holder.title.setTextColor(Utils.getColor(context, R.color.default_title_gray));
            holder.subtitle.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(text.toString());

        return view;
    }

    private class ViewHolder {
        TextView title;
        TextView subtitle;
    }
}
