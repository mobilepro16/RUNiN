package com.runin.runinapp.data;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.runin.runinapp.R;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Samuel Kobelkowsky on 7/24/17.
 * <p>
 * Every Indoor track has a number of segments
 */
public class Segment implements Serializable {
    private static final String TAG = Segment.class.getSimpleName();

    /**
     * The kind of exercise
     */
    @NonNull
    private final Kind kind;

    /**
     * The slope (inclination)
     */
    private final double slope;

    /**
     * The sequence. Used to order the segments in a track
     */
    private final int sequence;

    /**
     * The duration in milliseconds
     */
    private long duration;

    /**
     * The speed required for a beginner following program 1
     */
    private double speedBeginner1;

    /**
     * The speed required for a competitor following program 1
     */
    private double speedCompetitor1;

    /**
     * The speed required for an athlete following program 1
     */
    private double speedAthlete1;

    /**
     * The speed required for a runner following program 1
     */
    private double speedRunner1;

    /**
     * The speed required for a beginner following program 2
     */
    private double speedBeginner2;

    /**
     * The speed required for a competitor following program 2
     */
    private double speedCompetitor2;

    /**
     * The speed required for an athlete following program 2
     */
    private double speedAthlete2;

    /**
     * The speed required for a runner following program 2
     */
    private double speedRunner2;

    /**
     * The local video URI location
     */
    private String videoUri;

    /**
     * The date the segment was created on the API
     */
    private Date apiCreatedDate;

    /**
     * The URI of the remote video where it can be downloaded
     */
    private String videoRemoteUri;

    /**
     * The length of the video in bytes
     */
    private long byteLength;

    /**
     * The indoor level that the user selected. Used to calculate the speed.
     */
    @Nullable
    private IndoorLevel indoorLevel;

    /**
     * The speed level that the user selected.
     */
    @Nullable
    private SpeedLevel speedLevel;

    /**
     * The filename of the video
     */
    private String filename;

    /**
     * Creates an indoor track segment
     *
     * @param duration The duration of the segment in milliseconds
     * @param kind     The exercise kind during the segment
     * @param slope    The incline of the segment
     */
    Segment(long duration, @NonNull Kind kind, double slope, int sequence) {
        this.duration = duration;
        this.kind = kind;
        this.slope = slope;
        this.sequence = sequence;
    }

    /**
     * Copy constructor
     *
     * @param segment the Segment to be copied into a new Segment with de-referenced properties
     */
    Segment(Segment segment) {
        // Copy values
        this.slope = segment.slope;
        this.sequence = segment.sequence;
        this.duration = segment.duration;
        this.speedBeginner1 = segment.speedBeginner1;
        this.speedCompetitor1 = segment.speedCompetitor1;
        this.speedAthlete1 = segment.speedAthlete1;
        this.speedRunner1 = segment.speedRunner1;
        this.speedBeginner2 = segment.speedBeginner2;
        this.speedCompetitor2 = segment.speedCompetitor2;
        this.speedAthlete2 = segment.speedAthlete2;
        this.speedRunner2 = segment.speedRunner2;
        this.videoUri = segment.videoUri;
        this.videoRemoteUri = segment.videoRemoteUri;
        this.byteLength = segment.byteLength;
        this.speedLevel = segment.speedLevel;
        this.filename = segment.filename;

        // No need to do deep copy of these enums:
        this.kind = segment.kind;
        this.indoorLevel = segment.indoorLevel;

        // Need to deep copy these
        //this.downloadedDate = new Date(segment.downloadedDate.getTime());
        this.apiCreatedDate = segment.apiCreatedDate == null ? null : new Date(segment.apiCreatedDate.getTime());

        // Copy reference for these
        // ...
    }

    /**
     * Gets the distance of the segment, based on the average objective speed and duration.
     *
     * @return The distance
     */
    public double getDistance() {
        return getSpeed() * duration / 3600000;
    }

