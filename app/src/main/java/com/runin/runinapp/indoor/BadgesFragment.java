package com.runin.runinapp.indoor;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.BadgeGridItemAdapter;
import com.runin.runinapp.data.Badge;
import com.runin.runinapp.data.IndoorAppState;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Who is Usuario?
 * Created by Usuario on 01/06/2017.
 */

public class BadgesFragment extends Fragment {
    //private static final String TAG = BadgesFragment.class.getSimpleName();

    public BadgesFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badges, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() != null) {
            IndoorAppState indoorAppState;
            final ArrayList<Badge> badges;

            indoorAppState = ((App) getActivity().getApplication()).getIndoorAppState();
            badges = indoorAppState.getBadges();

            GridView gridview = view.findViewById(R.id.gridview);
            TextView title = view.findViewById(R.id.screen_title);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            if (getContext() != null) {
                gridview.setAdapter(new BadgeGridItemAdapter(getContext(), badges));
                Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/BebasNeue.otf");
                title.setTypeface(font);
            }

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Badge badge = badges.get(position);
                    showPopUp(badge);
                }
            });
        }

        return view;
    }

    private void showPopUp(Badge badge) {
        if (badge.isCompleted() && getActivity() != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogBadgeFullFragment newFragment = DialogBadgeFullFragment.newInstance(badge);
            newFragment.show(ft, "dialog");
        }
        else {
            Toast.makeText(getContext(), R.string.badge_not_obtained, Toast.LENGTH_SHORT).show();
        }
    }
}
