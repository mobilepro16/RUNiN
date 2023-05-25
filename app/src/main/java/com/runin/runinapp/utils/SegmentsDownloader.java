package com.runin.runinapp.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;

import java.util.ArrayList;

/**
 * Downloads a list of files.
 * Created by Samuel Kobelkowsky on 8/23/17.
 */

public class SegmentsDownloader {

    private final String TAG = SegmentsDownloader.class.getSimpleName();

    private final Context context;
    private DownloadTask downloadTask;
    private int current_element_index;
    private ArrayList<Segment> segments;
    private Listener internalListener;
    private Listener externalListener;
    private int maxProgress = 0;

    public SegmentsDownloader(final IndoorAppState indoorAppState, Context context, final ArrayList<Segment> remoteSegments, final Listener externalListener) {

        if (remoteSegments == null) throw new NullPointerException("Files list is null");

        if (remoteSegments.size() == 0) throw new IllegalStateException("Files list is empty");

        this.context = context;

        this.current_element_index = 0;

        this.segments = remoteSegments;

        this.externalListener = externalListener;

        this.maxProgress = remoteSegments.size() * 100;

        internalListener = new Listener() {
            private PowerManager.WakeLock mWakeLock;

            @Override
            public void onProgressUpdate(int progress) {
                double total_progress = 100.0 * current_element_index + progress;

                externalListener.onProgressUpdate((int) (100.0 * total_progress / maxProgress));
            }

            @Override
            public void onSuccess() {
                indoorAppState.save();

                current_element_index += 1;

                if (current_element_index == remoteSegments.size()) {
                    Log.i(TAG, "Success!!!!");
                    externalListener.onSuccess();
                    return;
                }

                String filename = segments.get(current_element_index).getFilename();

                String uri = segments.get(current_element_index).getVideoRemoteUri();

                downloadTask = new DownloadTask(internalListener);
                downloadTask.execute(uri, filename);
            }

            @Override
            public void onFailure(String error) {
                externalListener.onFailure(error);
            }

            @Override
            public void onCancel() {
                externalListener.onCancel();
            }

            @Override
            public void onPreExecute() {
                PowerManager pm = (PowerManager) SegmentsDownloader.this.context.getSystemService(Context.POWER_SERVICE);
                if (pm != null) {
                    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
                    // Only stay awake for 5 minutes
                    mWakeLock.acquire(300000);
                }

                externalListener.onPreExecute();
            }

            @Override
            public void onPostExecute() {
                if (mWakeLock != null) {
                    mWakeLock.release();
                }

                externalListener.onPostExecute();
            }
        };
    }

    public void download() {

        if (segments == null) throw new NullPointerException("Need a list of segments");

        // Nothing to download
        if (segments.size() == 0) externalListener.onSuccess();

        String filename = segments.get(0).getFilename();

        String uri = segments.get(0).getVideoRemoteUri();

        downloadTask = new DownloadTask(internalListener);
        downloadTask.execute(uri, filename);
    }

    public void cancel() {
        downloadTask.cancel(true);

        externalListener.onCancel();
    }

    public interface Listener {
        void onProgressUpdate(int progress);

        void onSuccess();

        void onFailure(String error);

        void onCancel();

        void onPreExecute();

        void onPostExecute();
    }
}