    /**
     * Gets duration of the segment in milliseconds.
     *
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the segment in milliseconds.
     *
     * @param duration the duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Gets the objective speed in km/h.
     *
     * @return the speed
     */
    public double getSpeed() {
        if (speedLevel == SpeedLevel.MODERATED) {
            switch (indoorLevel) {
                case BEGINNER:
                    return speedBeginner1;
                case COMPETITOR:
                    return speedCompetitor1;
                case ATHLETE:
                    return speedAthlete1;
                case RUNNER:
                    return speedRunner1;
                default:
                    Log.e(TAG, "Invalid indoorLevel");
                    return 0;
            }
        }
        else {
            switch (indoorLevel) {
                case BEGINNER:
                    return speedBeginner2;
                case COMPETITOR:
                    return speedCompetitor2;
                case ATHLETE:
                    return speedAthlete2;
                case RUNNER:
                    return speedRunner2;
                default:
                    Log.e(TAG, "Invalid indoorLevel");
                    return 0;
            }
        }
    }

    /**
     * Sets the speed of the segment. Need an indoor level set up for that.
     * TODO: Check why this method is never used.
     *
     * @param speed The speed
     */
    @SuppressWarnings("unused")
    public void setSpeed(double speed) {
        if (speedLevel == SpeedLevel.MODERATED) {
            switch (indoorLevel) {
                case BEGINNER:
                    setSpeedBeginner1(speed);
                    break;
                case COMPETITOR:
                    setSpeedCompetitor1(speed);
                    break;
                case ATHLETE:
                    setSpeedAthlete1(speed);
                    break;
                case RUNNER:
                    setSpeedRunner1(speed);
                    break;
            }
        }
        else {
            switch (indoorLevel) {
                case BEGINNER:
                    setSpeedBeginner2(speed);
                    break;
                case COMPETITOR:
                    setSpeedCompetitor2(speed);
                    break;
                case ATHLETE:
                    setSpeedAthlete2(speed);
                    break;
                case RUNNER:
                    setSpeedRunner2(speed);
                    break;
            }
        }
    }

    /**
     * Gets the kind of exercise intended on this segment.
     *
     * @return the exercise kind
     */
    @NonNull
    public Kind getKind() {
        return kind;
    }

    /**
     * Gets the average segment slope.
     *
     * @return the slope
     */
    public double getSlope() {
        return slope;
    }

    /**
     * Gets the URI for the video of this segment.
     *
     * @return the video URI
     */
    public Uri getVideo() {
        return Uri.parse(videoUri);
    }

