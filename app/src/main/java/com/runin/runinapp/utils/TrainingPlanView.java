package com.runin.runinapp.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.runin.runinapp.R;
import com.runin.runinapp.data.Phase;

import java.util.List;

public class TrainingPlanView extends View {
    private static final String TAG = TrainingPlanView.class.getSimpleName();
    private List<Phase> phases = null;

    private int baseLine = 0;

    private int warmUpColor = getResources().getColor(R.color.greenRunin);
    private int trainingColor = getResources().getColor(R.color.colorCal);
    private int recoveryColor = getResources().getColor(R.color.colorGrayMenu);
    private int coolDownColor = getResources().getColor(R.color.greenRunin);
    private int trialColor = getResources().getColor(R.color.colorDistance);
    private int runColor = getResources().getColor(R.color.colorAccent);

    private Paint myPaintWarmUp;
    private Paint myPaintTraining;
    private Paint myPaintRecovery;
    private Paint myPaintCoolDown;
    private Paint myPaintTrial;
    private Paint myPaintRun;

    private double factorW = 1.0;
    private double factorH = 1.0;
    private int dp2px = 1;
    private int spacing = 5;
    private int maxH = 0;

    public TrainingPlanView(Context context) {
        super(context);
        init(null, 0);
    }

    public TrainingPlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TrainingPlanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TrainingPlanView, defStyle, 0);

        warmUpColor = a.getColor(R.styleable.TrainingPlanView_warmUpColor, warmUpColor);
        trainingColor = a.getColor(R.styleable.TrainingPlanView_trainingColor, trainingColor);
        recoveryColor = a.getColor(R.styleable.TrainingPlanView_recoveryColor, recoveryColor);
        coolDownColor = a.getColor(R.styleable.TrainingPlanView_coolDownColor, coolDownColor);
        trialColor = a.getColor(R.styleable.TrainingPlanView_trialColor, trialColor);
        runColor = a.getColor(R.styleable.TrainingPlanView_runColor, runColor);

        a.recycle();

        myPaintWarmUp = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintWarmUp.setColor(warmUpColor);

        myPaintTraining = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintTraining.setColor(trainingColor);

        myPaintRecovery = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintRecovery.setColor(recoveryColor);

        myPaintCoolDown = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintCoolDown.setColor(coolDownColor);

        myPaintTrial = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintTrial.setColor(trialColor);

        myPaintRun = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaintRun.setColor(runColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, String.format("left: %d, top: %d, right: %d, bottom: %d", getPaddingLeft(), 60, 20, baseLine));

        int marginLeft = getPaddingLeft();

        if (phases != null) {
            for (Phase phase : phases) {

                int h = 0;
                int w = 0;
                Paint paint = null;

                switch (phase.getKind()) {
                    case WARM_UP:
                        h = 20 * dp2px;
                        w = 30 * dp2px;
                        paint = myPaintWarmUp;
                        break;
                    case TRAINING:
                        h = 40 * dp2px;
                        w = 20 * dp2px;
                        paint = myPaintTraining;
                        break;
                    case RECOVERY:
                        h = 20 * dp2px;
                        w = 20 * dp2px;
                        paint = myPaintRecovery;
                        break;
                    case COOL_DOWN:
                        h = 20 * dp2px;
                        w = 30 * dp2px;
                        paint = myPaintCoolDown;
                        break;
                    case TRIAL:
                        h = 20 * dp2px;
                        w = 20 * dp2px;
                        paint = myPaintTrial;
                        break;
                    case RUN:
                        h = 60 * dp2px;
                        w = 20 * dp2px;
                        paint = myPaintRun;
                        break;
                }

                h = (int) ((double) h * factorH);
                w = (int) ((double) w * factorW);

                canvas.drawRect(marginLeft, baseLine - h, marginLeft + w, baseLine, paint);
                marginLeft += w + spacing;
            }
        }
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;

        // Redraw the view
        invalidate();
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        dp2px = Utils.dp2px(1, getContext());

        int totalWidth = 0;

        if (phases != null) {
            for (Phase phase : phases) {
                if (phase.getKind() == Phase.Kind.WARM_UP || phase.getKind() == Phase.Kind.COOL_DOWN)
                // Warm up and cool down are wider than the others
                {
                    totalWidth += 30 * dp2px;
                }
                else {
                    totalWidth += 20 * dp2px;
                }

                switch (phase.getKind()) {
                    case WARM_UP:
                        if (maxH < 20 * dp2px) maxH = 20 * dp2px;
                        break;
                    case TRAINING:
                        if (maxH < 40 * dp2px) maxH = 40 * dp2px;
                        break;
                    case RECOVERY:
                        if (maxH < 20 * dp2px) maxH = 20 * dp2px;
                        break;
                    case COOL_DOWN:
                        if (maxH < 20 * dp2px) maxH = 20 * dp2px;
                        break;
                    case TRIAL:
                        if (maxH < 20 * dp2px) maxH = 20 * dp2px;
                        break;
                    case RUN:
                        if (maxH < 60 * dp2px) maxH = 60 * dp2px;
                        break;
                }
            }

            // Spacing
            totalWidth += (phases.size() - 1) * 5 * dp2px;
            factorW = 1.0 * (w - getPaddingEnd() - getPaddingStart()) / totalWidth;
            factorH = 1.0 * (h - getPaddingBottom() - getPaddingTop()) / maxH;
            spacing = (int) (5.0 * dp2px * factorW);
        }

        baseLine = h - getPaddingBottom();

        Log.i(TAG, String.format("Factor W: %f, Factor H: %f, Base Line Y: %d, MaxH: %d", factorW, factorH, baseLine, maxH));
    }
}
