package com.runin.runinapp.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.runin.runinapp.data.Badge;

import java.util.ArrayList;

/**
 * Adapter for Badges screen
 *
 * Created by Omar Sevilla  on 29/12/2016.
 * Updated by Samuel Kobelkowsky on 09/11/2017
 */
public class BadgeGridItemAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Badge> badges;

    /**
     * Class constructor
     * @param context The activity context
     * @param badges An array of the badges to show
     */
    public BadgeGridItemAdapter(Context context, ArrayList<Badge> badges) {
        this.badges = badges;
        this.context = context;
    }

    /**
     * The number of badges
     * @return The number of badges
     */
    @Override
    public int getCount() {
        return badges.size();
    }

    /**
     * The badge at a given position
     * @param position The position of the badge
     * @return The badge at the given position
     */
    @Override
    public Object getItem(int position) {
        return badges.get(position);
    }

    /**
     * Return the item ID
     * @param position The position of the badge
     * @return the item ID
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns an ImageView for an item referenced by the Adapter by its position
     * @param position The position of the Badge
     * @param view A previously created view for the badge
     * @param parent The parent view group
     * @return The ImageView for the badge
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ImageView imageView;

        if (view == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setAdjustViewBounds(true);
        }
        else {
            imageView = (ImageView) view;
        }

        Badge badge = badges.get(position);
        imageView.setImageResource(badge.isCompleted() ? badge.getIcon() : badge.getGrayIcon());
        return imageView;
    }
}