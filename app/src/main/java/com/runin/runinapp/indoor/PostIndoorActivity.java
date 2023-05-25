package com.runin.runinapp.indoor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.circleDisplay.CircleDisplay;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by thoma on 22/11/2016.
 */
public class PostIndoorActivity extends AppCompatActivity {
    //private static final String TAG = PostIndoorActivity.class.getSimpleName();

    @BindView(R.id.buttonEnd)
    Button buttonEnd;

    @BindView(R.id.screen_title)
    TextView screenTitle;

    @BindView(R.id.cal_level)
    TextView cal_level;

    @BindView(R.id.km_level)
    TextView km_level;

    @BindView(R.id.average_level)
    TextView average_level;

    @BindView(R.id.share)
    Button share;

    private Track selectedTrack;

    private TableLayout dataTable;
    @Inject
    User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IndoorAppState indoorAppState;
        CircleDisplay km_bar, avg_bar;

        super.onCreate(savedInstanceState);

        indoorAppState = ((App) getApplication()).getIndoorAppState();

        selectedTrack = indoorAppState.getSelectedTrack();

        setContentView(R.layout.activity_post_indoor);

        ButterKnife.bind(this);

        km_bar = findViewById(R.id.bar_km);
        avg_bar = findViewById(R.id.bar_avg);
        CircleDisplay cd = findViewById(R.id.circleDisplay_cal);

        cd.setTouchEnabled(false);
        cd.setAnimDuration(1500);
        cd.setValueWidthPercent(15f);
        cd.setTextSize(Utils.sp2px(17.5f, this));
        cd.setColor(Utils.getColor(this, R.color.colorRhythm));
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Utils.getColor(this, R.color.colorQS));
        cd.setPaint(CircleDisplay.PAINT_INNER, mPaint);
        cd.setFormatDigits(0);
        cd.setUnit("%");
        cd.setStepSize(1f);
        cd.showValue(100f, 100f, true);
        cal_level.setText(String.format(Locale.getDefault(), "%.0f", selectedTrack.getCalories(userProfile.getWeight())));

        km_bar.setTouchEnabled(false);
        km_bar.setAnimDuration(1500);
        km_bar.setValueWidthPercent(15f);
        km_bar.setTextSize(Utils.sp2px(10f, this));
        km_bar.setColor(Utils.getColor(this, R.color.colorTime));
        km_bar.setDrawText(true);
        km_bar.setDrawInnerCircle(true);
        km_bar.setPaint(CircleDisplay.PAINT_INNER, mPaint);
        km_bar.setFormatDigits(1);
        km_bar.setUnit("%");
        km_bar.setStepSize(0.5f);
        km_bar.showValue(100f, 100f, true);
        km_level.setText(String.format(Locale.getDefault(), "%.2f", selectedTrack.getDistance()));

        avg_bar.setTouchEnabled(false);
        avg_bar.setAnimDuration(1500);
        avg_bar.setValueWidthPercent(15f);
        avg_bar.setTextSize(Utils.sp2px(10f, this));
        avg_bar.setColor(Utils.getColor(this, R.color.colorYellowRunin));
        avg_bar.setDrawText(true);
        avg_bar.setDrawInnerCircle(true);
        avg_bar.setPaint(CircleDisplay.PAINT_INNER, mPaint);
        avg_bar.setFormatDigits(1);
        avg_bar.setUnit("%");
        avg_bar.setStepSize(0.5f);
        avg_bar.showValue(100f, 100f, true);

        average_level.setText(String.format(Locale.getDefault(), "%.1f", selectedTrack.getSpeed()));

        buttonEnd.setTypeface(Utils.getFontBebasNeue(PostIndoorActivity.this));
        screenTitle.setTypeface(Utils.getFontBebasNeue(PostIndoorActivity.this));
        share.setTypeface(Utils.getFontBebasNeue(this));

        // Change the icon
        ImageView badge = findViewById(R.id.imageView6);
        badge.setImageDrawable(Utils.getDrawable(this, indoorAppState.getSelectedTrack().getBadge().getRectangularIcon()));

        dataTable = findViewById(R.id.table_include).findViewById(R.id.Tabla);
        fillStatsTable();
    }

    @SuppressWarnings("UnusedParameters")
    public void gotoIndoor(View v) {
        String preferenceFile = App.sharedPreferencesFile;
        SharedPreferences sharedPreferences = getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);
        boolean indoorLevelSelectedAndConfirmed = sharedPreferences.getBoolean(App.sharedPreferencesIndoorLevelSelectedAndConfirmed, false);

        if (!indoorLevelSelectedAndConfirmed && selectedTrack.getId().equals(BuildConfig.DEBUG ? Track.DEMO_DEVEL_ID : Track.DEMO_ID)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(App.sharedPreferencesIndoorLevelSelectedAndConfirmed, true);
            editor.apply();

            Bundle args = new Bundle();
            args.putBoolean(App.extrasConfirmIndoorLevel, true);

            Intent intent = new Intent(this, SelectLevelActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }

        PostIndoorActivity.this.finish();
    }

    private void fillStatsTable() {

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
            } else {
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
            } else {
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

    public void setShare(@SuppressWarnings("unused") View view) {
        String packageName = "com.facebook.katana";
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent == null) {
            // Cannot open application
            Intent intent2 = new Intent(Intent.ACTION_SEND);
            intent2.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name));
            intent2.setType("text/plain");
            startActivity(Intent.createChooser(intent2, getResources().getText(R.string.send_to)));
        } else {
            Intent intentFb = new Intent(Intent.ACTION_SEND);
            intentFb.setType("text/plain");
            intentFb.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_url));
            intentFb.setPackage("com.facebook.katana");
            startActivity(intentFb);
        }
    }
}
