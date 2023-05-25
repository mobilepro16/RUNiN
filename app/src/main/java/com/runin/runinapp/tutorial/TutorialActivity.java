package com.runin.runinapp.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.TutorialViewPagerAdapter;
import com.runin.runinapp.indoor.IndoorDashboardActivity;
import com.runin.runinapp.settings.ProfileActivity;
import com.runin.runinapp.utils.ExtensionsKt;
import com.runin.runinapp.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Tutorial Screen with functions of the app
 * Created by Cesar on 09/08/2016
 */
public class TutorialActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener {
    private final int[] mImageResources = {
            R.mipmap.tutorial_bg_one,
            R.mipmap.tutorial_bg_two,
            R.mipmap.tutorial_bg_three,
            R.mipmap.tutorial_bg_four
    };
    private final int[] mTitlesResources = {
            R.string.welcome_tutorial_1,
            R.string.welcome_tutorial_2,
            R.string.welcome_tutorial_3,
            R.string.welcome_tutorial_4
    };
    private final int[] mDescriptionResources = {
            R.string.content_tutorial_1,
            R.string.content_tutorial_2,
            R.string.content_tutorial_3,
            R.string.content_tutorial_4
    };

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.btn_finish)
    Button btnFinish;

    @BindView(R.id.pager_introduction)
    ViewPager intro_images;

    @BindView(R.id.viewPagerCountDots)
    LinearLayout pager_indicator;
    @Inject
    SharedPreferences sharedPreferences;

    private int dotsCount;
    private ImageView[] dots;
    private TutorialViewPagerAdapter mAdapter;
    private boolean isFromTutorialScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intentOrigin = this.getIntent();

        if (getIntent() != null && intentOrigin.getExtras() != null) {
            if (intentOrigin.hasExtra(App.extrasFrom)) {
                String origin = intentOrigin.getExtras().getString(App.extrasFrom);
                if (origin != null && origin.equals(App.screenNameTutorials)) {
                    isFromTutorialScreen = true;
                }
            }
        }
        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        btnNext.setTypeface(Utils.getFontBebasNeue(this), Typeface.ITALIC);
        // Avoid font clipping
        btnNext.setText(String.format(" %s ", getString(R.string.next_button)));

        btnFinish.setTypeface(Utils.getFontBebasNeue(this), Typeface.ITALIC);
        // Avoid font clipping
        btnFinish.setText(String.format(" %s ", getString(R.string.finish_button)));


        mAdapter = new TutorialViewPagerAdapter(TutorialActivity.this, mImageResources, mTitlesResources, mDescriptionResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(this);

        setUiPageViewController();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(Utils.getDrawable(this, R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(Utils.getDrawable(this, R.drawable.selecteditem_dot));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                        ? intro_images.getCurrentItem() + 1 : 0);
                break;

            case R.id.btn_finish:
                if (!isFromTutorialScreen) {
                    ExtensionsKt.putBoolean(sharedPreferences, App.sharedPreferencesPropertyTutorialVisited, true);
                    Intent mainIntent = new Intent(this, IndoorDashboardActivity.class);
                    startActivity(mainIntent);
                }
                finish();

                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(Utils.getDrawable(this, R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(Utils.getDrawable(this, R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
