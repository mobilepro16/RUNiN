package com.runin.runinapp.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.tutorial.TutorialActivity;
import com.runin.runinapp.utils.detector.MotionDetectionActivity;

import butterknife.ButterKnife;

/**
 * Settings menu
 * Created by Citrus01 on 28/05/2017.
 */

public class DashboardSettingsFragment extends Fragment {

    public DashboardSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard_settings, container, false);
        ButterKnife.bind(this, view);
        ListView listViewSettings = view.findViewById(R.id.list_settings);

        // Defined Array values to show in ListView
        String[] values;

        if (BuildConfig.DEBUG) {
            values = new String[9];
        }
        else {
            values = new String[8];
        }

        //values[0] = this.getString(R.string.restore_buys);
        values[0] = this.getString(R.string.manage_videos);
        values[1] = this.getString(R.string.tutorial);
        values[2] = this.getString(R.string.help);
        values[3] = this.getString(R.string.about_us);
        values[4] = this.getString(R.string.rate_app);
        values[5] = this.getString(R.string.report_error);
        values[6] = this.getString(R.string.terms_use);
        values[7] = this.getString(R.string.privacy_notice);

        if (BuildConfig.DEBUG) {
            values[8] = getString(R.string.motion_detection);
        }

        if (getContext() != null && getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, values);

            listViewSettings.setAdapter(adapter);

            // ListView Item Click Listener
            listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // ListView Clicked item index
                    switch (position) {
                        //case 0:
                        //    restoreAdFree();
                        //    break;
                        case 0:
                            Intent intentManageVideos = new Intent(getContext(), SettingsManageVideosActivity.class);
                            startActivity(intentManageVideos);
                            break;
                        case 1:
                            Intent intentTutorial = new Intent(getContext(), TutorialActivity.class);
                            intentTutorial.putExtra(App.extrasFrom, App.screenNameTutorials);
                            startActivity(intentTutorial);
                            break;
                        case 2:
                            Intent intentInformation = new Intent(getContext(), InformationAppActivity.class);
                            intentInformation.putExtra(App.extrasFragment, App.extrasSettingsHelp);
                            startActivity(intentInformation);
                            break;
                        case 3:
                            Intent intentInformation1 = new Intent(getContext(), InformationAppActivity.class);
                            intentInformation1.putExtra(App.extrasFragment, App.extrasSettingsAboutUs);
                            startActivity(intentInformation1);
                            break;
                        case 4:
                            final String appPackageName = getActivity().getPackageName();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            break;
                        case 5:
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_contact)});
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_an_error));
                            try {
                                startActivity(Intent.createChooser(i, getString(R.string.send_email)));

                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getContext(),
                                        R.string.there_are_no_email_clients,
                                        Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 6:
                            Intent intentInformation2 = new Intent(getContext(), InformationAppActivity.class);
                            intentInformation2.putExtra(App.extrasFragment, App.extrasSettingsTerms);
                            startActivity(intentInformation2);
                            break;
                        case 7:
                            Intent intentInformation3 = new Intent(getContext(), InformationAppActivity.class);
                            intentInformation3.putExtra(App.extrasFragment, App.extrasSettingsPrivacy);
                            startActivity(intentInformation3);
                            break;
                        case 8:
                            Intent motionDetectionIntent = new Intent(getContext(), MotionDetectionActivity.class);
                            startActivity(motionDetectionIntent);
                    }
                }
            });
        }
        return view;
    }

}
