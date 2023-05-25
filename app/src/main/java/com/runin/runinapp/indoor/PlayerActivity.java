package com.runin.runinapp.indoor;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Segment;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.VideoControllerView;
import com.runin.runinapp.utils.detector.SensorsActivity;
import com.runin.runinapp.utils.detector.data.GlobalData;
import com.runin.runinapp.utils.detector.data.Preferences;
import com.runin.runinapp.utils.detector.detection.AggregateLumaMotionDetection;
import com.runin.runinapp.utils.detector.detection.IMotionDetection;
import com.runin.runinapp.utils.detector.detection.LumaMotionDetection;
import com.runin.runinapp.utils.detector.detection.RgbMotionDetection;
import com.runin.runinapp.utils.detector.image.ImageProcessing;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

/**
 * indoor player
 * Created by Omar Sevilla  on 27/12/2016.
 */
public class PlayerActivity
        extends SensorsActivity
        implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, ReadySetGoDialogFragment.OnDismissListener,
        VideoControllerView.MediaPlayerControl, DialogAwakeWindow.OnStillListener, DialogFragmentNextVideo.OnDismissListener {

    // Identifier for Logs
    private static final String TAG = PlayerActivity.class.getSimpleName();

    // Used for activity life cycle
    private static final String CURRENT_SEGMENT_STATE_KEY = "CURRENT_SEGMENT_STATE_KEY";

    // Used for activity life cycle
    private static final String CURRENT_VIDEO_ADVANCE_STATE_KEY = "CURRENT_VIDEO_ADVANCE_STATE_KEY";

    private static final String TOTAL_PAST_TIME = "TOTAL_PAST_TIME";
    private static final String TOTAL_PAST_DISTANCE = "TOTAL_PAST_DISTANCE";

    // For motion detection
    private static final AtomicBoolean motionDetectionProcessing = new AtomicBoolean(false);

    // For motion detection
    @SuppressWarnings("deprecation")
    private static Camera camera = null;

    // For motion detection
    private static boolean inCameraPreview = false;

    // For motion detection
    private static IMotionDetection motionDetector = null;

    // Tells if the video has finished before another has started
    private boolean isVideoCompleted = false;

    // The surface containing the camera preview. Needs a few tricks to work when hidden.
    private SurfaceView surfaceViewMotionDetection;

    // For motion detection
    private ReadySetGoDialogFragment readySetGoDialogFragment;

    // For motion detection
    private DialogFragmentNextVideo dialogFragmentNextVideo;

    // For motion detection
    private Dialog stopFollowDialog;

    // Allows to control and monitor the surface where the video is playing. Accessed via the SurfaceHolder.Callback interface methods.
    private SurfaceHolder surfaceHolderTrackVideo;

    // For motion detection
    private SurfaceHolder surfaceHolderMotionDetection;

    // The track has different segments, each its own video. This is the current playing segment.
    private int currentSegment = 0;

    // The video player
    private MediaPlayer videoPlayer;

    // Contains the Application State.
    private IndoorAppState indoorAppState;

    // Contains the progress bar and video controls
    private VideoControllerView videoControllerView;

    // Used to indicate back pressed dialog that user doesn't want to continue
    private boolean isCancelling;

    // Stores the position of the video. Used when the Activity stops and restarts to seek the same point in time
    private int currentVideoAdvance;

    // Stores the last time (in milliseconds) that the motion detection fired an alert
    private long lastMotionAlert;

    // The surface containing the video playing
    private SurfaceView surfaceViewTrackVideo;

    // The segment information fragment.
    private IndoorProgramInfoFragment indoorProgramInfoFragment;

    private long totalPastTime;
    private double totalPastDistance;
    private boolean trainingMarkedAsCompleted;

    @Inject
    User userProfile;

    /**
     * Perform initialization of all fragments and loaders
     *
     * @param savedInstanceState contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Creating PlayerActivity");

        // Get the application state
        indoorAppState = ((App) getApplication()).getIndoorAppState();
        // Contains the user profile
        // The View containing the controls for the video (progress bar, pause button)
        videoControllerView = new VideoControllerView(this);
        videoControllerView.setIndoorAppState(indoorAppState);
        videoControllerView.setUserProfile(userProfile);

        setContentView(R.layout.activity_play_movie);

        // Restore the current segment and point in time, if the video was playing when the activity closed
        if (savedInstanceState != null) {
            Log.w(TAG, "Restoring Instance State");
            currentSegment = savedInstanceState.getInt(CURRENT_SEGMENT_STATE_KEY, 0);
            currentVideoAdvance = savedInstanceState.getInt(CURRENT_VIDEO_ADVANCE_STATE_KEY, 0);
            totalPastTime = savedInstanceState.getLong(TOTAL_PAST_TIME, 0);
            totalPastDistance = savedInstanceState.getDouble(TOTAL_PAST_DISTANCE, 0);
        } else {
            currentSegment = 0;
            currentVideoAdvance = 0;
            totalPastTime = 0;
            totalPastDistance = 0;
        }

        // Improve the frame rates (for motion detection)
        getWindow().setFormat(PixelFormat.UNKNOWN);

        // Don't block or dim the screen when video is playing.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // For motion detection.  Need to understand these
        if (Preferences.USE_RGB) {
            motionDetector = new RgbMotionDetection();
        } else if (Preferences.USE_LUMA) {
            motionDetector = new LumaMotionDetection();
        } else {
            // Using State based (aggregate map)
            motionDetector = new AggregateLumaMotionDetection();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.w(TAG, "onStart");

        // This is where a preview of the camera is shown for movement detection.
        Log.w(TAG, "Creating surface view motion detection");
        surfaceViewMotionDetection = findViewById(R.id.surfaceMotionDetection);
        surfaceHolderMotionDetection = surfaceViewMotionDetection.getHolder();
        surfaceHolderMotionDetection.addCallback(PlayerActivity.this);

        // After the surface where the camera preview is created, create second surface (the one for the video playing)
        // This is the surface where the video is played. Callbacks are surfaceCreated and surfaceChanged
        // In surfaceCreated we assign the MediaPlayer to the surface.
        Log.w(TAG, "Creating surface view track video");
        surfaceViewTrackVideo = findViewById(R.id.surfaceTrackVideo);
        surfaceHolderTrackVideo = surfaceViewTrackVideo.getHolder();
        surfaceHolderTrackVideo.addCallback(PlayerActivity.this);

        surfaceViewTrackVideo.setVisibility(View.VISIBLE);

        surfaceViewMotionDetection.setVisibility(View.VISIBLE);

        surfaceViewTrackVideo.setZOrderMediaOverlay(true);

    }

    /**
     * Called when the activity will start interacting with the user.  Inherited from android.app.Activity
     */
    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.w(TAG, "Saving Instance State");

        outState.putInt(CURRENT_SEGMENT_STATE_KEY, currentSegment);
        outState.putInt(CURRENT_VIDEO_ADVANCE_STATE_KEY, currentVideoAdvance);
        outState.putLong(TOTAL_PAST_TIME, totalPastTime);
        outState.putDouble(TOTAL_PAST_DISTANCE, totalPastDistance);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Called when the system is about to start resuming a previous activity. Inherited from android.app.Activity
     */
    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "onPause");

        // Stop the video player
        try {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
                currentVideoAdvance = videoPlayer.getCurrentPosition();
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in onPause: Cannot pause videoPlayer.");
        }

        // Dismiss dialogs if open
        if (dialogFragmentNextVideo != null && dialogFragmentNextVideo.isVisible())
            dialogFragmentNextVideo.dismiss();
        if (stopFollowDialog != null && stopFollowDialog.isShowing()) stopFollowDialog.dismiss();
        if (readySetGoDialogFragment != null && readySetGoDialogFragment.isVisible())
            readySetGoDialogFragment.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.w(TAG, "onStop");

        // Stop the video player
        try {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
                currentVideoAdvance = videoPlayer.getCurrentPosition();
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in onPause: Cannot pause videoPlayer.");
        }

        // Stop all the voices in the app
        ((App) getApplication()).cancelSpeak();

        // Free resources used when playing video
        if (videoPlayer != null) videoPlayer.release();

        // Deactivate motion detection
        deactivateCameraPreview();
    }

    /**
     * When user pushes the Hardware button, the app forces the Activity to go portrait, and it looks ugly.
     *
     * @param newConfig the new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        SurfaceView surfaceViewTrackVideo = findViewById(R.id.surfaceTrackVideo);

        // If changing to landscape, show hidden elements
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (surfaceViewTrackVideo != null) surfaceViewTrackVideo.setVisibility(View.VISIBLE);
            if (videoControllerView != null) videoControllerView.setVisibility(View.VISIBLE);
            // Force motion detection to start
            if (surfaceViewMotionDetection != null) {
                surfaceViewMotionDetection.setVisibility(View.VISIBLE);
                deactivateCameraPreview();
                activateCameraPreview();
            }
        }
        // If changing to portrait hide elements on screen
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (surfaceViewTrackVideo != null) surfaceViewTrackVideo.setVisibility(View.INVISIBLE);
            if (videoControllerView != null) videoControllerView.setVisibility(View.INVISIBLE);
            if (surfaceViewMotionDetection != null)
                surfaceViewMotionDetection.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Handle the back button
     */
    @Override
    public void onBackPressed() {
        try {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in onBackPressed: Cannot pause videoPlayer.");
        }

        // So far we just paused the video.
        isCancelling = false;

        // Voice message
        ((App) getApplication()).speakOut(getString(R.string.workout_paused));

        // Create the Dialog asking the user if he/she wants to stop or resume the run
        // TODO: Create a fragment with this
        stopFollowDialog = new Dialog(this, android.R.style.Theme_Dialog);

        View promptView = View.inflate(this, R.layout.popup_finish_video, null);

        // Used to set the font. Currently is the only way to do it.
        Button continueButton = promptView.findViewById(R.id.continue_btn);
        Button finishButton = promptView.findViewById(R.id.finish_btn);
        TextView title_seg = promptView.findViewById(R.id.title_seg);
        TextView title_war = promptView.findViewById(R.id.title_war);

        continueButton.setTypeface(Utils.getFontBebasNeue(this));
        finishButton.setTypeface(Utils.getFontBebasNeue(this));
        title_seg.setTypeface(Utils.getFontBebasNeue(this));
        title_war.setTypeface(Utils.getFontBebasNeue(this));

        // User pushes continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopFollowDialog.dismiss();
            }
        });

        // User pushes stop race button
        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((App) getApplication()).speakOut(getString(R.string.finished_race));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error while sleeping", e);
                }

                // We want to finish the Activity
                isCancelling = true;

                stopFollowDialog.dismiss();

                Intent intent = new Intent(PlayerActivity.this, IndoorNotFinishedActivity.class);
                startActivity(intent);

                PlayerActivity.this.finish();
            }
        });

        // This should also cater for out of the dialog click
        stopFollowDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i(TAG, "Dismissing back pressed dialog");
                if (!isCancelling) {
                    ((App) getApplication()).speakOut(getString(R.string.cool_lets_continue));
                    resumeVideo();
                    activateCameraPreview();
                }
            }
        });

        Log.w(TAG, "Showing pause dialog");
        deactivateCameraPreview();

        if (stopFollowDialog.getWindow() != null) {
            stopFollowDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        stopFollowDialog.setCancelable(true);
        stopFollowDialog.setContentView(promptView);
        stopFollowDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        stopFollowDialog.show();
    }

    //</editor-fold>

    //<editor-fold desc="SurfaceHolder">

    /**
     * Required to implement by interface SurfaceHolder.Callback
     *
     * @param holder the surface containing the player
     * @param format the format of the surface
     * @param width  the width of the surface
     * @param height the height of the surface
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder == surfaceHolderMotionDetection) {
            Log.w(TAG, "Motion detection surface changed");

            if (camera == null) {
                Log.e(TAG, "Camera is null in surfaceChanged");
                return;
            }

            @SuppressWarnings("deprecation")
            Camera.Parameters parameters = camera.getParameters();

            @SuppressWarnings("deprecation")
            Camera.Size size = getBestPreviewSize(width, height, parameters);

            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.w(TAG, String.format("Motion detection surface width=%d height=%d", size.width, size.height));
            }

            camera.setParameters(parameters);
        }
    }

    /**
     * Required to implement by interface SurfaceHolder.Callback
     *
     * @param holder the surface containing the player
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Assign the MediaPlayer to the surface created.
        if (holder == surfaceHolderTrackVideo) {

            Log.w(TAG, "Player surface created");

            videoPlayer = new MediaPlayer();
            videoPlayer.setDisplay(surfaceHolderTrackVideo);

            prepareMediaPlayer();
        }

        if (holder == surfaceHolderMotionDetection) {
            Log.w(TAG, "Motion detection surface created");

            // The other surface, for video playing, is only created after we have permission to use the camera
            prepareCamera();
        }
    }

    /**
     * Required to implement by interface SurfaceHolder.Callback
     *
     * @param holder the surface containing the player
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (holder == surfaceHolderTrackVideo) {
            Log.w(TAG, "Player surface destroyed");
        }

        if (holder == surfaceHolderMotionDetection) {
            Log.w(TAG, "Motion detection surface destroyed");

            if (camera != null) {
                deactivateCameraPreview();
                camera.release();
                camera = null;
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="MediaPlayerVideo">

    /**
     * Required to implement by interface MediaPlayer.OnPreparedListener
     *
     * @param mediaPlayer the video player
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        long advertisedDuration = indoorAppState.getSelectedTrack().getSegment(currentSegment).getDuration();
        long realDuration = mediaPlayer.getDuration();

        // The API has not the videos exact length
        if (advertisedDuration != realDuration) {
            indoorAppState.getSelectedTrack().getSegment(currentSegment).setDuration(realDuration);
            Log.e(TAG, String.format("The duration of the video (%d) is different than what we were told (%d)", realDuration, advertisedDuration));
        }

        // We're restarting the activity
        if (currentVideoAdvance != 0) {
            Log.w(TAG, "Setting current video advance to " + String.valueOf(currentVideoAdvance));
            mediaPlayer.seekTo(currentVideoAdvance);

            // When next video comes, it will begin from 0
            currentVideoAdvance = 0;

            // Force motion detection to start
            deactivateCameraPreview();
            activateCameraPreview();
        }

        // Used to send message to videoControllerView
        isVideoCompleted = false;
    }

    /**
     * Prepares the video player and starts the process that will cause it to play.
     * Called when the Activity creates the surface where the video will be shown and also after Next Video Dialog is dismissed
     */
    private void prepareMediaPlayer() {
        try {
            Uri videoUri = indoorAppState.getSelectedTrack().getSegment(currentSegment).getVideo();
            Log.w(TAG, "VideoURI: " + videoUri.toString() + " for Track: " + indoorAppState.getSelectedTrack().getId() + ", program: " + indoorAppState.getSelectedTrack().getSelectedProgram());

            videoPlayer.setDataSource(PlayerActivity.this, videoUri);
            videoPlayer.prepare();
            videoPlayer.setOnPreparedListener(PlayerActivity.this);
            videoPlayer.setOnCompletionListener(PlayerActivity.this);
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't prepare MediaPlayer", ex);
        }

        // Only play greeting audio on the first segment
        if (currentSegment == 0 && currentVideoAdvance == 0) {
            ((App) getApplication()).speakOutFlush(getString(R.string.lets_start_coach_is_active));

            // Call the Ready... Set... Go... dialog and after the dialog is dismissed, it will call startVideo()
            showStartRunDialogPopUp();
        }
        // Otherwise go directly to play the video
        else {
            startVideo();
        }
    }

    /**
     * Start the video play
     */
    private void startVideo() {
        if (videoPlayer == null) {
            Log.e(TAG, "MediaPlayer is null, cannot play");
            return;
        }

        try {
            surfaceViewTrackVideo.setVisibility(View.VISIBLE);

            // Prepares the videoControllerView, which is the view where the pause button and progress bar are.
            videoControllerView.setMediaPlayerControl(this);
            videoControllerView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            videoControllerView.setCurrentSegment(currentSegment);
            videoControllerView.show();

            videoPlayer.start();

            activateCameraPreview();
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException in startVideo(): Cannot start videoPlayer now: " + e.getLocalizedMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "videoControllerView is null", e);
        }
    }

    /**
     * Continue with the video playing
     */
    private void resumeVideo() {
        try {
            if (videoPlayer != null) {
                videoPlayer.start();
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in resumeVideo: Cannot resume videoPlayer.");
        }

        try {
            videoControllerView.show();
        } catch (NullPointerException ex) {
            Log.e(TAG, "videoControllerView not existing", ex);
        }
    }

    /**
     * Process a Pause key
     */
    public void userPause() {
        // Shows the Are you Sure to go back alert
        onBackPressed();
    }

    public void programInfo() {
        // API for performing a set of Fragment operations.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Find existing fragments with same tag
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        // Remove an existing fragment
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }

        fragmentTransaction.addToBackStack(null);

        indoorProgramInfoFragment = IndoorProgramInfoFragment.newInstance(indoorAppState.getSelectedTrack(), currentSegment);

        indoorProgramInfoFragment.show(fragmentTransaction, "dialog");
    }

    /**
     * From MediaPlayer.OnCompletionListener interface
     *
     * @param mediaPlayer the video player
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i(TAG, "Video Completed");

        isVideoCompleted = true;

        videoControllerView.setVisibility(View.INVISIBLE);

        // Stops playback
        mediaPlayer.stop();

        // Resets the MediaPlayer to initialized state. Will need to set the data source and prepare() again.
        mediaPlayer.reset();

        // If there are more videos to play, show the dialog telling the user to get ready. On dismiss it will call prepareMediaPlayer().
        if (currentSegment < indoorAppState.getSelectedTrack().getSegments().size() - 1) {
            currentSegment++;
            showPopUpNext();
            Log.i(TAG, "Next segment: " + currentSegment);
        } else {
            Log.i(TAG, "Track finished, updating statistics.");

            ((App) getApplication()).speakOut(getString(R.string.congratulations_finished_race));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error while sleeping", e);
            }

            // Show summary of exercise
            Intent intent = new Intent(this, PostIndoorActivity.class);
            startActivity(intent);
            PlayerActivity.this.finish();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Ready Set Go">

    /**
     * Calls the Ready Set Go dialog
     */
    private void showStartRunDialogPopUp() {
        // API for performing a set of Fragment operations.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Find existing fragments with same tag
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        // Remove an existing fragment
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }

        fragmentTransaction.addToBackStack(null);

        readySetGoDialogFragment = ReadySetGoDialogFragment.newInstance(this);

        readySetGoDialogFragment.show(fragmentTransaction, "dialog");
    }

    /**
     * The Ready Set Go fragment dismissed. Let's pay the video!
     */
    @Override
    public void onDismissDialogFragmentReadySetGo() {
        Log.i(TAG, "Dismissing Ready Set Go Dialog");

        startVideo();

        ((App) getApplication()).cancelSpeak();
    }

    //</editor-fold>

    //<editor-fold desc="Next Video">

    private void showPopUpNext() {
        // API for performing a set of Fragment operations.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Find existing fragments with same tag
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        // Remove an existing fragment
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }

        // Pause the movement detection
        deactivateCameraPreview();


        fragmentTransaction.addToBackStack(null);

        Double speed = indoorAppState.getSelectedTrack().getSegment(currentSegment).getSpeed();
        Double slope = indoorAppState.getSelectedTrack().getSegment(currentSegment).getSlope();

        dialogFragmentNextVideo = DialogFragmentNextVideo.newInstance(this, speed, slope);
        dialogFragmentNextVideo.show(fragmentTransaction, "dialog");
    }

    /**
     * The Ready Set Go fragment is dismissed.
     */
    @Override
    public void onDismissDialogFragmentNextVideo() {
        Log.i(TAG, "Dismissing next video dialog");
        ((App) getApplication()).cancelSpeak();

        // Prepares the video to play.
        prepareMediaPlayer();
    }

    //</editor-fold>

    //<editor-fold desc="Are you still there? Dialog">

    /**
     * The user pressed continue after pause
     */
    @Override
    public void onStillDialogAwakeWindow() {
        Log.w(TAG, "Yes, we are awake");
        resumeVideo();
        activateCameraPreview();
    }

    /**
     * Shows the Are you still there? Dialog
     */
    private void awake() {
        if (videoPlayer != null) {
            videoPlayer.pause();
            deactivateCameraPreview();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("awake");

        if (prev != null) {
            Log.w(TAG, "Hey, we're busy!");
            return;
            //  ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogAwakeWindow newFragment = DialogAwakeWindow.newInstance(this);
        newFragment.show(ft, "awake");
    }

    /**
     * User pressed the "Cancel" button on the Ready Set Go dialog
     */
    @Override
    public void onFinishDialogAwakeWindow() {
        Log.w(TAG, "Nobody home, it seems.");

        ((App) getApplication()).speakOut(getString(R.string.dont_worry_you_can_try_again));
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error while sleeping", e);
        }

        Intent intent = new Intent(PlayerActivity.this, IndoorNotFinishedActivity.class);
        startActivity(intent);

        PlayerActivity.this.finish();
    }

    //</editor-fold>

    //<editor-fold desc="Video Controls">

    /**
     * The control asks for the current video position
     *
     * @return The current position
     */
    private int getCurrentPosition() {
        if (videoPlayer == null) {
            return 0;
        }

        try {
            if (videoPlayer.isPlaying()) {
                return videoPlayer.getCurrentPosition();
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in getCurrentPosition(): Cannot get current position of videoPlayer.");
        }
        return 0;
    }

    /**
     * The control asks whether the video is playing
     *
     * @return if the video is playing
     */
    @Override
    public boolean isPlaying() {
        try {
            return videoPlayer != null && videoPlayer.isPlaying();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "IllegalStateException in isPlaying(): Cannot query playing status of videoPlayer.");
        }

        return false;
    }

    @Override
    public long getTotalPastTime() {
        return totalPastTime;
    }

    @Override
    public double getTotalPastDistance() {
        return totalPastDistance;
    }

    @Override
    public int getPosition() {
        return (int) ((double) totalPastTime / indoorAppState.getSelectedTrack().getDuration() * 100);
    }

    @Override
    public void updateIndicators() {
        List<Segment> segments = indoorAppState.getSelectedTrack().getSegments();

        double currentSegmentDistance = 0;
        double totalPastDistance = 0;

        long currentSegmentPosition = 0;
        long totalPastTime = 0;

        for (int i = 0; i < currentSegment; i++) {
            totalPastTime += segments.get(i).getDuration();
            totalPastDistance += segments.get(i).getDistance();
        }

        if (videoPlayer != null && isPlaying()) {
            currentSegmentPosition = getCurrentPosition();
            currentSegmentDistance = segments.get(currentSegment).getDistance() * currentSegmentPosition / segments.get(currentSegment).getDuration();
        } else if (isVideoCompleted) {
            currentSegmentPosition = segments.get(currentSegment).getDuration();
            currentSegmentDistance = segments.get(currentSegment).getDistance();
        }

        totalPastTime += currentSegmentPosition;
        totalPastDistance += currentSegmentDistance;

        this.totalPastDistance = totalPastDistance;
        this.totalPastTime = totalPastTime;

        if (indoorProgramInfoFragment != null && indoorProgramInfoFragment.isVisible()) {
            indoorProgramInfoFragment.updateTimes(totalPastTime, getCurrentPosition());
        }

        if ((double) getPosition() / 100.0 > App.minimumPacePercentageForCompleteTraining && !trainingMarkedAsCompleted) {
            trainingMarkedAsCompleted = true;
            indoorAppState.markTrackCompleted();
            indoorAppState.save();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Motion Detection">

    private void prepareCamera() {

        // After Android 6.0 we need to check app permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                camera = null;
                Toast.makeText(this, getString(R.string.camera_permission_denied_motion_detection), Toast.LENGTH_SHORT).show();
            } else {
                // Older versions of Android
                openFrontFacingCameraGingerbread();
            }
        }
        // Permission already granted
        else {
            openFrontFacingCameraGingerbread();
        }
    }

    @SuppressWarnings("deprecation")
    private void openFrontFacingCameraGingerbread() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.w(TAG, "Found a front camera");

                if (camera == null) {
                    camera = Camera.open(camIdx);
                }

                try {
                    camera.setPreviewDisplay(surfaceHolderMotionDetection);
                } catch (IOException ex) {
                    Log.e(TAG, "Cannot set preview display");
                    return;
                }

                return;
            }
        }
    }

    private void activateCameraPreview() {
        Log.w(TAG, "Activating camera preview");

        if (camera != null) {

            lastMotionAlert = System.currentTimeMillis();

            //noinspection deprecation
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera cam) {
                    if (data == null) return;

                    @SuppressWarnings("deprecation")
                    Camera.Size size = cam.getParameters().getPreviewSize();
                    if (size == null) return;

                    if (!GlobalData.isPhoneInMotion()) {
                        PlayerActivity.DetectionThread thread = new PlayerActivity.DetectionThread(data, size.width, size.height);
                        thread.start();
                    }
                }
            });

            camera.startPreview();
            inCameraPreview = true;
        }
    }

    private void deactivateCameraPreview() {
        Log.w(TAG, "Deactivating camera preview");

        if (camera != null) {
            camera.setPreviewCallback(null);
            if (inCameraPreview) {
                camera.stopPreview();
                inCameraPreview = false;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) result = size;
                }
            }
        }

        if (result == null) {
            Log.e(TAG, "best preview size is null");
        } else {
            Log.w(TAG, String.format("best preview size is: %d x %d", result.width, result.height));
        }

        return result;
    }

    private final class DetectionThread extends Thread {

        private final byte[] data;
        private final int width;
        private final int height;

        private DetectionThread(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            if (!motionDetectionProcessing.compareAndSet(false, true)) return;

            try {
                // Current frame (with changes)
                int[] img;

                if (Preferences.USE_RGB) {
                    img = ImageProcessing.decodeYUV420SPtoRGB(data, width, height);
                } else {
                    img = ImageProcessing.decodeYUV420SPtoLuma(data, width, height);
                }

                long seconds = (System.currentTimeMillis() - lastMotionAlert) / 1000;

                if (img != null) {
                    //Log.w(TAG, String.format("w: %d h: %d", width, height));

                    if (motionDetector.detect(img, width, height)) {
                        Log.w(TAG, "We have movement");

                        lastMotionAlert = System.currentTimeMillis();
                    } else {
                        Log.w(TAG, String.format("No movement: %d seconds", seconds));

                        if (seconds > 15) {
                            Log.w(TAG, "15 seconds without movement");
                            awake();
                        }
                    }
                } else {
                    Log.e(TAG, "img is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                motionDetectionProcessing.set(false);
            }
            motionDetectionProcessing.set(false);
        }
    }
    //</editor-fold>
}
