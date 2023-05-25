package com.runin.runinapp.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.runin.runinapp.R;
import com.runin.runinapp.utils.SegmentsDownloader;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Model data for indoor tracks
 * Created by Samuel Kobelkowsky on 7/24/17.
 */
public class Track implements Serializable {
    // The lists of different indoor tracks
    public static final String DEMO_ID = "com.runinapp.runin.demo";
    public static final String DEMO_DEVEL_ID = "com.runinapp.runin.demo_devel";
    public static final String HIGHER_AND_FASTER_ID = "com.runinapp.runin.higher_and_faster";
    public static final String HARDER_AND_STRONGER_ID = "com.runinapp.runin.harder_and_stronger";
    public static final String FURTHER_AND_LONGER_ID = "com.runinapp.runin.further_and_longer";
    private static final String TAG = Track.class.getSimpleName();

    /**
     * The track ID
     */
    @NonNull
    private final String id;

    /**
     * The segments for this track when running program 1
     */
    @NonNull
    private final ArrayList<Segment> program1Segments;

    /**
     * The segments for this track when running program 2
     */
    @NonNull
    private final ArrayList<Segment> program2Segments;

    /**
     * All the segments that were added to the track. May only contain program 1 or 2 tracks
     */
    @NonNull
    private final ArrayList<Segment> allSegments;

    /**
     * The badge obtained after completing program 1
     */
    @NonNull
    private final Badge program1Badge;

    /**
     * The badge obtained after completing program 2
     */
    @Nullable
    private final Badge program2Badge;

    /**
     * The advance percentage obtained after completing this track
     */
    private final double advancePercentage;

    /**
     * The track that after completed unlocks this track
     */
    @Nullable
    private final String unlockedBy;

    // Resources

    /**
     * The resource name for the title of this track
     */
    @NonNull
    private final String titleId;

    /**
     * The resource name for the geographic location name where this track is supposed to be
     */
    @NonNull
    private final String locationId;

    /**
     * The resource name for the image id of the background of the track adapter
     */
    @NonNull
    private final String backgroundResourceId;

    /**
     * The resource name for the image of the map of the track
     */
    @NonNull
    private final String mapResourceId;

    /**
     * The resource name of the description of the track
     */
    @NonNull
    private final String specsId;

    /**
     * The resource name of the title of the program 1
     */
    @NonNull
    private final String program1TitleId;

    /**
     * The resource name of the description of the program 1
     */
    @NonNull
    private final String program1DescriptionId;

    /**
     * The resource name of the title of the program 2
     */
    @Nullable
    private final String program2TitleId;

    /**
     * The resource name of the description of the program 2
     */
    @Nullable
    private final String program2DescriptionId;

    /**
     * The calories spent for the beginner speed level moderated of program 1
     */
    private final double caloriesFactor1BeginnerModerated;

    /**
     * The required caloriesFactor for the beginner speed level moderated of program 2
     */
    private final double caloriesFactor2BeginnerModerated;

    /**
     * The required caloriesFactor for the competitor speed level moderated of program 1
     */
    private final double caloriesFactor1CompetitorModerated;

    /**
     * The required caloriesFactor for the competitor speed level moderated of program 2
     */
    private final double caloriesFactor2CompetitorModerated;

    /**
     * The required caloriesFactor for the athlete speed level moderated of program 1
     */
    private final double caloriesFactor1AthleteModerated;

    /**
     * The required caloriesFactor for the athlete speed level moderated of program 2
     */
    private final double caloriesFactor2AthleteModerated;

    /**
     * The required caloriesFactor for the runner speed level moderated of program 1
     */
    private final double caloriesFactor1RunnerModerated;

    /**
     * The required caloriesFactor for the runner speed level moderated of program 2
     */
    private final double caloriesFactor2RunnerModerated;

    /**
     * The calories spent for the beginner speed level Intense of program 1
     */
    private final double caloriesFactor1BeginnerIntense;

