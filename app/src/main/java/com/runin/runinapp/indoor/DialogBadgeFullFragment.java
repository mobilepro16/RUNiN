package com.runin.runinapp.indoor;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.Badge;
import com.runin.runinapp.utils.Utils;

/**
 * Shows a full screen badge icon with a description text
 *
 * Created by Omar Sevilla  on 05/01/2017.
 */

public class DialogBadgeFullFragment extends DialogFragment {
    private static final String TAG = DialogBadgeFullFragment.class.getSimpleName();

    private Badge badge;

    public static DialogBadgeFullFragment newInstance(Badge badge) {
        DialogBadgeFullFragment f = new DialogBadgeFullFragment();

        f.setBadge(badge);

        return f;
    }

    private void setBadge(Badge badge) {
        this.badge = badge;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_badge, container);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        else {
            Log.e(TAG, "getDialog or getWindow is null");
        }

        ImageView imgButton = view.findViewById(R.id.img_badge);
        ImageView imgButton2 = view.findViewById(R.id.img_badge2);
        TextView txt_badge = view.findViewById(R.id.txt_badge);

        Drawable mDraw = Utils.getDrawable(getContext(), badge.getIcon());
        String tDraw = getString(badge.getDescription());

        // WTF??? Why two times an image one over the other?

        imgButton.setImageDrawable(mDraw);
        imgButton2.setImageDrawable(mDraw);
        txt_badge.setText(tDraw);
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
