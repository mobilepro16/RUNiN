package com.runin.runinapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.R;

/**
 * Pager adapter of the tutorial
 * Created by Cesar on 09/08/2016.
 */
public class TutorialViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final int[] mResources;
    private final int[] titleResources;
    private final int[] descriptionResources;

    /**
     * Constructor to get the resources and string of every screen
     */
    public TutorialViewPagerAdapter(Context mContext, int[] mResources, int[] titleResources, int[] descriptionResources) {
        this.mContext = mContext;
        this.mResources = mResources;
        this.titleResources = titleResources;
        this.descriptionResources = descriptionResources;
    }


    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * In this method set the string and image depending of the position
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item_tutorial, container, false);

        ImageView imageView = itemView.findViewById(R.id.img_pager_item);
        imageView.setImageResource(mResources[position]);

        TextView title = itemView.findViewById(R.id.img_pager_item_title);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/BebasNeue.otf");
        title.setTypeface(font);
        title.setText(titleResources[position]);

        TextView description = itemView.findViewById(R.id.img_pager_item_description);
        description.setText(descriptionResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}