package com.runin.runinapp.indoor;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
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

public class ReadySetGoDialogFragment extends DialogFragment {

    private static final String TAG = ReadySetGoDialogFragment.class.getSimpleName();
    private static OnDismissListener listener;
    private ValueAnimator animator;
    private TextView messageText;
    private TextView countText;

    public static ReadySetGoDialogFragment newInstance(OnDismissListener dismissListener) {
        ReadySetGoDialogFragment f = new ReadySetGoDialogFragment();

        listener = dismissListener;
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();

        setCountText();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ready_set_go, container);

        messageText = view.findViewById(R.id.message_text);
        countText = view.findViewById(R.id.count_text);
        TextView cancel = view.findViewById(R.id.cancel_button);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                }
                if (getActivity() != null) {
                    ((App) getActivity().getApplication()).cancelSpeak();
                }
                dismiss();
            }
        });

        return view;
    }

    private void setCountText() {
        animator = new ValueAnimator();
        animator.setObjectValues(5, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                String number = String.valueOf(animation.getAnimatedValue());
                countText.setText(number);

            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int res = Math.round(startValue + (endValue - startValue) * fraction);

                if (res == 3) {
                    messageText.setText(R.string.ready);
                }

                if (res == 1) {
                    messageText.setText(R.string.LetsGo);
                }

                if (res == 0) {
                    // Dialog can be null if user hits cancel button
                    if (getDialog() != null) {
                        animator.cancel();
                        getDialog().dismiss();
                    }
                    else {
                        Log.w(TAG, "getDialog() is null");
                    }
                }

                return res;
            }
        });
        animator.setDuration(6000);
        animator.start();
        if (getActivity() != null) {
            ((App) getActivity().getApplication()).speakOut(getString(R.string.starting_race));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            //     Thread.sleep(1000);

            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }

            super.onDismiss(dialog);
            listener.onDismissDialogFragmentReadySetGo();
        } catch (Exception e) {
            Log.e(TAG, "Something happened", e);
        }

    }

    public interface OnDismissListener {
        void onDismissDialogFragmentReadySetGo();
    }
}