    /**
     * The required caloriesFactor for the beginner speed level Intense of program 2
     */
    private final double caloriesFactor2BeginnerIntense;

    /**
     * The required caloriesFactor for the competitor speed level Intense of program 1
     */
    private final double caloriesFactor1CompetitorIntense;

    /**
     * The required caloriesFactor for the competitor speed level Intense of program 2
     */
    private final double caloriesFactor2CompetitorIntense;

    /**
     * The required caloriesFactor for the athlete speed level Intense of program 1
     */
    private final double caloriesFactor1AthleteIntense;

    /**
     * The required caloriesFactor for the athlete speed level Intense of program 2
     */
    private final double caloriesFactor2AthleteIntense;

    /**
     * The required caloriesFactor for the runner speed level Intense of program 1
     */
    private final double caloriesFactor1RunnerIntense;

    /**
     * The required caloriesFactor for the runner speed level Intense of program 2
     */
    private final double caloriesFactor2RunnerIntense;

    /**
     * The required effort for the beginner level of program 1
     */
    @NonNull
    private final Effort effort1Beginner;

    /**
     * The required effort for the beginner level of program 2
     */
    @Nullable
    private final Effort effort2Beginner;

    /**
     * The required effort for the competitor level of program 1
     */
    @NonNull
    private final Effort effort1Competitor;

    /**
     * The required effort for the competitor level of program 2
     */
    @Nullable
    private final Effort effort2Competitor;

    /**
     * The required effort for the athlete level of program 1
     */
    @NonNull
    private final Effort effort1Athlete;

    /**
     * The required effort for the athlete level of program 2
     */
    @Nullable
    private final Effort effort2Athlete;

    /**
     * The required effort for the runner level of program 1
     */
    @NonNull
    private final Effort effort1Runner;

    /**
     * The required effort for the runner level of program 2
     */
    @Nullable
    private final Effort effort2Runner;

    /**
     * The SKU for purchasing the track
     */
    @NonNull
    private final String sku;

    /**
     * The sequence of this track in the list of tracks
     */
    private final int sequence;

    /**
     * Tells if the track has been unlocked after running its required predecessor
     */

    private Boolean enabledForUser;
    /**
     * Tells if the track has been purchased
     */

    private Boolean purchased;
    /**
     * The date the track was ran
     */

    @Nullable
    private Date completedDate;
    /**
     * The selected program
     */

    private int selectedProgram;
    /**
     * The percent downloaded of all the videos
     */

    private int percentDownloaded;
    /**
     * Tells if the track is currently being downloaded
     */
    private boolean downloading = false;

    /**
     * An instance of the class that downloads the videos
     */
    private SegmentsDownloader downloader;

    /**
     * The selected indoor level that the user has
     */
    private IndoorLevel indoorLevel;

    /**
     * The speed level that the user selected
     */
    private SpeedLevel speedLevel;

