package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.database.SelectedPlansHistory;
import com.runin.runinapp.data.database.SelectedPlansHistoryOperationsDB;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.data.Training;
import com.runin.runinapp.outdoor.OutdoorDashboardActivity;
import com.runin.runinapp.outdoor.OutdoorSelectPlanActivity;
import com.runin.runinapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 11/06/2017.
 */
public class SelectPlanSummary extends AppCompatActivity {
    @BindView(R.id.button_acept)
    Button accept;

    @BindView(R.id.button_change)
    Button change;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.inicio_fecha)
    TextView startDate;

    @BindView(R.id.fin_fecha)
    TextView endDate;

    @BindView(R.id.recom_plans)
    TextView recommendedPlans;

    private Plan selectedPlan;
    private SelectedPlansHistoryOperationsDB selectedPlansHistoryOperationsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        ButterKnife.bind(this);

        accept.setTypeface(Utils.getFontBebasNeue(this));
        title.setTypeface(Utils.getFontBebasNeue(this));
        change.setTypeface(Utils.getFontBebasNeue(this));

        OutdoorAppState outdoorAppState = ((App) getApplication()).getOutdoorAppState();
        selectedPlan = outdoorAppState.getSelectedPlan();

        selectedPlansHistoryOperationsDB = new SelectedPlansHistoryOperationsDB();
        SelectedPlansHistory plansHistory = selectedPlansHistoryOperationsDB.get(selectedPlan.getTitle(), this);

        if (plansHistory == null) {
            ((ImageView) findViewById(R.id.plan_logo)).setImageResource(outdoorAppState.getPlanIcon(selectedPlan.getTitle()));
            recommendedPlans.setText(getResources().getString(selectedPlan.getDescription()));
        }
        else {
            // Go directly to next screen.
            finishIntent();
        }

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        startDate.setText(formatter.format(today));

        int totalTrainings = 0;
        List<Stage> stages = selectedPlan.getStages();
        for (Stage stage : stages) {
            List<Training> trainings = stage.getTrainings();
            for (Training ignored : trainings) {
                totalTrainings++;
            }
        }

        int weeks = totalTrainings / selectedPlan.getDaysPerWeek();
        int remainder = totalTrainings % selectedPlan.getDaysPerWeek();
        int days = weeks * 7 + remainder;

        Date finishDate = addDaysToDate(today, days);

        endDate.setText(formatter.format(finishDate));

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPlanSummary.this, OutdoorSelectPlanActivity.class);
                startActivity(intent);
                SelectPlanSummary.this.finish();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedPlansHistory history = new SelectedPlansHistory(selectedPlan.getTitle(), selectedPlan.getLength(), selectedPlan.getDaysPerWeek(), new Date());

                selectedPlansHistoryOperationsDB.add(history, SelectPlanSummary.this);

                finishIntent();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intentOutdoor = new Intent(this, SelectPlanLength.class);
        startActivity(intentOutdoor);
        finish();
    }

    private Date addDaysToDate(Date date, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }

    private void finishIntent() {
        Intent intent = new Intent(SelectPlanSummary.this, OutdoorDashboardActivity.class);
        intent.putExtra(App.extrasScreenName, App.screenNamePlansMain);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}