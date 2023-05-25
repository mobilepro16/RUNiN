/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.runin.runinapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.data.User;
import com.runin.runinapp.indoor.PlayerActivity;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * A view containing controls for a MediaPlayer. Typically contains the
 * buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress
 * slider. It takes care of synchronizing the controls with the state
 * of the MediaPlayer.
 * <p>
 * The way to use this class is to instantiate it programmatically.
 * The MediaController will create a default set of controls
 * and put them in a window floating above your application. Specifically,
 * the controls will float above the view specified with setAnchorView().
 * The window will disappear if left idle for three seconds and reappear
 * when the user touches the anchor view.
 * <p>
 * Functions like show() and hide() have no effect when MediaController is created in an xml layout.
 * <p>
 * MediaController will hide and show the buttons according to these rules:
 * <ul>
 * <li> The "previous" and "next" buttons are hidden until setPrevNextListeners()
 * has been called
 * <li> The "previous" and "next" buttons are visible but disabled if
 * setPrevNextListeners() was called with null listeners
 * <li> The "rewind" and "fast forward" buttons are shown unless requested
 * otherwise by using the MediaController(Context, boolean) constructor
 * with the boolean set to false
 * </ul>
 */
public class VideoControllerView extends FrameLayout {

    private static final String TAG = VideoControllerView.class.getSimpleName();

    private static final int SHOW_PROGRESS = 1;
    private final Context mContext;
    private final Handler mHandler = new MessageHandler(this);
    // The progressbar has this padding on the sides (in dp) so the icon doesn't clip.
    private final int progressbarPadding;
    private Track selectedTrack;
    private int currentSegment = 0;
    private MediaPlayerControl mediaPlayerControl;
    private ViewGroup mAnchor;
    private View mRoot;
    private TextView currentPercentageTextView;
    private boolean mShowing;
    private ImageButton mPauseButton;
    private Button mProgramButton;
    private ImageView ray;
    private LinearLayout indicatorsContainer;
    private IndoorAppState indoorAppState;
    private User userProfile;
    private ArrayList<Segment> segments;
    private ProgressBar progressBar;
    private TextView calories;
    private TextView time;
    private TextView distance;

    // When is the next audio message due?
    private double nextAudioMessageDistance = 1.0;

    public VideoControllerView(Context context) {
        super(context);

        mContext = context;

        // 16 dp as defined in the layout
        progressbarPadding = Utils.dp2px(16, context);
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        progressbarPadding = Utils.dp2px(16, context);
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
        progressbarPadding = Utils.dp2px(16, context);
    }