    /**
     * Gets the Android Resource ID for the icon.
     *
     * @return the resource ID for the icon
     */
    public int getIcon() {
        switch (kind) {
            case WARM_UP:
                return R.drawable.indoor_icon_caminar;
            case FAST_WALK:
                return R.drawable.indoor_icon_caminar_rapido;
            case JOG:
                return R.drawable.indoor_icon_trotar;
            case RUN:
                return R.drawable.indoor_icon_correr;
            case SPRINT:
                return R.drawable.indoor_icon_sprint;
            case RECOVERY:
                return R.drawable.indoor_icon_caminar;
            case COOL_DOWN:
                return R.drawable.indoor_icon_enfriar;
        }

        Log.e(TAG, "Invalid Badge icon");
        return 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Segment duration: %d, kind: %d, slope: %f, filename: %s, remoteUrl: %s, downloaded: %s, bytes: %d, speedBeginner: %f\n",
                duration, kind.getValue(), slope, filename, videoRemoteUri, getDownloadedDate() != null ? getDownloadedDate() : "-", byteLength, speedBeginner1);
    }

    /**
     * Sets the URI for the local video
     *
     * @param videoUri The URI for the local video
     */
    void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    /**
     * Gets the URI for the local video
     *
     * @return The URI for the local video
     */
    public String getVideoRemoteUri() {
        return videoRemoteUri;
    }

    /**
     * The URL to download the video
     *
     * @param videoRemoteUri the URL in String format
     */
    void setVideoRemoteUri(String videoRemoteUri) {
        this.videoRemoteUri = videoRemoteUri;
    }

    /**
     * The file length of the video
     *
     * @return the file length
     */
    private long getByteLength() {
        return byteLength;
    }

    /**
     * Sets the file length of the video
     *
     * @param byteLength the file length
     */
    void setByteLength(long byteLength) {
        this.byteLength = byteLength;
    }

    /**
     * Sets the indoor level
     *
     * @param indoorLevel The indoor level
     */
    void setIndoorLevel(@Nullable IndoorLevel indoorLevel) {
        this.indoorLevel = indoorLevel;
    }

    /**
     * Sets the objective speed in km/h.
     *
     * @param speed the speed
     */
    void setSpeedBeginner1(double speed) {
        this.speedBeginner1 = speed;
    }

    /**
     * Sets the objective speed in km/h.
     *
     * @param speed the speed
     */
    void setSpeedCompetitor1(double speed) {
        this.speedCompetitor1 = speed;
    }

    /**
     * Sets the objective speed in km/h.
     *
     * @param speed the speed
     */
    void setSpeedAthlete1(double speed) {
        this.speedAthlete1 = speed;
    }

    /**
     * Sets the objective speed in km/h.
     *
     * @param speed the speed
     */
    void setSpeedRunner1(double speed) {
        this.speedRunner1 = speed;
    }

    /**
     * Gets the downloaded date of the video file.
     *
     * @return The downloaded date
     */
    Date getDownloadedDate() {
        String filename = getFilename();
        if (filename == null || filename.isEmpty()) {
            Log.e(TAG, "Don't have a filename");
            return null;
        }

        File file = new File(getFilename());
        return new Date(file.lastModified());
    }

    /**
     * Sets the speed for beginners on program 2
     *
     * @param speedBeginner2 the speed
     */
    void setSpeedBeginner2(double speedBeginner2) {
        this.speedBeginner2 = speedBeginner2;
    }

    /**
     * Sets the speed for competitors on program 2
     *
     * @param speedCompetitor2 the speed
     */
    void setSpeedCompetitor2(double speedCompetitor2) {
        this.speedCompetitor2 = speedCompetitor2;
    }

    /**
     * Sets the speed for athletes on program 2
     *
     * @param speedAthlete2 the speed
     */
    void setSpeedAthlete2(double speedAthlete2) {
        this.speedAthlete2 = speedAthlete2;
    }

    /**
     * Sets the speed for runners on program 2
     *
     * @param speedRunner2 the speed
     */
    void setSpeedRunner2(double speedRunner2) {
        this.speedRunner2 = speedRunner2;
    }

    /**
     * Gets the filename of the video
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename of the video
     *
     * @param filename The filename of the video
     */
    void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the sequence
     *
     * @return the sequence
     */
    int getSequence() {
        return sequence;
    }

    /**
     * Gets the created date in the API
     *
     * @return the created date
     */
    Date getApiCreatedDate() {
        return apiCreatedDate;
    }

    /**
     * Sets the created date in the API
     *
     * @param apiCreatedDate the created date
     */
    void setApiCreatedDate(Date apiCreatedDate) {
        this.apiCreatedDate = apiCreatedDate;
    }

    /**
     * Determines if the video has been downloaded
     *
     * @return whether the video has been downloaded
     */
    public boolean isDownloaded() {
        if (byteLength == 0) {
            Log.e(TAG, "Byte length is 0");
            return false;
        }

        if (filename == null || filename.isEmpty()) {
            Log.e(TAG, "Filename is null or empty");
            return false;
        }

        File file = new File(filename);
        if (!file.exists()) {
            Log.i(TAG, String.format("File %s doesn't exist", filename));
        }

        return file.exists() && file.length() > 0.9 * getByteLength();
    }

    /**
     * Sets the speed level
     *
     * @param speedLevel the speed level
     */
    void setSpeedLevel(@NonNull SpeedLevel speedLevel) {
        this.speedLevel = speedLevel;
    }

    /**
     * The kind of intended exercise for a segment.
     */
    public enum Kind {
        WARM_UP(0),
        FAST_WALK(1),
        JOG(2),
        RUN(3),
        SPRINT(4),
        RECOVERY(5),
        COOL_DOWN(6);

        private final int value;

        Kind(int value) {
            this.value = value;
        }

        public static Kind fromInt(int i) {
            for (Kind val : Kind.values()) {
                if (val.getValue() == i) {
                    return val;
                }
            }

            return null;
        }

        public int getValue() {
            return value;
        }
    }
}