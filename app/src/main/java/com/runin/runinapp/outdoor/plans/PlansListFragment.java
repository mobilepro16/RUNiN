package com.runin.runinapp.outdoor.plans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.utils.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Usuario on 12/06/2017.
 */


public class PlansListFragment extends Fragment {
    private static final String TAG = PlansListFragment.class.getSimpleName();

    @BindView(R.id.button_next)
    Button next;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.plan3k)
    LinearLayout plan3k;

    @BindView(R.id.plan5k)
    LinearLayout plan5k;

    @BindView(R.id.plan10k)
    LinearLayout plan10k;

    @BindView(R.id.plan5k_plus)
    LinearLayout plan5k_plus;

    @BindView(R.id.plan10k_plus)
    LinearLayout plan10k_plus;

    @BindView(R.id.porcentaje_3k)
    TextView percentage3K;

    @BindView(R.id.porcentaje_5k)
    TextView percentage5K;

    @BindView(R.id.porcentaje_10k)
    TextView percentage10K;

    @BindView(R.id.porcentaje_5k_plus)
    TextView percentage5KPlus;

    @BindView(R.id.porcentaje_10k_plus)
    TextView percentage10KPlus;

    private OutdoorAppState outdoorAppState;

    public PlansListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plans, container, false);
        ButterKnife.bind(this, view);

        next.setTypeface(Utils.getFontBebasNeue(getContext()));
        title.setTypeface(Utils.getFontBebasNeue(getContext()));

        Context context = getContext();
        final Activity activity = getActivity();
        if (activity == null) return view;

        outdoorAppState = ((App) activity.getApplication()).getOutdoorAppState();

        List<String> previouslySelectedPlans = outdoorAppState.getPreviouslySelectedPlans();
        Log.i(TAG, "Previously selected plans: " + previouslySelectedPlans.size());

        Plan plan3KLong = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_3K_TITLE, Plan.Length.Long);

        Plan plan5KLong = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_5K_TITLE, Plan.Length.Long);
        Plan plan5KShort = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_5K_TITLE, Plan.Length.Short);

        Plan plan10KLong = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_10K_TITLE, Plan.Length.Long);
        Plan plan10KShort = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_10K_TITLE, Plan.Length.Short);

        Plan plan5KPlusLong = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_5K_PLUS_TITLE, Plan.Length.Long);
        Plan plan5KPlusShort = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_5K_PLUS_TITLE, Plan.Length.Short);

        Plan plan10KPlusLong = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_10K_PLUS_TITLE, Plan.Length.Long);
        Plan plan10KPlusShort = outdoorAppState.getPlanByTitleAndLength(OutdoorAppState.PLAN_10K_PLUS_TITLE, Plan.Length.Short);

        double plan3KPercentFinished = outdoorAppState.planPercentFinished(plan3KLong.getId(), context);

        double plan5KLongPercentFinished = outdoorAppState.planPercentFinished(plan5KLong.getId(), context);
        double plan5KShortPercentFinished = outdoorAppState.planPercentFinished(plan5KShort.getId(), context);

        double plan10KLongPercentFinished = outdoorAppState.planPercentFinished(plan10KLong.getId(), context);
        double plan10KShortPercentFinished = outdoorAppState.planPercentFinished(plan10KShort.getId(), context);

        double plan5KPlusLongPercentFinished = outdoorAppState.planPercentFinished(plan5KPlusLong.getId(), context);
        double plan5KPlusShortPercentFinished = outdoorAppState.planPercentFinished(plan5KPlusShort.getId(), context);

        double plan10KPlusLongPercentFinished = outdoorAppState.planPercentFinished(plan10KPlusLong.getId(), context);
        double plan10KPlusShortPercentFinished = outdoorAppState.planPercentFinished(plan10KPlusShort.getId(), context);

        double plan5KPercentFinished = 0.0;
        double plan10KPercentFinished = 0.0;
        double plan5KPlusPercentFinished = 0.0;
        double plan10KPlusPercentFinished = 0.0;

        if (plan5KLongPercentFinished > 0.0) {
            plan5KPercentFinished = plan5KLongPercentFinished;
        }
        if (plan5KShortPercentFinished > plan5KLongPercentFinished) {
            plan5KPercentFinished = plan5KShortPercentFinished;
        }

        if (plan10KLongPercentFinished > 0.0) {
            plan10KPercentFinished = plan10KLongPercentFinished;
        }
        if (plan10KShortPercentFinished > plan10KLongPercentFinished) {
            plan10KPercentFinished = plan10KShortPercentFinished;
        }

        if (plan5KPlusLongPercentFinished > 0.0) {
            plan5KPlusPercentFinished = plan5KPlusLongPercentFinished;
        }
        if (plan5KPlusShortPercentFinished > plan5KPlusLongPercentFinished) {
            plan5KPlusPercentFinished = plan5KPlusShortPercentFinished;
        }

        if (plan10KPlusLongPercentFinished > 0.0) {
            plan10KPlusPercentFinished = plan10KPlusLongPercentFinished;
        }
        if (plan10KPlusShortPercentFinished > plan10KPlusLongPercentFinished) {
            plan10KPlusPercentFinished = plan10KPlusShortPercentFinished;
        }

        if (previouslySelectedPlans.contains(OutdoorAppState.PLAN_3K_TITLE)) {
            plan3k.setVisibility(View.VISIBLE);
            percentage3K.setText(String.format("%s%s", String.format(Locale.getDefault(), "%.0f", plan3KPercentFinished * 100), getResources().getString(R.string.porcentaje_completado)));
        }
        else {
            plan3k.setVisibility(View.GONE);
        }

        if (previouslySelectedPlans.contains(OutdoorAppState.PLAN_5K_TITLE)) {
            plan5k.setVisibility(View.VISIBLE);
            percentage5K.setText(String.format("%s%s", String.format(Locale.getDefault(), "%.0f", plan5KPercentFinished * 100), getResources().getString(R.string.porcentaje_completado)));
        }
        else {
            plan5k.setVisibility(View.GONE);
        }

        if (previouslySelectedPlans.contains(OutdoorAppState.PLAN_10K_TITLE)) {
            plan10k.setVisibility(View.VISIBLE);
            percentage10K.setText(String.format("%s%s", String.format(Locale.getDefault(), "%.0f", plan10KPercentFinished * 100), getResources().getString(R.string.porcentaje_completado)));
        }
        else {
            plan10k.setVisibility(View.GONE);
        }

        if (previouslySelectedPlans.contains(OutdoorAppState.PLAN_5K_PLUS_TITLE)) {
            plan5k_plus.setVisibility(View.VISIBLE);
            percentage5KPlus.setText(String.format("%s%s", String.format(Locale.getDefault(), "%.0f", plan5KPlusPercentFinished * 100), getResources().getString(R.string.porcentaje_completado)));
        }
        else {
            plan5k_plus.setVisibility(View.GONE);
        }

        if (previouslySelectedPlans.contains(OutdoorAppState.PLAN_10K_PLUS_TITLE)) {
            plan10k_plus.setVisibility(View.VISIBLE);
            percentage10KPlus.setText(String.format("%s%s", String.format(Locale.getDefault(), "%.0f", plan10KPlusPercentFinished * 100), getResources().getString(R.string.porcentaje_completado)));
        }
        else {
            plan10k_plus.setVisibility(View.GONE);
        }


        plan3k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorAppState.setSelectedPlanName(OutdoorAppState.PLAN_3K_TITLE);
                Intent intentOutdoor = new Intent(getContext(), SelectPlanLength.class);
                startActivity(intentOutdoor);
            }
        });

        plan5k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorAppState.setSelectedPlanName(OutdoorAppState.PLAN_5K_TITLE);
                Intent intentOutdoor = new Intent(getContext(), SelectPlanLength.class);
                startActivity(intentOutdoor);
            }
        });

        plan10k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorAppState.setSelectedPlanName(OutdoorAppState.PLAN_10K_TITLE);
                Intent intentOutdoor = new Intent(getContext(), SelectPlanLength.class);
                startActivity(intentOutdoor);
            }
        });

        plan5k_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorAppState.setSelectedPlanName(OutdoorAppState.PLAN_5K_PLUS_TITLE);
                Intent intentOutdoor = new Intent(getContext(), SelectPlanLength.class);
                startActivity(intentOutdoor);
            }
        });

        plan10k_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorAppState.setSelectedPlanName(OutdoorAppState.PLAN_10K_PLUS_TITLE);
                Intent intentOutdoor = new Intent(getContext(), SelectPlanLength.class);
                startActivity(intentOutdoor);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(getContext(), SelectPlanFocusActivity.class);
                startActivity(intentMain);
            }
        });


        return view;
    }
}
