package com.runin.runinapp.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.utils.RecyclerItemData;

/**
 * Documentation: Not used yet. It will be documented when used.
 *
 * Created by Usuario on 29/09/2016.
 * Updated by Samuel Kobelkowsky on 09/11/2017
 */
public class MusicSelectPlayer extends RecyclerView.Adapter<MusicSelectPlayer.ViewHolder> {

    private final RecyclerItemData[] itemData;

    public MusicSelectPlayer(RecyclerItemData[] itemData) {
        this.itemData = itemData;
    }

    @Override
    public MusicSelectPlayer.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = View.inflate(parent.getContext(), R.layout.recycler_player, null);
        return new ViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(MusicSelectPlayer.ViewHolder holder, int position) {
        holder.textView.setText(itemData[position].getTitle());
        holder.imageViewIcon.setImageResource(itemData[position].getImageUrl());
        holder.imageViewArrow.setImageResource(R.mipmap.right_icon);
        if (itemData[position].getShowArrow()) {
            holder.imageViewArrow.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageViewArrow.setVisibility(View.GONE);
            holder.imageViewIcon.setAlpha((float) 0.4);
            holder.textView.setAlpha((float) 0.4);
        }
    }

    @Override
    public int getItemCount() {
        return itemData.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final ImageView imageViewIcon;
        final ImageView imageViewArrow;

        ViewHolder(View itemView) {
            super(itemView);
            imageViewArrow = itemView.findViewById(R.id.arrow_music);
            imageViewIcon = itemView.findViewById(R.id.icon_music);
            textView = itemView.findViewById(R.id.text_music);
        }
    }
}