    /**
     * Class Constructor
     *
     * @param sequence             Order to show to the user
     * @param id                   Store product identifier for this track
     * @param titleId              Short title for the track
     * @param locationId           Where the track is located?
     * @param backgroundResourceId The Android resource that contains the track background
     * @param mapResourceId        The Android resource that contains the track map
     * @param specsId              Long description of the track
     * @param advancePercentage    Percentage that the user advances in the program when the track is completed
     * @param unlockedBy           Id of the track that unlocks this track
     * @param program1TitleId      Some tracks have two different programs. Name of program 1.
     * @param program2TitleId      Some tracks have two different programs. Name of program 2.
     * @param program1Badge        The badge won for finishing program1.
     * @param program2Badge        The badge won for finishing program.
     * @param sku                  The SKU for the Google store.
     */
    public Track(int sequence,
                 @NonNull String id,
                 @NonNull String titleId,
                 @NonNull String locationId,
                 @NonNull String backgroundResourceId,
                 @NonNull String mapResourceId,
                 @NonNull String specsId,
                 double advancePercentage,
                 @Nullable String unlockedBy,
                 @NonNull String program1TitleId,
                 @Nullable String program2TitleId,
                 @NonNull Badge program1Badge,
                 @Nullable Badge program2Badge,
                 @NonNull String program1DescriptionId,
                 @Nullable String program2DescriptionId,
                 @NonNull Effort effort1Beginner,
                 @Nullable Effort effort2Beginner,
                 @NonNull Effort effort1Competitor,
                 @Nullable Effort effort2Competitor,
                 @NonNull Effort effort1Athlete,
                 @Nullable Effort effort2Athlete,
                 @NonNull Effort effort1Runner,
                 @Nullable Effort effort2Runner,
                 @NonNull String sku,
                 double caloriesFactor1BeginnerModerated,
                 double caloriesFactor1BeginnerIntense,
                 double caloriesFactor2BeginnerModerated,
                 double caloriesFactor2BeginnerIntense,
                 double caloriesFactor1CompetitorModerated,
                 double caloriesFactor1CompetitorIntense,
                 double caloriesFactor2CompetitorModerated,
                 double caloriesFactor2CompetitorIntense,
                 double caloriesFactor1AthleteModerated,
                 double caloriesFactor1AthleteIntense,
                 double caloriesFactor2AthleteModerated,
                 double caloriesFactor2AthleteIntense,
                 double caloriesFactor1RunnerModerated,
                 double caloriesFactor1RunnerIntense,
                 double caloriesFactor2RunnerModerated,
                 double caloriesFactor2RunnerIntense) {

        this.sequence = sequence;
        this.id = id;
        this.titleId = titleId;
        this.enabledForUser = false;
        this.purchased = false;
        this.locationId = locationId;
        this.backgroundResourceId = backgroundResourceId;
        this.mapResourceId = mapResourceId;
        this.specsId = specsId;
        this.advancePercentage = advancePercentage;
        this.unlockedBy = unlockedBy;
        this.program1TitleId = program1TitleId;
        this.program2TitleId = program2TitleId;
        this.sku = sku;
        this.program1Badge = program1Badge;
        this.program2Badge = program2Badge;
        this.program1DescriptionId = program1DescriptionId;
        this.program2DescriptionId = program2DescriptionId;

        this.effort1Beginner = effort1Beginner;
        this.effort2Beginner = effort2Beginner;
        this.effort1Competitor = effort1Competitor;
        this.effort2Competitor = effort2Competitor;
        this.effort1Athlete = effort1Athlete;
        this.effort2Athlete = effort2Athlete;
        this.effort1Runner = effort1Runner;
        this.effort2Runner = effort2Runner;

        this.caloriesFactor1BeginnerModerated = caloriesFactor1BeginnerModerated;
        this.caloriesFactor2BeginnerModerated = caloriesFactor2BeginnerModerated;
        this.caloriesFactor1AthleteModerated = caloriesFactor1AthleteModerated;
        this.caloriesFactor2AthleteModerated = caloriesFactor2AthleteModerated;
        this.caloriesFactor1CompetitorModerated = caloriesFactor1CompetitorModerated;
        this.caloriesFactor2CompetitorModerated = caloriesFactor2CompetitorModerated;
        this.caloriesFactor1RunnerModerated = caloriesFactor1RunnerModerated;
        this.caloriesFactor2RunnerModerated = caloriesFactor2RunnerModerated;
        this.caloriesFactor1BeginnerIntense = caloriesFactor1BeginnerIntense;
        this.caloriesFactor2BeginnerIntense = caloriesFactor2BeginnerIntense;
        this.caloriesFactor1AthleteIntense = caloriesFactor1AthleteIntense;
        this.caloriesFactor2AthleteIntense = caloriesFactor2AthleteIntense;
        this.caloriesFactor1CompetitorIntense = caloriesFactor1CompetitorIntense;
        this.caloriesFactor2CompetitorIntense = caloriesFactor2CompetitorIntense;
        this.caloriesFactor1RunnerIntense = caloriesFactor1RunnerIntense;
        this.caloriesFactor2RunnerIntense = caloriesFactor2RunnerIntense;

        this.program1Segments = new ArrayList<>();
        this.program2Segments = new ArrayList<>();
        this.allSegments = new ArrayList<>();
        this.completedDate = null;
        this.downloader = null;
    }

