package com.runin.runinapp.indoor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runin.runinapp.R;
import com.runin.runinapp.utils.Utils;

public class IndoorNotFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_not_finished);

        Button buttonEnd = findViewById(R.id.buttonEnd);
        TextView screenTitle = findViewById(R.id.screen_title);

        buttonEnd.setTypeface(Utils.getFontBebasNeue(this));
        screenTitle.setTypeface(Utils.getFontBebasNeue(this));
    }

    @SuppressWarnings("UnusedParameters")
    public void finishButtonClick(View view) {
        this.finish();
    }
}