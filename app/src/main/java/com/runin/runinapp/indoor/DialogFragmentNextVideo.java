package com.runin.runinapp.indoor;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;

import java.text.DecimalFormat;

public class DialogFragmentNextVideo extends DialogFragment {

    private static final String TAG = DialogFragmentNextVideo.class.getSimpleName();
    private static OnDismissListener listener;
    private ValueAnimator animator;
    private TextView txtMessage;
    private Double speed;
    private Double slope;

    public static DialogFragmentNextVideo newInstance(OnDismissListener dismissListener, Double speed, Double slope) {
        DialogFragmentNextVideo f = new DialogFragmentNextVideo();

        f.setSlope(slope);
        f.setSpeed(speed);

        listener = dismissListener;
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();

        animateTexts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.awake_layout_next, container);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        else {
            Log.e(TAG, "getDialog or getWindow is null");
        }

        txtMessage = view.findViewById(R.id.txMain);

        DecimalFormat df = new DecimalFormat("##.#");

        String speed = df.format(this.speed);
        String slope = df.format(this.slope);
        String degree = getString(R.string.degree);
        String degrees = getString(R.string.degrees);

        if (getActivity() != null) {
            ((App) getActivity().getApplication()).speakOutFlush(getString(R.string.prepare_next_video_speak, speed, slope, this.slope == 1.0 ? degree : degrees));
        }

        TextView slopeTextView = view.findViewById(R.id.slope);
        TextView speedTextView = view.findViewById(R.id.speed);

        speedTextView.setText(speed);
        slopeTextView.setText(slope);

        return view;
    }

    private void animateTexts() {
        animator = new ValueAnimator();
        animator.setObjectValues(5, 0);
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int res = Math.round(startValue + (endValue - startValue) * fraction);

                try {
                    if (res == 3) {
                        txtMessage.setText(getString(R.string.ready));
                    }

                    if (res == 1) {
                        txtMessage.setText(getString(R.string.LetsGo));
                    }

                    if (res == 0) {
                        if (getDialog() != null) {
                            animator.cancel();
                            getDialog().dismiss();
                        }
                        else {
                            Log.e(TAG, "getDialog() is null");
                        }
                    }
                } catch (IllegalStateException ex) {
                    // The dialog might already have been gone and this animator is still working
                    Log.e(TAG, "Animator working on a ghost dialog", ex);
                }

                return res;
            }
        });
        animator.setDuration(10000);
        animator.start();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            if (animator.isRunning()) {
                animator.cancel();
            }

            super.onDismiss(dialog);
            listener.onDismissDialogFragmentNextVideo();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "Illegal state", ex);
        }
    }

    private void setSpeed(Double speed) {
        this.speed = speed;
    }

    private void setSlope(Double slope) {
        this.slope = slope;
    }

    /**
     * Notifies the calling object that the animation has finished.
     */
    public interface OnDismissListener {
        void onDismissDialogFragmentNextVideo();
    }
}