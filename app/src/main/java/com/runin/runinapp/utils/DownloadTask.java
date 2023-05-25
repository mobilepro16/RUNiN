package com.runin.runinapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Downloader of large files
 * Created by Samuel Kobelkowsky on 11/29/17.
 * https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
 */
class DownloadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = DownloadTask.class.getSimpleName();

    private final SegmentsDownloader.Listener listener;

    DownloadTask(SegmentsDownloader.Listener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... sUrl) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(sUrl[0]);
            String filename = sUrl[1];

            Log.i(TAG, "Downloading " + url + " into " + filename);

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            File file = new File(filename);
            if (file.exists()) {
                Log.w(TAG, "File " + filename + " exists. Overwriting.");
            }

            output = new FileOutputStream(filename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                {
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (InterruptedIOException e) {
            Log.e(TAG, "Download interrupted", e);
            return "Download interrupted";
        } catch (Exception e) {
            Log.e(TAG, "Error downloading", e);
            return e.toString();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        listener.onProgressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onPostExecute();

        if (s != null) {
            listener.onFailure(s);
        }
        else {
            listener.onSuccess();
        }
    }
}
