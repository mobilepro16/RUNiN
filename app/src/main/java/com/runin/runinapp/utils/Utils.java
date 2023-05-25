package com.runin.runinapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.exifinterface.media.ExifInterface;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * General utils
 * Created by Cesar on 30/08/2016.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static Typeface getFontChunk(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/ChunkfiveEx.ttf");
    }

    public static Typeface getFontBebasNeue(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/BebasNeue.otf");
    }

    private static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int sourceWidth = bm.getWidth();
        int sourceHeight = bm.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, bm.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(bm, null, targetRect, null);

        return dest;
    }

    /**
     * Resizes an image and crops it into a circular shape
     *
     * @param bitmap the original image
     * @param width  width of the target image
     * @param height height of the target image
     * @return the new image
     */
    public static Bitmap getResizedAndCircleBitmap(Bitmap bitmap, int width, int height) {
        Bitmap resized = Utils.getResizedBitmap(bitmap, height, width);

        return Utils.getRoundedRectBitmap(resized, width);
    }

    /**
     * Remove ugly deprecated messages for Html without breaking compatibility.
     *
     * @param source the source
     * @return the spanned
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            return Html.fromHtml(source);
        }
    }

    /**
     * Removes version compatibility issues of getColor
     *
     * @param context     The context
     * @param resource_id The R.color.color_id
     * @return The corresponding color
     */
    public static int getColor(Context context, int resource_id) {
        int color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = ContextCompat.getColor(context, resource_id);
        }
        else {
            //noinspection deprecation
            color = context.getResources().getColor(resource_id);
        }

        return color;
    }

    /**
     * Removes version compatibility issues of getColor
     *
     * @param context     The context
     * @param resource_id The R.color.color_id
     * @return The corresponding color
     */
    public static Drawable getDrawable(Context context, int resource_id) {
        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable = ContextCompat.getDrawable(context, resource_id);
        }
        else {
            //noinspection deprecation
            drawable = context.getResources().getDrawable(resource_id);
        }

        return drawable;
    }

    public static Drawable getDrawable(Context context, String resourceIdName) {
        Drawable drawable;
        int id = context.getResources().getIdentifier(resourceIdName, "drawable", context.getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable = ContextCompat.getDrawable(context, id);
        }
        else {
            //noinspection deprecation
            drawable = context.getResources().getDrawable(id);
        }

        return drawable;
    }

    /**
     * Converts density points to pixels
     *
     * @param dp      density points
     * @param context application context
     * @return pixels
     */
    public static int dp2px(int dp, Context context) {
        float logicalDensity = context.getResources().getDisplayMetrics().density;
        return (int) (dp * logicalDensity + 0.5f);
    }

    /**
     * Converts scaled density points to pixels
     *
     * @param sp      scaled density points
     * @param context application context
     * @return pixels
     */
    public static float sp2px(float sp, Context context) {
        float logicalDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * logicalDensity;
    }

    /**
     * Determines if an image file is rotated and returns the number of degrees
     *
     * @param file The image file
     * @return the number of degrees rotated
     * @throws IOException if the file is not valid
     */
    public static int getImageRotationAngle(File file) throws IOException {
        String path = file.getPath();
        Log.i(TAG, "Image to rotate: " + path);

        ExifInterface exif = new ExifInterface(path);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }

        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }

        return 0;
    }

    /**
     * Checks if the WIFI is on and connected
     * <p>
     * see: https://stackoverflow.com/questions/3841317/how-do-i-see-if-wi-fi-is-connected-on-android
     */
    public static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr != null && wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            return wifiInfo.getNetworkId() != -1;
        }

        return false; // Wi-Fi adapter is OFF
    }

    public static String getString(Context context, String resourceIdName) {
        int id = context.getResources().getIdentifier(resourceIdName, "string", context.getPackageName());
        return context.getString(id);
    }

    /**
     * Helps determine if we're using an emulator (mainly for development)
     *
     * @return whether we're running the app on an emulator
     */
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
