package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.database.SelectedPlansHistory;
import com.runin.runinapp.data.database.SelectedPlansHistoryOperationsDB;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 11/06/2017.
 */
public class SelectPlanLength extends AppCompatActivity {
    @BindView(R.id.button_next)
    Button next;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.days_1)
    ImageView days_1;

    @BindView(R.id.days_2)
    ImageView days_2;

    @BindView(R.id.days_3)
    ImageView days_3;

    @BindView(R.id.days_4)
    ImageView days_4;

    @BindView(R.id.days_5)
    ImageView days_5;

    @BindView(R.id.days_6)
    ImageView days_6;

    @BindView(R.id.days_7)
    ImageView days_7;

    @BindView(R.id.plan_logo)
    ImageView plan_logo;

    @BindView(R.id.desc_type_plan)
    TextView desc_type_plan;

    @BindView(R.id.plan_botons)
    LinearLayout planButtons;

    @BindView(R.id.select_plan)
    TextView selectPlan;

    @BindView(R.id.long_plan)
    Button longPlan;

    @BindView(R.id.short_plan)
    Button shortPlan;

    @BindView(R.id.desc_plan)
    TextView descriptionPlan;

    private int optionPlan = 0;
    private int numberOfDays = 0;

    private String planName;
    private OutdoorAppState outdoorAppState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        ButterKnife.bind(this);

        title.setTypeface(Utils.getFontBebasNeue(this));
        next.setTypeface(Utils.getFontBebasNeue(this));
        longPlan.setTypeface(Utils.getFontBebasNeue(this));
        shortPlan.setTypeface(Utils.getFontBebasNeue(this));

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        planName = outdoorAppState.getSelectedPlanName();

        // Here we don't have a plan ID yet, that's why we're using the plan name always.
        SelectedPlansHistoryOperationsDB selectedPlansHistoryOperationsDB = new SelectedPlansHistoryOperationsDB();
        SelectedPlansHistory plansHistory = selectedPlansHistoryOperationsDB.get(planName, this);

        // Plan 3K only has one option.
        if (planName.equals(OutdoorAppState.PLAN_3K_TITLE)) {
            planButtons.setVisibility(View.GONE);
            selectPlan.setVisibility(View.GONE);
            desc_type_plan.setVisibility(View.GONE);
        }

        if (plansHistory == null) {
            plan_logo.setImageResource(outdoorAppState.getPlanIcon(planName));
            descriptionPlan.setText(outdoorAppState.getPlanDescription(planName));
        }
        /* TODO: This is all wrong... Must be in SelectPlanActivity, not here. Why we start an activity and then just jump from it? ... */
        else {
            Plan selectedPlan = outdoorAppState.getPlanByTitleAndLength(planName, plansHistory.getLength());
            outdoorAppState.setSelectedPlan(selectedPlan);
            selectedPlan.setDaysPerWeek(plansHistory.getDaysInWeek());
            // Go directly to next screen.
            finishIntent();
        }

        // Default plan is Long
        optionPlan = 1;

        days_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_1.setImageResource(R.drawable.circle_color_1);
                numberOfDays = 1;
            }
        });

        days_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_2.setImageResource(R.drawable.circle_color_2);
                numberOfDays = 2;
            }
        });

        days_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_3.setImageResource(R.drawable.circle_color_3);
                numberOfDays = 3;
            }
        });

        days_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_4.setImageResource(R.drawable.circle_color_4);
                numberOfDays = 4;
            }
        });

        days_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_5.setImageResource(R.drawable.circle_color_5);
                numberOfDays = 5;
            }
        });

        days_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_6.setImageResource(R.drawable.circle_color_6);
                numberOfDays = 6;
            }
        });

        days_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                days_7.setImageResource(R.drawable.circle_color_7);
                numberOfDays = 7;
            }
        });

        longPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalPlan();
                longPlan.setBackground(Utils.getDrawable(SelectPlanLength.this, R.drawable.button_orange));
                desc_type_plan.setText(getResources().getString(R.string.desc_plan_largo));
                optionPlan = 1;
            }
        });

        shortPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalPlan();
                shortPlan.setBackground(Utils.getDrawable(SelectPlanLength.this, R.drawable.button_orange));
                desc_type_plan.setText(getResources().getString(R.string.desc_plan_corto));
                optionPlan = 2;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfDays == 0) {
                    Toast.makeText(SelectPlanLength.this, R.string.select_training_days, Toast.LENGTH_SHORT).show();
                }
                else {

                    Plan.Length length;
                    if (optionPlan == 1) {
                        length = Plan.Length.Long;
                    }
                    else {
                        length = Plan.Length.Short;
                    }
                    Plan selectedPlan = outdoorAppState.getPlanByTitleAndLength(planName, length);
                    outdoorAppState.setSelectedPlan(selectedPlan);
                    selectedPlan.setDaysPerWeek(numberOfDays);
                    finishIntent();
                }
            }
        });
    }

    private void normalView() {
        days_1.setImageResource(R.drawable.circle_grey_1);
        days_2.setImageResource(R.drawable.circle_grey_2);
        days_3.setImageResource(R.drawable.circle_grey_3);
        days_4.setImageResource(R.drawable.circle_grey_4);
        days_5.setImageResource(R.drawable.circle_grey_5);
        days_6.setImageResource(R.drawable.circle_grey_6);
        days_7.setImageResource(R.drawable.circle_grey_7);
    }

    private void normalPlan() {
        longPlan.setBackground(Utils.getDrawable(this, R.drawable.button_gray));
        shortPlan.setBackground(Utils.getDrawable(this, R.drawable.button_gray));
    }

    private void finishIntent() {
        Intent intent = new Intent(SelectPlanLength.this, SelectPlanSummary.class);
        startActivity(intent);
        finish();
    }
}
