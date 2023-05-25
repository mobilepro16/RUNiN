package com.runin.runinapp.indoor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.MusicSelectPlayer;
import com.runin.runinapp.data.Badge;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.RecyclerItemData;
import com.runin.runinapp.utils.RecyclerMusicClickListener;
import com.runin.runinapp.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreIndoorActivity extends AppCompatActivity {
    private static final String TAG = PreIndoorActivity.class.getSimpleName();

    // Permission results are identified by this constant
    private static final int MY_PERMISSION_CAMERA_REQUEST_ID = 1;

    @BindView(R.id.buttonSave)
    Button save;

    @BindView(R.id.tx_location)
    TextView tx_location;

    @BindView(R.id.desc_specs)
    TextView desc_specs;

    @BindView(R.id.cal_text)
    TextView cal_text;

    @BindView(R.id.effort_text)
    TextView effort_text;

    @BindView(R.id.time_text)
    TextView time_text;

    @BindView(R.id.im_map)
    ImageView im_map;

    @BindView(R.id.program2_layout)
    LinearLayout program2_layout;

    @BindView(R.id.program1_badge)
    ImageView program1_badge;

    @BindView(R.id.program1_title)
    TextView program1_title;

    @BindView(R.id.program2_badge)
    ImageView program2_badge;

    @BindView(R.id.program2_title)
    TextView program2_title;

    @BindView(R.id.program_spinner)
    Spinner program_spinner;

    @BindView(R.id.program_spinner_label)
    TextView program_spinner_label;

    @Inject
    User userProfile;

    private Track selectedTrack;

    private TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IndoorAppState indoorAppState = ((App) this.getApplication()).getIndoorAppState();

        setContentView(R.layout.activity_pre_indoor);
        ButterKnife.bind(this);

        RecyclerView recyclerMusicView = findViewById(R.id.recycler_player_options);
        dataTable = findViewById(R.id.table_include).findViewById(R.id.Tabla);

        recyclerMusicView.setLayoutManager(new LinearLayoutManager(this));

        save.setTypeface(Utils.getFontBebasNeue(this), Typeface.ITALIC);

        selectedTrack = indoorAppState.getSelectedTrack();
        selectedTrack.setSelectedProgram(0);
        selectedTrack.setIndoorLevel(indoorAppState.getIndoorLevel());
        selectedTrack.setSpeedLevel(indoorAppState.getSpeedLevel());

        tx_location.setText(Utils.getString(this, selectedTrack.getLocationId()));
        desc_specs.setText(Utils.getString(this, selectedTrack.getSpecsId()));
        im_map.setImageDrawable(Utils.getDrawable(this, selectedTrack.getMapResourceId()));

        Badge p1Badge = selectedTrack.getProgram1Badge();
        int p1BadgeIcon = indoorAppState.isBadgeWon(p1Badge.getLevel()) ? p1Badge.getRectangularIcon() : p1Badge.getRectangularGrayIcon();

        program1_badge.setImageDrawable(Utils.getDrawable(this, p1BadgeIcon));
        program1_title.setText(Utils.getString(this, selectedTrack.getProgram1DescriptionId()));

        // List of programs
        ArrayList<String> programs = new ArrayList<>();
        programs.add(Utils.getString(this, selectedTrack.getProgram1TitleId()));

        // If the track doesn't have program 2, then hide all the relevant logic.
        if (selectedTrack.getProgram2Badge() != null) {

            Badge p2Badge = selectedTrack.getProgram2Badge();
            int p2BadgeIcon = indoorAppState.isBadgeWon(p2Badge.getLevel()) ? p2Badge.getRectangularIcon() : p2Badge.getRectangularGrayIcon();

            program2_badge.setImageDrawable(Utils.getDrawable(this, p2BadgeIcon));
            program2_title.setText(Utils.getString(this, selectedTrack.getProgram2DescriptionId()));
            program2_layout.setVisibility(View.VISIBLE);
            program_spinner.setVisibility(View.VISIBLE);
            program_spinner_label.setVisibility(View.VISIBLE);
            programs.add(Utils.getString(this, selectedTrack.getProgram2TitleId()));
        }
        else {
            program2_layout.setVisibility(View.GONE);
            program_spinner.setVisibility(View.GONE);
            program_spinner_label.setVisibility(View.GONE);
        }

        // The spinner that allows the user to select between program 0 and 1 is controlled by the Adapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, programs);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        program_spinner.setAdapter(spinnerArrayAdapter);

        // Set the selected program in the track when the user chooses the adapter.
        program_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, String.format("Setting selected program to %d", i));
                selectedTrack.setSelectedProgram(i);

                fillStatsTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        RecyclerItemData itemData[] = {
                new RecyclerItemData(getString(R.string.musicSelect))
        };

        MusicSelectPlayer musicSelectPlayerAdapter = new MusicSelectPlayer(itemData);
        recyclerMusicView.setAdapter(musicSelectPlayerAdapter);
        recyclerMusicView.addOnItemTouchListener(new RecyclerMusicClickListener(PreIndoorActivity.this, new RecyclerMusicClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        try {
                            @SuppressWarnings("deprecation") Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(PreIndoorActivity.this, R.string.no_music_players_installed, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(PreIndoorActivity.this, R.string.cannot_start_music_player, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Cannot start music app", ex);
                        }
                        break;
                }
            }
        }));

        fillStatsTable();
    }

    private void fillStatsTable() {

        cal_text.setText(String.format(Locale.getDefault(), "%.0f cal", selectedTrack.getCalories(userProfile.getWeight())));
        effort_text.setText(getString(selectedTrack.getEffortResource()));
        time_text.setText(DateUtils.millisToMinSec(selectedTrack.getDuration()));

        // Clean the segments table
        int count = dataTable.getChildCount();

        // First row is title.
        for (int i = count - 1; i >= 1; i--) {
            View child = dataTable.getChildAt(i);

            if (child instanceof TableRow) dataTable.removeViewAt(i);
        }

        // Fill the segments information table
        int i = 0;

        for (Segment segment : selectedTrack.getSegments()) {
            i++;
            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            if (i % 2 == 0) {
                row.setBackgroundColor(Utils.getColor(this, R.color.white));
            }
            else {
                row.setBackgroundColor(Utils.getColor(this, R.color.colorGraysoft));
            }

            // Convert from dp to pixels
            int paddingEnd = Utils.dp2px(10, this);
            int paddingStart = Utils.dp2px(10, this);
            int paddingTop = Utils.dp2px(3, this);
            int paddingBottom = Utils.dp2px(3, this);

            TableRow.LayoutParams intervalTextViewParams = new TableRow.LayoutParams();
            intervalTextViewParams.column = 0;

            TextView intervalTextView = new TextView(this);
            intervalTextView.setGravity(Gravity.CENTER);
            intervalTextView.setText(String.valueOf(i));
            intervalTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            intervalTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            intervalTextView.setLayoutParams(intervalTextViewParams);

            TableRow.LayoutParams imageViewParams = new TableRow.LayoutParams();
            imageViewParams.width = Utils.dp2px(10, this);
            imageViewParams.height = Utils.dp2px(20, this);
            imageViewParams.column = 1;

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(imageViewParams);
            imageView.setImageDrawable(Utils.getDrawable(this, R.drawable.rayo));
            imageView.setContentDescription(null);

            if (segment.getKind().equals(Segment.Kind.RUN) || segment.getKind().equals(Segment.Kind.SPRINT)) {
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }

            TableRow.LayoutParams speedTextViewParams = new TableRow.LayoutParams();
            speedTextViewParams.column = 2;

            TextView speedTextView = new TextView(this);
            speedTextView.setGravity(Gravity.CENTER);
            speedTextView.setText(String.format(Locale.getDefault(), "%.1f km", segment.getSpeed()));
            speedTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            speedTextView.setLayoutParams(speedTextViewParams);

            TableRow.LayoutParams slopeTextViewParams = new TableRow.LayoutParams();
            slopeTextViewParams.column = 3;

            TextView slopeTextView = new TextView(this);
            slopeTextView.setGravity(Gravity.CENTER);
            slopeTextView.setText(String.format(Locale.getDefault(), "%.1f", segment.getSlope()));
            slopeTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            slopeTextView.setLayoutParams(slopeTextViewParams);

            TableRow.LayoutParams durationTextViewParams = new TableRow.LayoutParams();
            durationTextViewParams.column = 4;

            TextView durationTextView = new TextView(this);
            durationTextView.setGravity(Gravity.CENTER);
            durationTextView.setText(String.format(Locale.getDefault(), "%s", DateUtils.millisToMinSec(segment.getDuration())));
            durationTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            durationTextView.setLayoutParams(durationTextViewParams);

            row.addView(intervalTextView, 0);
            row.addView(imageView, 1);
            row.addView(speedTextView, 2);
            row.addView(slopeTextView, 3);
            row.addView(durationTextView, 4);

            dataTable.addView(row, dataTable.getChildCount());
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void gotoRun(View v) {
        // After Android 6.0 we need to check app permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // We assign an activity wide request id
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA_REQUEST_ID);
            }
            else {
                // Older versions of Android
                openPreIndoorRun();
            }
        }
        // Permission already granted
        else {
            openPreIndoorRun();
        }
    }

    private void openPreIndoorRun() {
        Intent intentPlayer = new Intent(PreIndoorActivity.this, PreIndoorRunActivity.class);
        startActivity(intentPlayer);
        PreIndoorActivity.this.finish();
    }

    /**
     * Call back of permission request
     *
     * @param requestCode  The ID of the permission request we (randomly) assigned
     * @param permissions  The permission requested
     * @param grantResults Whether the permission was granted or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        openPreIndoorRun();
    }
}
