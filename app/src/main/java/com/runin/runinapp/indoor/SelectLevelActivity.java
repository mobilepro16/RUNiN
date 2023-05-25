package com.runin.runinapp.indoor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.LevelRectAdapter;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.IndoorLevel;
import com.runin.runinapp.data.SpeedLevel;
import com.runin.runinapp.utils.Utils;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class SelectLevelActivity extends AppCompatActivity {

    private final static String TAG = SelectLevelActivity.class.getSimpleName();

    private ArrayList<IndoorLevel> levels;

    private boolean confirmIndoorLevel;

    private IndoorAppState indoorAppState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        indoorAppState = ((App) this.getApplication()).getIndoorAppState();

        confirmIndoorLevel = getIntent().getBooleanExtra(App.extrasConfirmIndoorLevel, false);

        setContentView(R.layout.activity_select_level);

        TwoWayView list_ab = findViewById(R.id.twoway_view);
        TextView tv_title = findViewById(R.id.screen_title);
        Spinner spinnerDifficult = findViewById(R.id.spinnerDifficult);

        tv_title.setTypeface(Utils.getFontBebasNeue(SelectLevelActivity.this));

        levels = indoorAppState.getIndoorLevels();

        LevelRectAdapter adapter = new LevelRectAdapter(SelectLevelActivity.this, levels);
        list_ab.setAdapter(adapter);

        list_ab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                IndoorLevel level = levels.get(position);
                String alertTitle = getString(R.string.you_have_chosnen_level_x, getString(level.getTitleId()));

                if (!confirmIndoorLevel) {
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(SelectLevelActivity.this).setTitle(alertTitle)
                            .setMessage(R.string.do_you_want_to_continue).setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoTracks(position);
                                }
                            });
                    AlertDialog confirm = confirmBuilder.create();
                    confirm.show();
                }
                else {
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(SelectLevelActivity.this).setTitle(alertTitle)
                            .setMessage(R.string.routines_will_be_increasing_want_to_continue).setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectLevelActivity.this);
                                    builder.setTitle(R.string.manage_videos).setMessage(R.string.remove_videos)
                                            .setPositiveButton(R.string.addConfirmedPositive, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    gotoTracks(position);
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                    AlertDialog confirm = confirmBuilder.create();
                    confirm.show();
                }
            }
        });

        TextView txtConfirmIndoorLevel = findViewById(R.id.confirmIndoorLevel);
        TextView txtSelectLevel = findViewById(R.id.selecciona_perfil);

        if (confirmIndoorLevel && indoorAppState.getIndoorLevel() != null) {
            txtSelectLevel.setVisibility(View.GONE);
            txtConfirmIndoorLevel.setVisibility(View.VISIBLE);

            String confirm_indoor_level = getString(R.string.confirm_indoor_level);
            String level_name = getString(indoorAppState.getIndoorLevel().getTitleId());
            txtConfirmIndoorLevel.setText(Utils.fromHtml(String.format(confirm_indoor_level, level_name)));
        }
        else {
            txtSelectLevel.setVisibility(View.VISIBLE);
            txtConfirmIndoorLevel.setVisibility(View.GONE);
        }

        // List of programs
        ArrayList<String> difficulty_levels = new ArrayList<>();
        difficulty_levels.add(getString(R.string.moderated));
        difficulty_levels.add(getString(R.string.intense));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficulty_levels);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficult.setAdapter(spinnerArrayAdapter);

        SpeedLevel speedLevel = indoorAppState.getSpeedLevel();
        spinnerDifficult.setSelection(speedLevel != null ? speedLevel.getValue() : 0);

        // Set the selected program in the track when the user chooses the adapter.
        spinnerDifficult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, String.format("Setting selected difficulty level to %d", i));
                indoorAppState.setSpeedLevel(SpeedLevel.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void gotoTracks(int level) {
        indoorAppState.setIndoorLevel(levels.get(level));

        // Save the user with the selected level
        Intent intentIndoor = new Intent(this, IndoorDashboardActivity.class);
        intentIndoor.putExtra(App.extrasScreenName, App.screenNameTracks);
        startActivity(intentIndoor);
        SelectLevelActivity.this.finish();
    }
}