    private static void toggleVisibility(View v) {
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
        }
        else if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        if (mRoot != null) {
            initControllerView(mRoot);
        }
    }

    public void setMediaPlayerControl(MediaPlayerControl mediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl;
        updatePausePlayIcon();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     */
    private View makeControllerView() {
        mRoot = View.inflate(mContext, R.layout.media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View v) {
        mPauseButton = v.findViewById(R.id.pause);
        mProgramButton = v.findViewById(R.id.program);
        ray = v.findViewById(R.id.rayo);

        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mediaPlayerControl.userPause();
                }
            });
        }

        if (mProgramButton != null) {
            mProgramButton.requestFocus();
            mProgramButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayerControl.programInfo();
                }
            });
        }

        show();

        indicatorsContainer = v.findViewById(R.id.badw);

        progressBar = v.findViewById(R.id.mediacontroller_progress);

        currentPercentageTextView = v.findViewById(R.id.time_current);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     */
    public void show() {
        setVisibility(VISIBLE);

        selectedTrack = indoorAppState.getSelectedTrack();

        segments = selectedTrack.getSegments();

        Segment playingSegment = segments.get(currentSegment);

        if (!mShowing && mAnchor != null) {
            setProgress();

            mPauseButton.requestFocus();

            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        updatePausePlayIcon();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        // Waits until the SeekBar can be measured on the screen, so we can put the overlaying little icons.
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateSegmentIcons();
            }
        });

        // Update the info on screen
        TextView speed = mRoot.findViewById(R.id.speed_textview);
        TextView slope = mRoot.findViewById(R.id.slope_textview);

        calories = mRoot.findViewById(R.id.calories_textview);
        time = mRoot.findViewById(R.id.time_textview);
        distance = mRoot.findViewById(R.id.distance_textview);

        DecimalFormat df = new DecimalFormat("##.#");
        speed.setText(String.format(Locale.getDefault(), "%s km/h", df.format(playingSegment.getSpeed())));
        slope.setText(String.format(Locale.getDefault(), "%s °", df.format(playingSegment.getSlope())));
    }

    /**
     * Puts small icons indicating the type of segment that the user will run
     */
    private void updateSegmentIcons() {

        // This is the container where all the little icons go
        RelativeLayout container = mRoot.findViewById(R.id.segment_markers);
        if (container == null) {
            return;
        }

        // We don't know the width of the container until it is rendered on the screen
        double containerWidth = container.getWidth();
        if (containerWidth == 0) {
            return;
        }

        double containerHeight = container.getHeight();
        if (containerHeight == 0) {
            return;
        }

        // Already drawn
        if (container.getChildCount() > 0) {
            return;
        }

        // The duration of the track until a given segment starts
        double relative_duration = 0.0;

        for (Segment segment : segments) {

            // The proportion of the duration of the track until the start of this segment
            double factor = relative_duration / selectedTrack.getDuration();

            // We place the little icon at the start of the segment, but taking into consideration the padding of the progress bar
            double x = (containerWidth - 2 * progressbarPadding) * factor + progressbarPadding;


            // The icon depends on the type of segment
            ImageView runner = new ImageView(mContext);
            runner.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Drawable runner_drawable = Utils.getDrawable(mContext, segment.getIcon());
            runner.setImageDrawable(runner_drawable);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) containerHeight, (int) containerHeight);

            // We place the center of the icon
            params.leftMargin = (int) (x - containerHeight / 2);
            params.rightMargin = 0;
            params.topMargin = 0;

            container.addView(runner, params);

            // Next segment begins where this end
            relative_duration += segment.getDuration();

            Log.i(TAG, "Placing runner icon with factor: " + String.valueOf(factor) + ", x: " + String.valueOf(x));
        }
    }

    //int temporal_debug = 0;
    private void setProgress() {

        mediaPlayerControl.updateIndicators();
        long totalPastTime = mediaPlayerControl.getTotalPastTime();
        double totalPastDistance = mediaPlayerControl.getTotalPastDistance();
        int position = mediaPlayerControl.getPosition();

        if (time != null) {
            time.setText(DateUtils.millisToMinSec(totalPastTime));
        }

        if (calories != null) {
            calories.setText(String.format(Locale.getDefault(), "%.0f %s", selectedTrack.getCalories(userProfile.getWeight()) * totalPastTime / selectedTrack.getDuration(), getContext().getString(R.string.calories_abbr)));
        }

        if (distance != null) {
            distance.setText(String.format(Locale.getDefault(), "%.02f %s", totalPastDistance, getContext().getString(R.string.km_abrv)));
        }

        // Apparently this method starts even before these elements exist.
        if (progressBar != null) {
            progressBar.setProgress(position);
        }

        if (currentPercentageTextView != null) {
            currentPercentageTextView.setText(String.format(Locale.getDefault(), "%d%%", position));
        }

        if (totalPastDistance > nextAudioMessageDistance) {
            PlayerActivity activity = (PlayerActivity) mContext;
            Random r = new Random();
            int index = r.nextInt(3) + 1;
            int id;

            switch (index) {
                case 1:
                    id = R.string.one_more_km_1;
                    break;
                case 2:
                    id = R.string.one_more_km_2;
                    break;
                case 3:
                default:
                    id = R.string.one_more_km_3;
                    break;
            }
            ((App) activity.getApplication()).speakOut(mContext.getString(id));
            nextAudioMessageDistance += 1.0;
        }

        updatePausePlayIcon();
    }

    /**
     * Toggles the visibility of the indicators of the screen
     *
     * @param event the touch event
     * @return true if we make use of the event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show();

        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                toggleVisibility(indicatorsContainer);
                performClick();
                return true;
            default:
                return super.onTouchEvent(event);
        }

    }

    /**
     * Android Studio requires to override performClick when overriding onTouchEvent
     *
     * @return The parent performClick. Nothing is done to it.
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void updatePausePlayIcon() {
        if (mRoot == null || mPauseButton == null || mediaPlayerControl == null) {
            return;
        }

        if (mediaPlayerControl.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.pause_white);
        }
        else {
            mPauseButton.setImageResource(R.drawable.play_white);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }

        if (progressBar != null) {
            progressBar.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public void setIndoorAppState(@NonNull IndoorAppState indoorAppState) {
        this.indoorAppState = indoorAppState;
    }

    public void setUserProfile(@NonNull User userProfile) {
        this.userProfile = userProfile;
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;

        Segment.Kind kind = segments.get(currentSegment).getKind();
        if (kind == Segment.Kind.RUN || kind == Segment.Kind.JOG || kind == Segment.Kind.SPRINT) {
            mProgramButton.setVisibility(GONE);
            ray.setVisibility(VISIBLE);
        }
        else {
            mProgramButton.setVisibility(VISIBLE);
            ray.setVisibility(GONE);
        }
    }

    public interface MediaPlayerControl {
        void userPause();

        boolean isPlaying();

        void programInfo();

        long getTotalPastTime();

        double getTotalPastDistance();

        int getPosition();

        void updateIndicators();
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mediaPlayerControl == null) {
                return;
            }

            switch (msg.what) {
                case SHOW_PROGRESS:
                    view.setProgress();
                    if (view.mShowing && view.mediaPlayerControl.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 150);
                    }
                    break;
            }
        }
    }
}