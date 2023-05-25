package com.runin.runinapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.R;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.utils.Utils;

import java.util.ArrayList;

/**
 * Track View
 * Created by Omar Sevilla  on 26/12/2016.
 */

public class TrackRectAdapter extends BaseAdapter {

    private final ArrayList<Track> tracks;
    private final Context context;
    private ViewHolder vh;

    public TrackRectAdapter(Context context, ArrayList<Track> tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int i) {
        return tracks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.track_rec, null);

            vh.title = convertView.findViewById(R.id.txt_title);
            vh.finish_btn = convertView.findViewById(R.id.finish_btn);
            vh.description = convertView.findViewById(R.id.description);
            vh.background = convertView.findViewById(R.id.background);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        Track selectedTrack = tracks.get(position);

        // Because onClick only returns the view, we need to identify the selected item that produces it.
        vh.title.setTag(tracks.get(position));
        vh.finish_btn.setTag(tracks.get(position));

        vh.title.setText(Utils.getString(context, selectedTrack.getTitleId()));
        vh.description.setText(Utils.getString(context, selectedTrack.getSpecsId()));
        vh.background.setBackground(Utils.getDrawable(context, selectedTrack.getBackgroundResourceId()));

        stateDependentButton(selectedTrack);

        return convertView;
    }

    /**
     * Sets up the button depending if the track is available, purchased or not.
     */
    private void stateDependentButton(Track track) {
        if (track.getAllSegments().size() == 0) {
            //Log.i(TAG, "Track " + track.getId() + " has no segments");
            vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_gray));
            vh.finish_btn.setText(R.string.wait_dots);
        }
        else if (track.isEnabledForUser() || BuildConfig.ALL_TRACKS_AVAILABLE) {
            if (track.isPurchased()) {
                if (track.isDownloaded()) {
                    vh.finish_btn.setText(R.string.button_run);
                    vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_orange));
                }
                else if (track.isDownloading()) {
                    vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_gray));
                    vh.finish_btn.setText(String.format("%s%%", String.valueOf(track.getPercentDownloaded())));
                }
                else {
                    vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_orange));
                    vh.finish_btn.setText(R.string.download);
                }
            }
            else {
                vh.finish_btn.setText(R.string.addClosePositive);
                vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_orange));
            }
        }
        else {
            vh.finish_btn.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_gray));
            vh.finish_btn.setText(R.string.addClosePositive);
        }
    }

    private class ViewHolder {
        TextView title;
        TextView finish_btn;
        TextView description;
        LinearLayout background;
    }
}