    /**
     * Creates a dep copy of a track
     *
     * @param track The track to copy from
     */
    public Track(Track track) {
        // Copy value
        this.sequence = track.sequence;
        this.id = track.id;
        this.titleId = track.titleId;
        this.enabledForUser = track.enabledForUser;
        this.purchased = track.purchased;
        this.locationId = track.locationId;
        this.backgroundResourceId = track.backgroundResourceId;
        this.mapResourceId = track.mapResourceId;
        this.specsId = track.specsId;
        this.advancePercentage = track.advancePercentage;
        this.unlockedBy = track.unlockedBy;
        this.program1TitleId = track.program1TitleId;
        this.program2TitleId = track.program2TitleId;
        this.sku = track.sku;
        this.program1DescriptionId = track.program1DescriptionId;
        this.program2DescriptionId = track.program2DescriptionId;
        this.selectedProgram = track.selectedProgram;
        this.percentDownloaded = track.percentDownloaded;
        this.downloading = track.downloading;
        this.speedLevel = track.speedLevel;

        // No need to do deep copy of these enums:
        this.effort1Beginner = track.effort1Beginner;
        this.effort2Beginner = track.effort2Beginner;
        this.effort1Competitor = track.effort1Competitor;
        this.effort2Competitor = track.effort2Competitor;
        this.effort1Athlete = track.effort1Athlete;
        this.effort2Athlete = track.effort2Athlete;
        this.effort1Runner = track.effort1Runner;
        this.effort2Runner = track.effort2Runner;

        this.caloriesFactor1BeginnerModerated = track.caloriesFactor1BeginnerModerated;
        this.caloriesFactor2BeginnerModerated = track.caloriesFactor2BeginnerModerated;
        this.caloriesFactor1AthleteModerated = track.caloriesFactor1AthleteModerated;
        this.caloriesFactor2AthleteModerated = track.caloriesFactor2AthleteModerated;
        this.caloriesFactor1CompetitorModerated = track.caloriesFactor1CompetitorModerated;
        this.caloriesFactor2CompetitorModerated = track.caloriesFactor2CompetitorModerated;
        this.caloriesFactor1RunnerModerated = track.caloriesFactor1RunnerModerated;
        this.caloriesFactor2RunnerModerated = track.caloriesFactor2RunnerModerated;
        this.caloriesFactor1BeginnerIntense = track.caloriesFactor1BeginnerIntense;
        this.caloriesFactor2BeginnerIntense = track.caloriesFactor2BeginnerIntense;
        this.caloriesFactor1AthleteIntense = track.caloriesFactor1AthleteIntense;
        this.caloriesFactor2AthleteIntense = track.caloriesFactor2AthleteIntense;
        this.caloriesFactor1CompetitorIntense = track.caloriesFactor1CompetitorIntense;
        this.caloriesFactor2CompetitorIntense = track.caloriesFactor2CompetitorIntense;
        this.caloriesFactor1RunnerIntense = track.caloriesFactor1RunnerIntense;
        this.caloriesFactor2RunnerIntense = track.caloriesFactor2RunnerIntense;

        this.indoorLevel = track.indoorLevel;

        // Copy reference for these
        this.downloader = track.downloader;

        // Need to deep copy these:
        this.completedDate = track.completedDate == null ? null : new Date(track.completedDate.getTime());
        this.program1Badge = new Badge(track.program1Badge.getLevel(), track.program1Badge.isCompleted());
        this.program2Badge = track.program2Badge == null ? null : new Badge(track.program2Badge.getLevel(), track.program2Badge.isCompleted());
        this.allSegments = new ArrayList<>();

        this.program1Segments = new ArrayList<>();
        for (Segment segment : track.program1Segments) {
            Segment copiedSegment = new Segment(segment);
            this.program1Segments.add(copiedSegment);
            this.allSegments.add(copiedSegment);
        }

        this.program2Segments = new ArrayList<>();
        for (Segment segment : track.program2Segments) {
            Segment copiedSegment = new Segment(segment);
            this.program2Segments.add(copiedSegment);
            this.allSegments.add(copiedSegment);
        }
    }

