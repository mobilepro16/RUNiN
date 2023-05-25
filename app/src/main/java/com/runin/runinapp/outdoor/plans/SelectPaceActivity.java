package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runin.runinapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectPaceActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.button_next)
    Button next;

    @BindView(R.id.screen_title)
    TextView title;

    @BindView(R.id.pace_lv)
    RelativeLayout pace;

    @BindView(R.id.pace_custom)
    RelativeLayout custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pace);
        ButterKnife.bind(this);

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/BebasNeue.otf");
        title.setTypeface(font);
        next.setTypeface(font);

        pace.setOnClickListener(this);
        custom.setOnClickListener(this);
        next.setOnClickListener(this);
    }


    private void normalView() {
        ((ImageView) this.findViewById(R.id.imageView19)).setImageResource(R.drawable.unchecked);
        ((ImageView) this.findViewById(R.id.imageView10)).setImageResource(R.drawable.unchecked);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pace_lv:
                normalView();
                ((ImageView) view.findViewById(R.id.imageView10)).setImageResource(R.drawable.check);

                break;
            case R.id.pace_custom:
                normalView();
                ((ImageView) view.findViewById(R.id.imageView19)).setImageResource(R.drawable.check);

                break;
            case R.id.button_next:
                normalView();
                Intent intent = new Intent(SelectPaceActivity.this, SelectPlanSummary.class);
                startActivity(intent);
                Log.e("FINAL", "PACE");
                SelectPaceActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
