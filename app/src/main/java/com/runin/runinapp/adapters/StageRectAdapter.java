package com.runin.runinapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.utils.Utils;

import java.util.List;

/**
 * Adapter for selecting a plan stage
 * Created by Citrus01 on 12/06/2017.
 */
public class StageRectAdapter extends BaseAdapter {

    private final List<Stage> stages;
    private final Context context;

    public StageRectAdapter(Context context, List<Stage> stages) {
        this.context = context;
        this.stages = stages;
    }

    @Override
    public int getCount() {
        return stages.size();
    }

    @Override
    public Object getItem(int i) {
        return stages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Stage stage = stages.get(position);
        ViewHolder vh;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.stage_rec, null);

            vh = new ViewHolder();

            vh.txtTitle = convertView.findViewById(R.id.txt_title);
            vh.txtDescription = convertView.findViewById(R.id.txt_desc);
            vh.imgDiagonal = convertView.findViewById(R.id.linearImag);
            vh.btnSelect = convertView.findViewById(R.id.finish_btn);

            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.txtTitle.setTag(stage.getTitle());
        vh.txtTitle.setText(stage.getTitle());
        vh.txtDescription.setText(stage.getDescription());

        if (stage.isPurchased()) {
            vh.btnSelect.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_orange));
            vh.btnSelect.setText(R.string.start_button_text);
        }
        else {
            vh.btnSelect.setBackground(Utils.getDrawable(context, R.drawable.roundcorner_gray));
            vh.btnSelect.setText(R.string.addClosePositive);
        }

        switch (position % 5) {
            case 0:
                vh.txtTitle.setBackgroundColor(Utils.getColor(context, R.color.principiante_text));
                vh.txtTitle.setTextColor(Utils.getColor(context, R.color.principiante));
                vh.imgDiagonal.setImageResource(R.mipmap.linea_diagonal_etapa1);
                break;
            case 1:
                vh.txtTitle.setBackgroundColor(Utils.getColor(context, R.color.stage2));
                vh.txtTitle.setTextColor(Utils.getColor(context, R.color.gray));
                vh.imgDiagonal.setImageResource(R.mipmap.linea_diagonal_etapa2);
                break;
            case 2:
                vh.txtTitle.setBackgroundColor(Utils.getColor(context, R.color.colorSpeedMax));
                vh.txtTitle.setTextColor(Utils.getColor(context, R.color.gray));
                vh.imgDiagonal.setImageResource(R.mipmap.linea_diagonal_etapa3);
                break;
            case 3:
                vh.txtTitle.setBackgroundColor(Utils.getColor(context, R.color.stage4));
                vh.txtTitle.setTextColor(Utils.getColor(context, R.color.gray));
                vh.imgDiagonal.setImageResource(R.mipmap.linea_diagonal_etapa4);
                break;
            default:
                vh.txtTitle.setBackgroundColor(Utils.getColor(context, R.color.stage4));
                vh.txtTitle.setTextColor(Utils.getColor(context, R.color.gray));
                vh.imgDiagonal.setImageResource(R.mipmap.linea_diagonal_etapa4);
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtDescription;
        TextView btnSelect;
        ImageView imgDiagonal;
    }
}