    /**
     * @return The total distance of the track
     */
    public double getDistance() {
        double total = 0.0;

        if (getSegments() == null) {
            throw new NullPointerException("Segments are not assigned yet to this track");
        }

        for (Segment segment : getSegments()) {
            total += segment.getDistance();
        }

        return total;
    }

    /**
     * @return The average speed that the user must achieve
     */
    public double getSpeed() {
        long total_duration = 0;
        double total_distance = 0.0;

        if (getSegments() == null) {
            throw new NullPointerException("Segments are not assigned yet to this track");
        }

        for (Segment segment : getSegments()) {
            total_duration += segment.getDuration();
            total_distance += segment.getDistance();
        }

        return total_distance / total_duration * 3600000;
    }

    /**
     * @return The badge of the track
     */
    public Badge getBadge() {
        if (selectedProgram == 0) {
            return program1Badge;
        }
        else if (selectedProgram == 1) {
            return program2Badge;
        }

        throw new IllegalStateException("selectedProgram is different that 0 and 1");
    }

    /**
     * @return The track ID
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * @return The track Title
     */
    @NonNull
    public String getTitleId() {
        return titleId;
    }

    /**
     * @return The list of segments that compose this track
     */
    public ArrayList<Segment> getSegments() {
        if (selectedProgram == 0) {
            return program1Segments;
        }
        else if (selectedProgram == 1) {
            return program2Segments;
        }

        throw new IllegalStateException("selectedProgram is different that 0 and 1");
    }

    /**
     * Return all the segments for all the programs
     *
     * @return All the segments
     */
    @NonNull
    public ArrayList<Segment> getAllSegments() {
        return allSegments;
    }

    /**
     * @return If the track is available for the user to run
     */
    public Boolean isEnabledForUser() {
        return enabledForUser;
    }

    /**
     * Marks the track available for the user to run
     */
    void setEnabledForUser() {
        this.enabledForUser = true;
    }

    /**
     * @return If the track has been purchased
     */
    public Boolean isPurchased() {
        return purchased;
    }

    /**
     * Marks the track as purchased
     *
     * @param purchased if the track has been purchased and downloaded and is ready to be ran
     */
    public void setAsPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    /**
     * @return The track location name (e.g. Parque El Ocotal).
     */
    @NonNull
    public String getLocationId() {
        return locationId;
    }

    /**
     * @return The Android resource ID for the background color
     */
    @NonNull
    public String getBackgroundResourceId() {
        return backgroundResourceId;
    }

    /**
     * @return The Android resource ID for the decoration map
     */
    @NonNull
    public String getMapResourceId() {
        return mapResourceId;
    }

    /**
     * @return The Android resource ID for the Specification text of the Track
     */
    @NonNull
    public String getSpecsId() {
        return specsId;
    }

    /**
     * @return The advance percentage that the user wins after running the track.
     */
    double getAdvancePercentage() {
        return advancePercentage;
    }

    /**
     * @return The date of the last time the user ran this track.
     */
    @Nullable
    Date getCompletedDate() {
        return completedDate;
    }

