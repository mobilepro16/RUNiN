package com.runin.runinapp.outdoor.quickstart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.R;
import com.runin.runinapp.adapters.MusicSelectPlayer;
import com.runin.runinapp.outdoor.OutdoorRunningActivity;
import com.runin.runinapp.utils.RecyclerItemData;
import com.runin.runinapp.utils.RecyclerMusicClickListener;
import com.runin.runinapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 */
public class QuickStartFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = QuickStartFragment.class.getSimpleName();
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    @BindView(R.id.quick_start_btn)
    Button buttonQuickStart;

    @BindView(R.id.title_quick_start)
    TextView titleQuickStart;

    @BindView(R.id.subtitle_quick_start)
    TextView subtitleQuickStart;

    @BindView(R.id.title)
    LinearLayout title_content;

    @BindView(R.id.imageView5)
    ImageView linea_diagonal;

    @BindView(R.id.quick_start_image)
    ImageView quick_start_image;

    private Context context;
    private Activity activity;
    private AlertDialog alert = null;

    public QuickStartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_start, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() == null) return view;

        this.context = getActivity();
        this.activity = getActivity();

        buttonQuickStart.setOnClickListener(this);
        titleQuickStart.setTypeface(Utils.getFontBebasNeue(context));
        subtitleQuickStart.setTypeface(Utils.getFontBebasNeue(context));
        buttonQuickStart.setTypeface(Utils.getFontBebasNeue(context));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_player_options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerItemData itemData[] = {
                new RecyclerItemData(getString(R.string.musicSelect)),
        };


        MusicSelectPlayer musicSelectPlayerAdapter = new MusicSelectPlayer(itemData);
        recyclerView.setAdapter(musicSelectPlayerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerMusicClickListener(context, new RecyclerMusicClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        try {
                            @SuppressWarnings("deprecation") Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(QuickStartFragment.this.context, R.string.no_music_players_installed, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(QuickStartFragment.this.context, R.string.cannot_start_music_player, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Cannot start music app", ex);
                        }
                        break;
                }
            }
        }));

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.quick_start_btn) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "GPS Active");
                // if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    finishIntent();
                }
                else {
                    // Show rationale and request permission.
                    // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
                    // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                    }
                }
            }
            else {
                Log.e(TAG, "GPS Inactive");
                AlertNoGps();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onCreate(null);
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.gps_system_inactive_want_to_activate)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(context, R.string.it_is_necessary_to_activate_gps, Toast.LENGTH_LONG).show();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroy() {
        if (alert != null) {
            alert.dismiss();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    finishIntent();
                }
            }
            else {
                // Permission was denied. Display an error message.
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getActivity(), R.string.it_is_necessary_to_activate_location, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void finishIntent() {
        Intent intent = new Intent(getActivity(), OutdoorRunningActivity.class);
        startActivity(intent);
    }
}
