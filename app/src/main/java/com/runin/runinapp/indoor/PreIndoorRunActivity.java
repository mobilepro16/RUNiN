package com.runin.runinapp.indoor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.utils.CameraPreview;
import com.runin.runinapp.utils.Utils;

import java.text.DecimalFormat;
import java.util.Locale;

import static android.hardware.Camera.getCameraInfo;

@SuppressWarnings("deprecation")
public class PreIndoorRunActivity extends Activity {
    private static final String TAG = PreIndoorRunActivity.class.getSimpleName();

    // For motion detection
    private static Camera camera = null;

    private final Handler handler = new Handler();

    private int cameraIndex;

    private CameraPreview cameraPreview;

    private Button btnStart;
    private Context context;



    /**
     * Used to activate the button after a few seconds.
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            btnStart.setEnabled(true);
            btnStart.setBackgroundColor(Utils.getColor(PreIndoorRunActivity.this, R.color.orange));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(getApplicationContext(), R.string.adjust_treadmill_with_speed_and_slope, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_pre_indoor_run);

        IndoorAppState indoorAppState = ((App) this.getApplication()).getIndoorAppState();

        Track selectedTrack = indoorAppState.getSelectedTrack();

        TextView txtSlope = findViewById(R.id.txtSlope);
        TextView txtSpeed = findViewById(R.id.txtSpeed);

        btnStart = findViewById(R.id.btnStart);

        cameraPreview = findViewById(R.id.surfaceMotionDetection);
        context = cameraPreview.getContext();

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();

        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;







        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.w(TAG, "Found a front camera");
//                result = (cameraInfo.orientation + degrees) % 360;
//                result = (360 - result) % 360;
//                camera.setDisplayOrientation(result);

                cameraIndex = camIdx;
            }

        }

        DecimalFormat df = new DecimalFormat("##.#");
        txtSlope.setText(String.format(Locale.getDefault(), "%s", df.format(selectedTrack.getSegments().get(0).getSlope())));
        txtSpeed.setText(String.format(Locale.getDefault(), "%s", df.format(selectedTrack.getSegments().get(0).getSpeed())));

        btnStart.setTypeface(Utils.getFontBebasNeue(this), Typeface.ITALIC);
    }

    public void gotoRun(@SuppressWarnings("unused") View v) {
        Intent intentPlayer = new Intent(PreIndoorRunActivity.this, PlayerActivity.class);
        intentPlayer.putExtra(App.extrasCurrentSegment, 0);
        startActivity(intentPlayer);
        PreIndoorRunActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.w(TAG, "onResume");

        handler.postDelayed(runnable, 5000);
        btnStart.setEnabled(false);
        btnStart.setBackgroundColor(Utils.getColor(PreIndoorRunActivity.this, R.color.gray));

        try {
            camera = Camera.open(cameraIndex);
            camera.setDisplayOrientation(270);

            camera.startPreview();
            cameraPreview.setCamera(camera, cameraIndex);
        } catch (Exception ex) {
            Log.e(TAG, "Error opening camera, maybe you didn't provide permission to do it?", ex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.w(TAG, "onPause");

        handler.removeCallbacks(runnable);

        try {
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception ex) {
            Log.e(TAG, "Error opening camera, maybe you didn't provide permission to do it?", ex);
        }
    }
}
