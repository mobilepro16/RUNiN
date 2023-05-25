package com.runin.runinapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.circleDisplay.CircleDisplay;

import java.util.Locale;

/**
 * Please document this class
 * Created by Citrus01 on 10/06/2017.
 */
public class OutdoorImproveSwipeAdapter extends PagerAdapter {

    private final Context context;

    public OutdoorImproveSwipeAdapter(Context context) {

        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.swipe_layout_improve, null);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.otf");
        float cdPercentage;

        TextView textView = view.findViewById(R.id.screen_title2);
        ImageView circle1 = view.findViewById(R.id.circul1);
        ImageView circle2 = view.findViewById(R.id.circul2);
        ImageView circle3 = view.findViewById(R.id.circul3);
        TextView numberToday = view.findViewById(R.id.num_hoy);
        TextView med_hoy = view.findViewById(R.id.med_hoy);
        TextView kmRec2 = view.findViewById(R.id.kmRec2);
        TextView element1 = view.findViewById(R.id.elemnt1);
        TextView element2 = view.findViewById(R.id.elemnt2);
        TextView element4 = view.findViewById(R.id.elemnt4);
        TextView element5 = view.findViewById(R.id.elemnt5);
        TextView element6 = view.findViewById(R.id.elemnt6);
        LinearLayout data = view.findViewById(R.id.linearContainer);
        CircleDisplay circleDisplay = view.findViewById(R.id.circleDisplay);

        numberToday.setTypeface(font);
        med_hoy.setTypeface(font);
        element1.setTypeface(font);
        element2.setTypeface(font);
        element5.setTypeface(font);
        element6.setTypeface(font);

        cdPercentage = 0f;

        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Utils.getColor(context, R.color.improve_middle));

        circleDisplay.setTouchEnabled(false);
        circleDisplay.setAnimDuration(3000);
        circleDisplay.setValueWidthPercent(13f);
        circleDisplay.setTextSize(Utils.sp2px(23f, context));
        circleDisplay.setColor(Color.WHITE);
        circleDisplay.setDrawText(true);
        circleDisplay.setDrawInnerCircle(true);
        circleDisplay.setPaint(CircleDisplay.PAINT_INNER, mPaint);
        circleDisplay.setFormatDigits(1);
        circleDisplay.setUnit("%");
        circleDisplay.setStepSize(0.5f);
        circleDisplay.showValue(cdPercentage, 100f, true);

        OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        if (position == 0) {
            circleDisplay.setVisibility(View.GONE);
            data.setVisibility(View.VISIBLE);

            Double nowKm = outdoorWorkoutsHistoryOperationsDB.getTodayDistance(context);
            Double totalKm = outdoorWorkoutsHistoryOperationsDB.getTotalDistance(context);
            Double lastKm = outdoorWorkoutsHistoryOperationsDB.getLastDistance(context);

            numberToday.setText(String.format(Locale.getDefault(), "%.1f", nowKm));
            element1.setText(String.format(Locale.getDefault(), "%.1f", totalKm));
            element5.setText(String.format(Locale.getDefault(), "%.1f", lastKm));
            textView.setText(context.getString(R.string.results));
            circle1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
            circle3.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
        }
        if (position == 1) {
            circleDisplay.setVisibility(View.GONE);
            data.setVisibility(View.VISIBLE);
            numberToday.setText("0");
            textView.setText(context.getString(R.string.results));
            circle1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
            circle3.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
        }

        if (position == 2) {
            circleDisplay.setVisibility(View.GONE);
            data.setVisibility(View.VISIBLE);
            numberToday.setText("0");
            textView.setText(context.getString(R.string.results));
            med_hoy.setText(context.getString(R.string.calories_abbr));
            kmRec2.setText(context.getString(R.string.calories_burned_today));
            element2.setText(context.getString(R.string.calories_abbr));
            element4.setText(context.getString(R.string.calories_total));
            element6.setText(context.getString(R.string.calories_abbr));
            circle1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle3.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
