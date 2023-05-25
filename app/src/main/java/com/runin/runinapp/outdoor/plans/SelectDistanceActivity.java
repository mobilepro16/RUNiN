package com.runin.runinapp.outdoor.plans;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runin.runinapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDistanceActivity extends AppCompatActivity {
    @BindView(R.id.button_next)
    Button next;

    @BindView(R.id.screen_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_distance);
        ButterKnife.bind(this);

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/BebasNeue.otf");
        title.setTypeface(font);
        next.setTypeface(font);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectDistanceActivity.this, SelectPaceActivity.class);
                startActivity(intent);
                SelectDistanceActivity.this.finish();
            }
        });
    }
}
