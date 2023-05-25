package com.runin.runinapp.musicPlayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectPlayerActivity extends AppCompatActivity {

    @BindView(R.id.title_select_music)
    TextView titleSelect;

    @BindView(R.id.confirm_music)
    Button buttonConfirm;

    @BindView(R.id.select_music_player)
    RadioGroup selectMusicGroup;

    @BindView(R.id.radio_music)
    RadioButton radioMusic;

    @BindView(R.id.radio_spotify)
    RadioButton radioSpotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);
        ButterKnife.bind(this);

        buttonConfirm.setTypeface(Utils.getFontBebasNeue(this));
        titleSelect.setTypeface(Utils.getFontBebasNeue(this));

        String preferencesFile = App.sharedPreferencesFile;

        SharedPreferences sharedPref = SelectPlayerActivity.this.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);

        String selectedMusicPlayer = App.sharedPreferencesPropertyMusicPlayer;
        String selectedMusicPlayerSaved = sharedPref.getString(selectedMusicPlayer, "");

        switch (selectedMusicPlayerSaved) {
            case "native":
                radioMusic.setChecked(true);
                radioSpotify.setChecked(false);
                break;
            case "spotify":
                radioMusic.setChecked(false);
                radioSpotify.setChecked(true);
                break;
            default:
                radioMusic.setChecked(false);
                radioSpotify.setChecked(false);
                break;
        }
    }

    public void confirmPlayer(@SuppressWarnings("unused") View view) {

        String selectedMusicPlayer = App.sharedPreferencesPropertyMusicPlayer;
        String preferenceFile = App.sharedPreferencesFile;
        SharedPreferences sharedPreferences = SelectPlayerActivity.this.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (radioMusic.isChecked()) {
            editor.putString(selectedMusicPlayer, "native");
        }
        else if (radioSpotify.isChecked()) {
            editor.putString(selectedMusicPlayer, "spotify");
        }
        else {
            editor.putString(selectedMusicPlayer, "none");
        }
        editor.apply();
        finish();
    }

    public void onClickRadioGroup(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_music:
                if (checked) {
                    radioSpotify.setChecked(false);
                    @SuppressWarnings("deprecation") Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                    startActivity(intent);
                }
                break;
            case R.id.radio_spotify:
                if (checked) {
                    radioMusic.setChecked(false);
                }
                break;
        }
    }
}
