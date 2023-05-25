package com.runin.runinapp.indoor;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.BadgeGridItemAdapter;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Badge;

import java.util.ArrayList;

public class IndoorBadgesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_badges);

        IndoorAppState indoorAppState = ((App) getApplication()).getIndoorAppState();
        final ArrayList<Badge> badges = indoorAppState.getBadges();

        GridView gridview = findViewById(R.id.gridview);
        TextView title = findViewById(R.id.screen_title);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        gridview.setAdapter(new BadgeGridItemAdapter(this, badges));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        title.setTypeface(font);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Badge badge = badges.get(position);
                showPopUp(badge);
            }
        });
    }

    private void showPopUp(Badge badge) {
        if (badge.isCompleted()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogBadgeFullFragment newFragment = DialogBadgeFullFragment.newInstance(badge);
            newFragment.show(ft, "dialog");
        }
        else {
            Toast.makeText(this, R.string.badge_not_obtained, Toast.LENGTH_SHORT).show();
        }
    }
}
