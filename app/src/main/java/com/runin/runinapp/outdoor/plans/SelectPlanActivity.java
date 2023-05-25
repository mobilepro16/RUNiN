package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:
 * Created by Usuario on 06/07/2017.
 */

public class SelectPlanActivity extends AppCompatActivity {

    @BindView(R.id.distance_container)
    LinearLayout distance_container;

    @BindView(R.id.vel_container)
    LinearLayout vel_container;

    @BindView(R.id.screen_title)
    TextView screenTitle;

    @BindView(R.id.button_next)
    Button next;

    @BindView(R.id.plans_desc)
    TextView plans_desc;

    @BindView(R.id.kilo_3)
    RelativeLayout kilo_3;

    @BindView(R.id.kilo_5)
    RelativeLayout kilo_5;

    @BindView(R.id.kilo_10)
    RelativeLayout kilo_10;

    @BindView(R.id.kilo_5_plus)
    RelativeLayout kilo_5_plus;

    @BindView(R.id.kilo_10_plus)
    RelativeLayout kilo_10_plus;

    @BindView(R.id.img_kilo_3_ch)
    ImageView img_kilo_3_ch;

    @BindView(R.id.img_kilo_5_ch)
    ImageView img_kilo_5_ch;

    @BindView(R.id.img_kilo_10_ch)
    ImageView img_kilo_10_ch;

    @BindView(R.id.img_kilo_5_ch_plus)
    ImageView img_kilo_5_ch_plus;

    @BindView(R.id.img_kilo_10_ch_plus)
    ImageView img_kilo_10_ch_plus;

    private OutdoorAppState outdoorAppState;

    private String selectedPlanName = null;

    public SelectPlanActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_buy);
        ButterKnife.bind(this);

        screenTitle.setTypeface(Utils.getFontBebasNeue(this));
        next.setTypeface(Utils.getFontBebasNeue(this));
        normalView();

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();

        Plan.Focus focus = outdoorAppState.getSelectedPlanFocus();


        if (focus == Plan.Focus.Distance) {
            distance_container.setVisibility(View.VISIBLE);
            vel_container.setVisibility(View.GONE);
            plans_desc.setText(getResources().getString(R.string.plans_distances_desc));
        }
        else if (focus == Plan.Focus.Speed) {
            distance_container.setVisibility(View.GONE);
            vel_container.setVisibility(View.VISIBLE);
            plans_desc.setText(getResources().getString(R.string.plans_vel_desc));
        }

        kilo_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                img_kilo_3_ch.setImageResource(R.drawable.check);
                selectedPlanName = OutdoorAppState.PLAN_3K_TITLE;
            }
        });

        kilo_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                img_kilo_5_ch.setImageResource(R.drawable.check);
                selectedPlanName = OutdoorAppState.PLAN_5K_TITLE;
            }
        });

        kilo_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                img_kilo_10_ch.setImageResource(R.drawable.check);
                selectedPlanName = OutdoorAppState.PLAN_10K_TITLE;
            }
        });

        kilo_5_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                img_kilo_5_ch_plus.setImageResource(R.drawable.check);
                selectedPlanName = OutdoorAppState.PLAN_5K_PLUS_TITLE;
            }
        });

        kilo_10_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalView();
                img_kilo_10_ch_plus.setImageResource(R.drawable.check);
                selectedPlanName = OutdoorAppState.PLAN_10K_PLUS_TITLE;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlanName == null) {
                    Toast.makeText(SelectPlanActivity.this, R.string.select_the_distance, Toast.LENGTH_LONG).show();
                }
                else {
                    finishIntent();
                }
            }
        });
    }

    private void normalView() {
        img_kilo_3_ch.setImageResource(R.drawable.unchecked);
        img_kilo_5_ch.setImageResource(R.drawable.unchecked);
        img_kilo_10_ch.setImageResource(R.drawable.unchecked);
        img_kilo_5_ch_plus.setImageResource(R.drawable.unchecked);
        img_kilo_10_ch_plus.setImageResource(R.drawable.unchecked);
    }

    private void finishIntent() {
        outdoorAppState.setSelectedPlanName(selectedPlanName);
        outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.Plans);
        outdoorAppState.save();

        Intent intentOutdoor = new Intent(this, SelectPlanLength.class);
        startActivity(intentOutdoor);
        finish();
    }
}
