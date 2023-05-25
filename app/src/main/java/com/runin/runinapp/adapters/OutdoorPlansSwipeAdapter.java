package com.runin.runinapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.utils.RoutineUtils;
import com.runin.runinapp.utils.Utils;

import java.util.Locale;

/**
 * Please document this class.
 * Created by Citrus01 on 12/06/2017.
 */
public class OutdoorPlansSwipeAdapter extends PagerAdapter {
    private final Context context;

    private final OutdoorAppState outdoorAppState;

    public OutdoorPlansSwipeAdapter(Context context, OutdoorAppState outdoorAppState) {
        this.context = context;
        this.outdoorAppState = outdoorAppState;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.swipe_layout_plans, null);

        OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.otf");

        Plan selectedPlan = outdoorAppState.getSelectedPlan();

        ImageView img_plan = view.findViewById(R.id.image_plan);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        ImageView dot1 = view.findViewById(R.id.dot1);
        ImageView dot2 = view.findViewById(R.id.dot2);
        TextView todayMeasure = view.findViewById(R.id.todayMeasure);
        TextView todayUnit = view.findViewById(R.id.todayUnit);
        TextView totalMeasure = view.findViewById(R.id.totalMeasure);
        TextView totalUnit = view.findViewById(R.id.totalUnit);
        TextView lastMeasure = view.findViewById(R.id.lastMeasure);
        TextView lastUnit = view.findViewById(R.id.lastUnit);
        LinearLayout dataContainer = view.findViewById(R.id.dataContainer);
        LinearLayout progress = view.findViewById(R.id.progress_plan);

        todayMeasure.setTypeface(font);
        todayUnit.setTypeface(font);
        totalMeasure.setTypeface(font);
        totalUnit.setTypeface(font);
        lastMeasure.setTypeface(font);
        lastUnit.setTypeface(font);

        img_plan.setImageResource(outdoorAppState.getPlanLargeIcon(selectedPlan.getTitle()));

        String kmNow = String.format(Locale.getDefault(), "%.1f", outdoorWorkoutsHistoryOperationsDB.getTodayDistance(context));
        String kmLast = String.format(Locale.getDefault(), "%.1f", outdoorWorkoutsHistoryOperationsDB.getLastDistance(context));
        String kmTotal = String.format(Locale.getDefault(), "%.1f", outdoorWorkoutsHistoryOperationsDB.getTotalDistance(context));

        if (position == 0) {
            progress.setVisibility(View.GONE);
            dataContainer.setVisibility(View.VISIBLE);
            todayMeasure.setText(String.valueOf(RoutineUtils.roundTwoDecimals(Double.parseDouble(kmNow))));
            totalMeasure.setText(String.valueOf(RoutineUtils.roundTwoDecimals(Double.parseDouble(kmTotal))));
            lastMeasure.setText(String.valueOf(RoutineUtils.roundTwoDecimals(Double.parseDouble(kmLast))));
            txtTitle.setText(context.getString(R.string.results));

            dot1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
            dot2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
        }

        // Originally the pager had another position for calories. I deleted them (SK)

        if (position == 1) {
            txtTitle.setText(R.string.progress);
            progress.setVisibility(View.VISIBLE);
            dataContainer.setVisibility(View.GONE);

            dot1.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide_opaque));
            dot2.setImageDrawable(Utils.getDrawable(context, R.drawable.circul_slide));
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
