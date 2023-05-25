package com.runin.runinapp.outdoor.improve;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Usuario on 06/04/2017.
 */

public class ImproveDefineFocusActivity extends AppCompatActivity {
    private static final String TAG = ImproveDefineFocusActivity.class.getSimpleName();

    @BindView(R.id.screen_title)
    TextView screen_title;

    @BindView(R.id.run_improve_btn)
    Button run_improve_btn;

    @BindView(R.id.dist_option)
    TextView dist_option;

    @BindView(R.id.cal_option)

    TextView cal_option;

    private int opc;

    private OutdoorAppState outdoorAppState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_improve);
        ButterKnife.bind(this);
        screen_title.setTypeface(Utils.getFontBebasNeue(this));
        run_improve_btn.setTypeface(Utils.getFontBebasNeue(this));
        dist_option.setTypeface(Utils.getFontBebasNeue(this));
        cal_option.setTypeface(Utils.getFontBebasNeue(this));
        opc = 0;
        run_improve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opc != 0) {

                    Intent intentMain = new Intent(ImproveDefineFocusActivity.this, ImproveDefineDistanceActivity.class);

                    if (opc == 1) {
                        intentMain.putExtra(App.extrasImproveYourselfFocus, App.focusDistance);
                    }
                    else if (opc == 2) {
                        intentMain.putExtra(App.extrasImproveYourselfFocus, App.focusCalories);
                    }
                    normalView();

                    outdoorAppState = ((App) getApplication()).getOutdoorAppState();
                    outdoorAppState.clear();
                    outdoorAppState.setSelectedWorkoutType(OutdoorAppState.WorkoutType.ImproveYourself);
                    Log.i(TAG, "Improve yourself plans selected");

                    startActivity(intentMain);
                }
                else {
                    Toast.makeText(ImproveDefineFocusActivity.this, R.string.select_one_option, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void normalView() {
        ((ImageView) this.findViewById(R.id.distance_image)).setImageResource(R.drawable.tenis);
        ((ImageView) this.findViewById(R.id.cal_image)).setImageResource(R.drawable.calorias_002);
    }

    public void distanceS(View view) {
        normalView();
        ((ImageView) view.findViewById(R.id.distance_image)).setImageResource(R.drawable.tenis_check);
        opc = 1;
    }

    public void improveCalories(View view) {
        normalView();
        ((ImageView) view.findViewById(R.id.cal_image)).setImageResource(R.drawable.calorias_chechk);
        opc = 2;
    }
}
