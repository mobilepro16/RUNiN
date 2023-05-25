package com.runin.runinapp.utils.detector;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.runin.runinapp.R;
import com.runin.runinapp.utils.detector.data.GlobalData;
import com.runin.runinapp.utils.detector.data.Preferences;
import com.runin.runinapp.utils.detector.detection.AggregateLumaMotionDetection;
import com.runin.runinapp.utils.detector.detection.IMotionDetection;
import com.runin.runinapp.utils.detector.detection.LumaMotionDetection;
import com.runin.runinapp.utils.detector.detection.RgbMotionDetection;
import com.runin.runinapp.utils.detector.image.ImageProcessing;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * This class extends Activity to handle a picture preview, process the frame
 * for motion, and then save the file to the SD card.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class MotionDetectionActivity extends SensorsActivity {

    private static final String TAG = MotionDetectionActivity.class.getSimpleName();

    private static SurfaceHolder previewHolder = null;
    @SuppressWarnings("deprecation")
    private static Camera camera = null;
    private static boolean inPreview = false;
    private static IMotionDetection detector = null;

    private static volatile AtomicBoolean processing = new AtomicBoolean(false);
    @SuppressWarnings("deprecation")
    private PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) return;
            @SuppressWarnings("deprecation") Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) return;

            if (!GlobalData.isPhoneInMotion()) {
                DetectionThread thread = new DetectionThread(data, size.width, size.height);
                thread.start();
            }
        }
    };
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviwDemo-srfcCallback", "Exception setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            @SuppressWarnings("deprecation") Camera.Parameters parameters = camera.getParameters();
            @SuppressWarnings("deprecation") Camera.Size size = getBestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.w(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
            inPreview = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    @SuppressWarnings("deprecation")
    private static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) result = size;
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motion_layout);

        SurfaceView preview = findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);

        if (Preferences.USE_RGB) {
            detector = new RgbMotionDetection();
        }
        else if (Preferences.USE_LUMA) {
            detector = new LumaMotionDetection();
        }
        else {
            // Using State based (aggregate map)
            detector = new AggregateLumaMotionDetection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        camera.setPreviewCallback(null);
        if (inPreview) camera.stopPreview();
        inPreview = false;
        camera.release();
        camera = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        camera = openFrontFacingCameraGingerbread();
    }

    @SuppressWarnings("deprecation")
    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    private static final class DetectionThread extends Thread {

        private byte[] data;
        private int width;
        private int height;

        DetectionThread(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            if (!processing.compareAndSet(false, true)) return;

            try {
                // Current frame (with changes)
                int[] img;
                if (Preferences.USE_RGB) {
                    img = ImageProcessing.decodeYUV420SPtoRGB(data, width, height);
                }
                else {
                    img = ImageProcessing.decodeYUV420SPtoLuma(data, width, height);
                }

                // Current frame (without changes)
                if (img != null) {
                    if (detector.detect(img, width, height)) {
                        Log.w(TAG, "Hay movimiento");
                    }
                    else {
                        Log.w(TAG, "No hay movimiento");
                    }
                }
                else {
                    Log.e(TAG, "img es nulo");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processing.set(false);
            }

            processing.set(false);
        }
    }
}