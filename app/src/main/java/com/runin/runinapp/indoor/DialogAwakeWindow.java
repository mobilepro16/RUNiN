package com.runin.runinapp.indoor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.utils.Utils;

/**
 * Dialog for the Are you Still there?
 */
public class DialogAwakeWindow extends DialogFragment {
    private static final String TAG = DialogAwakeWindow.class.getSimpleName();

    private static OnStillListener listener;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (DialogAwakeWindow.this.isVisible()) {
                listener.onFinishDialogAwakeWindow();
            }
        }
    };

    private Handler handler;

    public static DialogAwakeWindow newInstance(OnStillListener dismissListener) {
        DialogAwakeWindow dialogAwakeWindow = new DialogAwakeWindow();

        listener = dismissListener;
        return dialogAwakeWindow;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        handler = new Handler();

        handler.postDelayed(runnable, 60000);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.awake_layout, container);
        LinearLayout accept;
        TextView txtFinishButton, still, sure, sure2;

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        else {
            Log.e(TAG, "getDialog or getWindow is null");
        }

        accept = view.findViewById(R.id.acceptstill2);
        txtFinishButton = view.findViewById(R.id.finish_btn);
        still = view.findViewById(R.id.still);
        sure = view.findViewById(R.id.sure);
        sure2 = view.findViewById(R.id.sure2);


        still.setTypeface(Utils.getFontBebasNeue(getContext()));
        sure.setTypeface(Utils.getFontBebasNeue(getContext()));
        sure2.setTypeface(Utils.getFontBebasNeue(getContext()));

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onStillDialogAwakeWindow();
            }
        });

        txtFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onFinishDialogAwakeWindow();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public interface OnStillListener {
        void onStillDialogAwakeWindow();

        void onFinishDialogAwakeWindow();
    }
}