package com.runin.runinapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorLevel;

import java.util.List;

/**
 * Adapter for Indoor Levels.
 * <p>
 * Created by Omar Sevilla  on 26/12/2016.
 * Updated by Samuel Kobelkowsky on 09/11/2017
 */
public class LevelRectAdapter extends BaseAdapter {
    @NonNull
    private final Context context;

    @NonNull
    private final List<IndoorLevel> levels;

    /**
     * Class constructor
     *
     * @param context The activity context
     * @param levels  An array of the Indoor Levels to show
     */
    public LevelRectAdapter(@NonNull Context context, @NonNull List<IndoorLevel> levels) {
        this.levels = levels;
        this.context = context;
    }

    /**
     * The number of levels
     *
     * @return The number of levels
     */
    @Override
    public int getCount() {
        return levels.size();
    }

    /**
     * The badge at a given position
     *
     * @param i The position of the badge
     * @return The level at the given position
     */
    @Override
    public Object getItem(int i) {
        return levels.get(i);
    }

    /**
     * Return the item ID
     *
     * @param i The position of the badge
     * @return the item ID
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Returns a View for an item referenced by the Adapter by its position
     *
     * @param position  The position of the level
     * @param view      A previously created view for the level
     * @param viewGroup The parent view group
     * @return The  View for the level
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder vh;

        if (view == null) {
            vh = new ViewHolder();
            view = View.inflate(context, R.layout.level_rec, null);

            vh.b1 = view.findViewById(R.id.txt_title);
            vh.txt1ABook = view.findViewById(R.id.txt_desc);
            vh.background = view.findViewById(R.id.background);
            view.setTag(vh);
        }
        else {
            vh = (ViewHolder) view.getTag();
        }
        vh.b1.setTag(levels.get(position));
        vh.b1.setText(levels.get(position).getTitleId());
        vh.txt1ABook.setText(levels.get(position).getDescriptionId());
        vh.background.setBackground(context.getDrawable(levels.get(position).getBackgroundResource()));

        return view;
    }

    /**
     * In these objects the reference of the view is stored to be recycled.
     */
    private class ViewHolder {
        TextView b1;
        TextView txt1ABook;
        LinearLayout background;
    }
}