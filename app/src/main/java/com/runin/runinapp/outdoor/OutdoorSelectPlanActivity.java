package com.runin.runinapp.outdoor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.outdoor.improve.ImproveDefineFocusActivity;
import com.runin.runinapp.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * List to select the plan in outdoor activity
 * Created by Cesar on 09/08/2016.
 */
public class OutdoorSelectPlanActivity extends AppCompatActivity {
    //private static final String TAG = OutdoorSelectPlanActivity.class.getSimpleName();

    @BindView(R.id.quick_image)
    ImageView quickImg;

    @BindView(R.id.plans_image)
    ImageView plansImg;

    @BindView(R.id.improve_image)
    ImageView improveImg;

    @BindView(R.id.screen_title)
    TextView screenTitle;

    @BindView(R.id.place_title)
    TextView placeTitle;

    @BindView(R.id.quick_start_option)
    TextView quickStartOption;

    @BindView(R.id.plans_trainning_option)
    TextView plansTrainingOption;

    @BindView(R.id.improve_option)
    TextView improveOption;

    @BindView(R.id.button_next)
    Button next;

    private int opc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_plan_outdoor);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Picasso.get().load(R.mipmap.quick_icon_list).resize(91, 82).centerInside().into(quickImg);
        Picasso.get().load(R.mipmap.plans_icon_list).resize(91, 82).centerInside().into(plansImg);
        Picasso.get().load(R.mipmap.improve_icon_list).resize(91, 82).centerInside().into(improveImg);

        screenTitle.setTypeface(Utils.getFontBebasNeue(this));
        placeTitle.setTypeface(Utils.getFontBebasNeue(this));
        quickStartOption.setTypeface(Utils.getFontBebasNeue(this));
        plansTrainingOption.setTypeface(Utils.getFontBebasNeue(this));
        improveOption.setTypeface(Utils.getFontBebasNeue(this));
        opc = 0;
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/BebasNeue.otf");
        next.setTypeface(font);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opc == 0) {
                    Toast.makeText(OutdoorSelectPlanActivity.this, R.string.select_plan_outdoor, Toast.LENGTH_SHORT).show();
                }
                if (opc == 1) {
                    selectQuickStart();
                }
                if (opc == 2) {
                    selectPlans();
                }
                if (opc == 3) {
                    selectImprove();
                }
            }
        });
    }

    private void selectQuickStart() {
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeQuickstart);
        editor.apply();

        Intent intentMain = new Intent(this, OutdoorDashboardActivity.class);
        refresh();
        normalView();
        intentMain.putExtra(App.extrasScreenName, App.screenNameQuick);
        startActivity(intentMain);
    }

    private void selectPlans() {
        SharedPreferences sharedPref = this.getSharedPreferences(App.sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(App.sharedPreferencesPropertyTypeOfRunner, App.runnerTypeQuickstart);
        editor.apply();

        Intent intentMain = new Intent(this, OutdoorDashboardActivity.class);
        refresh();
        normalView();
        intentMain.putExtra(App.extrasScreenName, App.screenNamePlans);
        startActivity(intentMain);
    }

    private void selectImprove() {
        Intent intentMain = new Intent(this, ImproveDefineFocusActivity.class);
        refresh();
        normalView();
        startActivity(intentMain);
    }

    private void normalView() {
        ((ImageView) this.findViewById(R.id.quick_image)).setImageResource(R.mipmap.quick_icon_list);
        ((ImageView) this.findViewById(R.id.plans_image)).setImageResource(R.mipmap.plans_icon_list);
        ((ImageView) this.findViewById(R.id.improve_image)).setImageResource(R.mipmap.improve_icon_list);
    }

    public void plansP(View view) {
        normalView();
        ((ImageView) view.findViewById(R.id.plans_image)).setImageResource(R.mipmap.plans_icon_list_check);
        opc = 2;

    }

    public void quickStartP(View view) {
        normalView();
        ((ImageView) view.findViewById(R.id.quick_image)).setImageResource(R.mipmap.quick_icon_list_check);
        opc = 1;

    }

    public void improveP(View view) {
        normalView();
        ((ImageView) view.findViewById(R.id.improve_image)).setImageResource(R.mipmap.improve_icon_list_check);
        opc = 3;
    }

    private void refresh() {
        finish();
        startActivity(getIntent());
    }
}
