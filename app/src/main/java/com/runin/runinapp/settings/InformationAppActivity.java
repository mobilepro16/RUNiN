package com.runin.runinapp.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.runin.runinapp.App;
import com.runin.runinapp.R;

public class InformationAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_app);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intentAppActivity = getIntent();

        if (intentAppActivity.getExtras() != null) {
            String origin = intentAppActivity.getExtras().getString(App.extrasFragment);

            Fragment currentFragment = null;
            if (origin != null) {
                switch (origin) {
                    case App.extrasSettingsHelp:
                        currentFragment = new HelpUsFragment();
                        break;
                    case App.extrasSettingsAboutUs:
                        currentFragment = new AboutUsFragment();
                        break;
                    case App.extrasSettingsTerms:
                        currentFragment = new TermsFragment();
                        break;
                    case App.extrasSettingsPrivacy:
                        currentFragment = new PrivacyFragment();
                        break;
                }
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction().add(R.id.your_placeholder, currentFragment).commit();
            }
        }
    }
}
