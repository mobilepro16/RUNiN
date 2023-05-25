package com.runin.runinapp.indoor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.circleDisplay.CircleDisplay;

import java.util.Locale;


/**
 * Who is Citrus?
 * Created by Citrus01 on 23/04/2017.
 */

class SwipeAdapter extends PagerAdapter {
    private static final String TAG = PagerAdapter.class.getSimpleName();
    private final int[] image_swipe = {R.drawable.porcentaje, R.drawable.kilometros, R.drawable.calorias3};
    private final Context context;
    private double weight;

    SwipeAdapter(Context context) {
        this.context = context;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int getCount() {
        return image_swipe.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.swipe_layout, null);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.otf");
        IndoorAppState indoorAppState = ((App) context.getApplicationContext()).getIndoorAppState();

        TextView textView = view.findViewById(R.id.screen_title2);
        ImageView circle1 = view.findViewById(R.id.circul1);
        ImageView circle2 = view.findViewById(R.id.circul2);
        ImageView circle3 = view.findViewById(R.id.circul3);
        TextView num_hoy = view.findViewById(R.id.num_hoy);
        TextView med_hoy = view.findViewById(R.id.med_hoy);
        TextView kmRec2 = view.findViewById(R.id.kmRec2);
        TextView element1 = view.findViewById(R.id.elemnt1);
        TextView element2 = view.findViewById(R.id.elemnt2);
        TextView element4 = view.findViewById(R.id.elemnt4);
        TextView element5 = view.findViewById(R.id.elemnt5);
        TextView element6 = view.findViewById(R.id.elemnt6);
        LinearLayout data = view.findViewById(R.id.linearContainer);

        num_hoy.setTypeface(font);
        med_hoy.setTypeface(font);
        element1.setTypeface(font);
        element2.setTypeface(font);
        element5.setTypeface(font);
        element6.setTypeface(font);

        CircleDisplay cd = view.findViewById(R.id.circleDisplay);
        cd.setTouchEnabled(false);
        cd.setAnimDuration(1500);
        cd.setValueWidthPercent(13f);
        cd.setTextSize(Utils.sp2px(35f, context));
        cd.setColor(Color.WHITE);
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);

        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Utils.getColor(context, R.color.colorQS));
        cd.setPaint(CircleDisplay.PAINT_INNER, mPaint);
        cd.setFormatDigits(0);

        cd.setUnit("%");
        cd.setStepSize(1f);

        Double cdPercentage = indoorAppState.getPercentageAdvanceIndoor() * 100;

        Log.i(TAG, "cd PERCENTAGE: " + String.valueOf(cdPercentage));

        // cd.setCustomText(...); // sets a custom array of text
        cd.showValue(cdPercentage.floatValue(), 100f, true);


        if (position == 0) {

            textView.setText(context.getString(R.string.advance_percentage));
            cd.setVisibility(View.VISIBLE);
            data.setVisibility(View.GONE);

            circle1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
            circle2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle3.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
        }
        if (position == 1) {
            cd.setVisibility(View.GONE);
            data.setVisibility(View.VISIBLE);

            num_hoy.setText(String.format(Locale.getDefault(), "%.2f", indoorAppState.getDistanceToday()));
            element1.setText(String.format(Locale.getDefault(), "%.2f", indoorAppState.getDistanceTotal()));
            element5.setText(String.format(Locale.getDefault(), "%.2f", indoorAppState.getDistanceLastTraining()));


            textView.setText(context.getString(R.string.results));
            circle1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            circle2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
            circle3.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
        }

        if (position == 2) {
            cd.setVisibility(View.GONE);
            data.setVisibility(View.VISIBLE);

            num_hoy.setText(String.format(Locale.getDefault(), "%.0f", indoorAppState.getCaloriesToday(weight)));
            element1.setText(String.format(Locale.getDefault(), "%.0f", indoorAppState.getCaloriesTotal(weight)));
            element5.setText(String.format(Locale.getDefault(), "%.0f", indoorAppState.getCaloriesLastTraining(weight)));

            textView.setText(context.getString((R.string.results)));
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
