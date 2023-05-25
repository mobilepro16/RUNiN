package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
 * Please document this class
 */
public class SelectPlanFocusActivity extends AppCompatActivity {
    private static final String TAG = SelectPlanFocusActivity.class.getSimpleName();

    @BindView(R.id.buttonplan)
    Button next;

    @BindView(R.id.distance_scheme)
    RelativeLayout distance;

    @BindView(R.id.speed_scheme)
    RelativeLayout speed;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.dist_title)
    TextView dist_title;

    @BindView(R.id.speed_title)
    TextView speed_title;

    private OutdoorAppState outdoorAppState;
    private Plan.Focus planFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_plans);
        ButterKnife.bind(this);

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/BebasNeue.otf");
        title.setTypeface(font);
        next.setTypeface(font);
        dist_title.setTypeface(Utils.getFontBebasNeue(this));
        speed_title.setTypeface(Utils.getFontBebasNeue(this));

        outdoorAppState = ((App) getApplication()).getOutdoorAppState();
        outdoorAppState.clear();
        outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.Plans);
        Log.i(TAG, "Outdoor plans selected");

        planFocus = null;

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (planFocus == null) {
                    Toast.makeText(SelectPlanFocusActivity.this, getResources().getString(R.string.plantype_title), Toast.LENGTH_LONG).show();
                }
                else {
                    normalView();

                    outdoorAppState.setSelectedPlanFocus(planFocus);
                    Log.i(TAG, "Focus selected: " + planFocus.toString());
                    Intent intent = new Intent(SelectPlanFocusActivity.this, SelectPlanActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalView();
                ((ImageView) v.findViewById(R.id.imageView9)).setImageResource(R.mipmap.chica_corriendo_check);
                planFocus = Plan.Focus.Distance;
            }
        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalView();
                ((ImageView) v.findViewById(R.id.imageView8)).setImageResource(R.mipmap.icono2_check);
                planFocus = Plan.Focus.Speed;
            }
        });

    }

    private void normalView() {
        ((ImageView) this.findViewById(R.id.imageView9)).setImageResource(R.mipmap.chica_corriendo);
        ((ImageView) this.findViewById(R.id.imageView8)).setImageResource(R.mipmap.icono2);
    }
}
