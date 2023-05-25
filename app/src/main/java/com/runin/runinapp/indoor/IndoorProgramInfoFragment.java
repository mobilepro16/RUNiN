package com.runin.runinapp.indoor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.Utils;

import java.util.Locale;

public class IndoorProgramInfoFragment extends DialogFragment {
    private final static String TAG = IndoorProgramInfoFragment.class.getSimpleName();

    private final Handler handler = new Handler();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                dismiss();
            } catch (Exception ex) {
                Log.e(TAG, "Cannot dismiss", ex);
            }
        }
    };

    private TextView txtTimeTotal;
    private TextView txtTimeSegment;
    private TableLayout dataTable;
    private Track selectedTrack;
    private int currentSegment;

    public IndoorProgramInfoFragment() {
        // Required empty public constructor
    }

    public static IndoorProgramInfoFragment newInstance(Track selectedTrack, int currentSegment) {
        IndoorProgramInfoFragment fragment = new IndoorProgramInfoFragment();
        fragment.setSelectedTrack(selectedTrack);
        fragment.setCurrentSegment(currentSegment);
        return fragment;
    }

    private void setSelectedTrack(Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    private void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_indoor_program_info, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        dataTable = view.findViewById(R.id.table_include).findViewById(R.id.Tabla);
        txtTimeTotal = view.findViewById(R.id.time_total);
        txtTimeSegment = view.findViewById(R.id.time_segment);

        updateTable();

        return view;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {

        // Hide after some seconds
        handler.postDelayed(runnable, 5000);

        return super.show(transaction, tag);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        handler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();

        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);
    }

    private void updateTable() {
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
            TableRow row = new TableRow(this.getContext());
            row.setGravity(Gravity.CENTER);

            if (i % 2 == 0) {
                row.setBackgroundColor(Utils.getColor(this.getContext(), R.color.white));
            }
            else {
                row.setBackgroundColor(Utils.getColor(this.getContext(), R.color.colorGraysoft));
            }

            if (i - 1 == currentSegment) {
                row.setBackgroundColor(Utils.getColor(this.getContext(), R.color.colorDistance));
            }

            // Convert from dp to pixels
            int paddingEnd = Utils.dp2px(10, this.getContext());
            int paddingStart = Utils.dp2px(10, this.getContext());
            int paddingTop = Utils.dp2px(3, this.getContext());
            int paddingBottom = Utils.dp2px(3, this.getContext());

            TableRow.LayoutParams intervalTextViewParams = new TableRow.LayoutParams();
            intervalTextViewParams.column = 0;

            TextView intervalTextView = new TextView(this.getContext());
            intervalTextView.setGravity(Gravity.CENTER);
            intervalTextView.setText(String.valueOf(i));
            intervalTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            intervalTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            intervalTextView.setLayoutParams(intervalTextViewParams);

            TableRow.LayoutParams imageViewParams = new TableRow.LayoutParams();
            imageViewParams.width = Utils.dp2px(10, this.getContext());
            imageViewParams.height = Utils.dp2px(20, this.getContext());
            imageViewParams.column = 1;

            ImageView imageView = new ImageView(this.getContext());
            imageView.setLayoutParams(imageViewParams);
            imageView.setImageDrawable(Utils.getDrawable(this.getContext(), R.drawable.rayo));
            imageView.setContentDescription(null);

            if (segment.getKind().equals(Segment.Kind.RUN) || segment.getKind().equals(Segment.Kind.SPRINT)) {
                imageView.setVisibility(View.VISIBLE);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }

            TableRow.LayoutParams speedTextViewParams = new TableRow.LayoutParams();
            speedTextViewParams.column = 2;

            TextView speedTextView = new TextView(this.getContext());
            speedTextView.setGravity(Gravity.CENTER);
            speedTextView.setText(String.format(Locale.getDefault(), "%.1f km", segment.getSpeed()));
            speedTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            speedTextView.setLayoutParams(speedTextViewParams);

            TableRow.LayoutParams slopeTextViewParams = new TableRow.LayoutParams();
            slopeTextViewParams.column = 3;

            TextView slopeTextView = new TextView(this.getContext());
            slopeTextView.setGravity(Gravity.CENTER);
            slopeTextView.setText(String.format(Locale.getDefault(), "%.1f", segment.getSlope()));
            slopeTextView.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            slopeTextView.setLayoutParams(slopeTextViewParams);

            TableRow.LayoutParams durationTextViewParams = new TableRow.LayoutParams();
            durationTextViewParams.column = 4;

            TextView durationTextView = new TextView(this.getContext());
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

    public void updateTimes(long timeTotal, long timeSegment) {
        txtTimeTotal.setText(DateUtils.millisToMinSec(timeTotal));
        txtTimeSegment.setText(DateUtils.millisToMinSec(timeSegment));
    }
}