    /**
     * @param completedDate The date of the last time the user ran this track.
     */
    void setCompletedDate(@Nullable Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * @return The Track ID of the track that the user needs to run in order to mark this track as available.
     */
    @Nullable
    String getUnlockedBy() {
        return unlockedBy;
    }

    /**
     * A Track can have two different programs.
     *
     * @return The first program title.
     */
    @NonNull
    public String getProgram1TitleId() {
        return program1TitleId;
    }

    /**
     * A Track can have two different programs.
     *
     * @return The second program title.
     */
    @Nullable
    public String getProgram2TitleId() {
        return program2TitleId;
    }

    /**
     * A Track can have two different programs.
     *
     * @return The program that the user will run.
     */
    public int getSelectedProgram() {
        return selectedProgram;
    }

    /**
     * A Track can have two different programs.
     *
     * @param selected_program The program that the user will run.
     */
    public void setSelectedProgram(int selected_program) {
        if (selected_program < 0 || selected_program > 1) throw new IllegalStateException("Selected program must be 0 or 1");

        this.selectedProgram = selected_program;
    }

    /**
     * @return The Product ID in the Google Store
     */
    @NonNull
    public String getSku() {
        return sku;
    }

    /**
     * @return The Total duration of the track
     */
    public long getDuration() {
        if (getSegments() == null) {
            throw new NullPointerException("Segments are not assigned yet to this track");
        }

        long duration = 0;

        for (Segment segment : getSegments()) {
            duration += segment.getDuration();
        }

        return duration;
    }

    /**
     * Get the Badge for program 1
     *
     * @return The badge for program 1
     */
    @NonNull
    public Badge getProgram1Badge() {
        return program1Badge;
    }

    /**
     * Get the Badge for program 2
     *
     * @return The badge for program 2
     */
    @Nullable
    public Badge getProgram2Badge() {
        return program2Badge;
    }

    /**
     * Get the Indoor level
     *
     * @return The indoor level
     */
    IndoorLevel getIndoorLevel() {
        return indoorLevel;
    }

    /**
     * Sets the indoor level
     *
     * @param indoorLevel the indoor level to set
     */
    public void setIndoorLevel(IndoorLevel indoorLevel) {
        if (indoorLevel == null) throw new IllegalStateException("indoorLevel is null for the segment.");

        for (Segment segment : getAllSegments()) {
            segment.setIndoorLevel(indoorLevel);
        }

        this.indoorLevel = indoorLevel;
    }

    /**
     * Determine if the track has been downloaded
     *
     * @return Whether the track has been downloaded
     */
    public Boolean isDownloaded() {
        if (getAllSegments().size() == 0) {
            Log.i(TAG, "Track " + getId() + " has no segments");
            return false;
        }

        for (Segment segment : getAllSegments()) {
            if (!segment.isDownloaded()) {
                Log.i(TAG, String.format("Segment %d is not downloaded", segment.getSequence()));
                return false;
            }

            if (segment.getDownloadedDate() == null) {
                Log.e(TAG, String.format("No downloaded date for segment %d", segment.getSequence()));
                return false;
            }

            if (segment.getDownloadedDate().compareTo(segment.getApiCreatedDate()) < 0) {
                Log.e(TAG, String.format("Segment downloaded on %s. Created in API on %s.",
                        segment.getDownloadedDate(),
                        segment.getApiCreatedDate()));
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if the track has been downloaded
     *
     * @return Whether the track has been downloaded
     */
    public Boolean isPartiallyDownloaded() {
        for (Segment segment : getAllSegments()) {
            if (segment.isDownloaded()) {
                Log.i(TAG, String.format("Segment %d is not downloaded", segment.getSequence()));
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a segment to the track
     *
     * @param segment The segment to add
     */
    void addSegment(Segment segment) {
        if (segment == null) throw new NullPointerException("Segment is null");

        if (indoorLevel != null) {
            segment.setIndoorLevel(indoorLevel);
        }

        if (selectedProgram == 0) {
            program1Segments.add(segment);
        }
        else if (selectedProgram == 1) {
            program2Segments.add(segment);
        }
        else {
            throw new IllegalStateException("selectedProgram is different that 0 and 1");
        }

        /*
        for (Segment s : allSegments) {
            if (s.getSequence() == segment.getSequence()) {
                throw new IllegalStateException("Sequence already exists in this track");
            }
        }
        */

        allSegments.add(segment);
    }

    /**
     * Return a segment based on its sequence
     *
     * @param sequence the sequence of the segment to search
     * @return The segment that corresponds to the sequence
     */
    public Segment getSegment(int sequence) {
        if (selectedProgram == 0) {
            return program1Segments.get(sequence);
        }
        else if (selectedProgram == 1) {
            return program2Segments.get(sequence);
        }
        else {
            throw new IllegalStateException("selectedProgram is different that 0 and 1");
        }
    }

    /**
     * Clear the lists of segments
     */
    void clearSegments() {
        program1Segments.clear();
        program2Segments.clear();
        allSegments.clear();
    }

    @Override
    public String toString() {
        String result;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String completedDate = getCompletedDate() != null ? dateFormat.format(getCompletedDate()) : "";

        result = getId() + ": segments: " + getAllSegments().size() + ", last ran: " + completedDate + ", purchased: " + (isPurchased() ? "yes" : "no") + ", available: " + (isEnabledForUser() ? "yes" : "no") + ", downloaded: " + (isDownloaded() ? "yes" : "no") + "\n";

        return result;
    }

    /**
     * Downloads the videos of the segments
     *
     * @param indoorAppState The indoorAppState variable
     * @param context        The app context
     * @param listener       The listener with events to send
     */
    public void download(IndoorAppState indoorAppState, Context context, SegmentsDownloader.Listener listener) {
        Log.i(TAG, String.format("Downloading %d segments of track %s", getAllSegments().size(), getId()));

        if (getAllSegments().size() == 0) throw new IllegalStateException("No segments in track");

        ArrayList<Segment> list = new ArrayList<>();
        for (Segment segment : getAllSegments()) {
            if (!segment.isDownloaded() || segment.getDownloadedDate() == null || segment.getDownloadedDate().compareTo(segment.getApiCreatedDate()) < 0) {
                list.add(segment);
            }

        }

        if (list.size() == 0) {
            Log.i(TAG, "All the segments have been downloaded already");
            listener.onSuccess();
        }
        else {
            downloader = new SegmentsDownloader(indoorAppState, context, list, listener);
            downloading = true;
            downloader.download();
        }
    }

    /**
     * Cancels a download if it is in progress
     */
    public void cancelDownload() {
        if (downloader != null) {
            downloader.cancel();
        }
        downloading = false;
    }

    /**
     * Tells if a download is in progress
     *
     * @return If a download is in progress
     */
    public boolean isDownloading() {
        return downloading;
    }

    /**
     * Tells that we don't have a download in progress
     */
    public void setNotDownloading() {
        this.downloading = false;
    }

    /**
     * The percentage of the download
     *
     * @return The percentage of the download
     */
    public int getPercentDownloaded() {
        return percentDownloaded;
    }

    /**
     * Sets the percentage of the current download
     *
     * @param percentDownloaded the percentage of the current download
     */
    public void setPercentDownloaded(int percentDownloaded) {
        this.percentDownloaded = percentDownloaded;
    }

    /**
     * Gets the description resource of program 1
     *
     * @return The description resource of program 1
     */
    @NonNull
    public String getProgram1DescriptionId() {
        return program1DescriptionId;
    }

    /**
     * Gets the description resource of program 2
     *
     * @return The description resource of program 2
     */
    @Nullable
    public String getProgram2DescriptionId() {
        return program2DescriptionId;
    }

    /**
     * Gets the sequence of this track
     *
     * @return The sequence of this track
     */
    int getSequence() {
        return sequence;
    }

    /**
     * Gets the required effort of this track, based on the indoor level
     *
     * @return The required effort of this track
     */
    public int getEffortResource() {
        Effort effort;

        Log.i(TAG, "Getting effort for program: " + selectedProgram + " and indoorLevel: " + indoorLevel.toString());

        if (selectedProgram == 0) {
            switch (indoorLevel) {
                case BEGINNER:
                    effort = effort1Beginner;
                    break;
                case COMPETITOR:
                    effort = effort1Competitor;
                    break;
                case ATHLETE:
                    effort = effort1Athlete;
                    break;
                case RUNNER:
                    effort = effort1Runner;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }
        else if (selectedProgram == 1) {
            switch (indoorLevel) {
                case BEGINNER:
                    effort = effort2Beginner;
                    break;
                case COMPETITOR:
                    effort = effort2Competitor;
                    break;
                case ATHLETE:
                    effort = effort2Athlete;
                    break;
                case RUNNER:
                    effort = effort2Runner;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }
        else {
            throw new IllegalStateException("Invalid Program: " + selectedProgram);
        }

        if (effort == null) return 0;

        switch (effort) {
            case EASY:
                return R.string.effort_easy;
            case INTERMEDIATE:
                return R.string.effort_intermediate;
            case DIFFICULT:
                return R.string.effort_difficult;
        }

        Log.e(TAG, "Invalid effort indoorLevel");
        return 0;
    }

    /**
     * Gets the required effort of this track, based on the indoor level
     *
     * @return The required effort of this track
     */
    public double getCalories(double weight) {
        double factor = 0.0;

        if (selectedProgram < 0 || selectedProgram > 1) throw new IllegalStateException("You haven't set a valid selectedProgram for this track.");
        if (weight == 0) throw new IllegalStateException("Weight cannot be 0");

        //Log.i(TAG, "Getting calories for program: " + selectedProgram + " and indoorLevel: " + indoorLevel.toString());

        if (selectedProgram == 0 && speedLevel == SpeedLevel.MODERATED) {
            switch (indoorLevel) {
                case BEGINNER:
                    factor = caloriesFactor1BeginnerModerated;
                    break;
                case COMPETITOR:
                    factor = caloriesFactor1CompetitorModerated;
                    break;
                case ATHLETE:
                    factor = caloriesFactor1AthleteModerated;
                    break;
                case RUNNER:
                    factor = caloriesFactor1RunnerModerated;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }
        else if (selectedProgram == 1 && speedLevel == SpeedLevel.MODERATED) {
            switch (indoorLevel) {
                case BEGINNER:
                    factor = caloriesFactor2BeginnerModerated;
                    break;
                case COMPETITOR:
                    factor = caloriesFactor2CompetitorModerated;
                    break;
                case ATHLETE:
                    factor = caloriesFactor2AthleteModerated;
                    break;
                case RUNNER:
                    factor = caloriesFactor2RunnerModerated;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }
        else if (selectedProgram == 0 && speedLevel == SpeedLevel.INTENSE) {
            switch (indoorLevel) {
                case BEGINNER:
                    factor = caloriesFactor1BeginnerIntense;
                    break;
                case COMPETITOR:
                    factor = caloriesFactor1CompetitorIntense;
                    break;
                case ATHLETE:
                    factor = caloriesFactor1AthleteIntense;
                    break;
                case RUNNER:
                    factor = caloriesFactor1RunnerIntense;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }
        else if (selectedProgram == 1 && speedLevel == SpeedLevel.INTENSE) {
            switch (indoorLevel) {
                case BEGINNER:
                    factor = caloriesFactor2BeginnerIntense;
                    break;
                case COMPETITOR:
                    factor = caloriesFactor2CompetitorIntense;
                    break;
                case ATHLETE:
                    factor = caloriesFactor2AthleteIntense;
                    break;
                case RUNNER:
                    factor = caloriesFactor2RunnerIntense;
                    break;
                default:
                    throw new IllegalStateException("Invalid indoorLevel");
            }
        }

        return weight * factor;
    }

    /**
     * Returns the selected speed level
     *
     * @return The selected speed level
     */
    SpeedLevel getSpeedLevel() {
        return speedLevel;
    }

    /**
     * Sets the speed level
     *
     * @param speedLevel The speed level
     */
    public void setSpeedLevel(@NonNull SpeedLevel speedLevel) {
        for (Segment segment : getAllSegments()) {
            segment.setSpeedLevel(speedLevel);
        }

        this.speedLevel = speedLevel;
    }

    /**
     * The effort enum
     */
    enum Effort {
        EASY,
        INTERMEDIATE,
        DIFFICULT
    }
}
