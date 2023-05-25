package com.runin.runinapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class SettingsManageVideosActivity extends Activity {
    private static final String TAG = SettingsManageVideosActivity.class.getSimpleName();

    private ArrayList<Track> indoorTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_manage_videos);

        IndoorAppState indoorAppState = ((App) getApplication()).getIndoorAppState();
        indoorTracks = indoorAppState.getIndoorTracks();

        setVisibilityOfButtons();
    }

    private void setVisibilityOfButtons() {
        Button demo = findViewById(R.id.demo);
        Button harder_and_stronger = findViewById(R.id.harder_and_stronger);
        Button higher_and_faster = findViewById(R.id.higher_and_faster);
        Button further_and_longer = findViewById(R.id.further_and_longer);

        for (Track track : indoorTracks) {
            switch (track.getId()) {
                case Track.DEMO_ID:
                case Track.DEMO_DEVEL_ID:
                    demo.setEnabled(track.isPartiallyDownloaded());
                    break;
                case Track.HARDER_AND_STRONGER_ID:
                    harder_and_stronger.setEnabled(track.isPartiallyDownloaded());
                    break;
                case Track.HIGHER_AND_FASTER_ID:
                    higher_and_faster.setEnabled(track.isPartiallyDownloaded());
                    break;
                case Track.FURTHER_AND_LONGER_ID:
                    further_and_longer.setEnabled(track.isPartiallyDownloaded());
                    break;
            }
        }
    }

    /**
     * Called when the user clicks on the remove videos button in the layout
     *
     * @param track the track to erase its videos
     */
    @SuppressWarnings("UnusedParameters")
    private void removeVideos(final Track track) {
        new AlertDialog.Builder(this).setTitle(R.string.delete_videos).setMessage(getString(R.string.youre_about_to_dlete_videos, Utils.getString(this, track.getTitleId()))).setNegativeButton(R.string.no, null).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Segment segment : track.getAllSegments()) {
                    if (segment.isDownloaded()) {
                        removeFile(segment.getFilename());
                    }
                }

                setVisibilityOfButtons();

                Toast.makeText(SettingsManageVideosActivity.this, R.string.videos_have_been_removed, Toast.LENGTH_LONG).show();
            }
        }).show();
    }

    public void removeDemoVideosClick(@SuppressWarnings("unused") View view) {
        for (Track track : indoorTracks) {
            if (track.getId().equals(BuildConfig.DEBUG ? Track.DEMO_DEVEL_ID : Track.DEMO_ID)) {
                removeVideos(track);
            }
        }
    }

    public void removeHarderAndStrongerVideosClick(@SuppressWarnings("unused") View view) {
        for (Track track : indoorTracks) {
            if (track.getId().equals(Track.HARDER_AND_STRONGER_ID)) {
                removeVideos(track);
            }
        }
    }

    public void removeHigherAndFasterVideosClick(@SuppressWarnings("unused") View view) {
        for (Track track : indoorTracks) {
            if (track.getId().equals(Track.HIGHER_AND_FASTER_ID)) {
                removeVideos(track);
            }
        }
    }

    public void removeFurtherAndLongerVideosClick(@SuppressWarnings("unused") View view) {
        for (Track track : indoorTracks) {
            if (track.getId().equals(Track.FURTHER_AND_LONGER_ID)) {
                removeVideos(track);
            }
        }
    }

    private void removeFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            Log.i(TAG, "Removing file " + file.getAbsolutePath());
            if (!file.delete()) {
                Log.e(TAG, String.format("Cannot remove file: %s", file.getAbsolutePath()));
            }
        }
    }
}
