package com.runin.runinapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.data.database.CompletedIndoorTrack;
import com.runin.runinapp.data.database.CompletedIndoorTrackOperationsDB;
import com.runin.runinapp.utils.DateUtils;
import com.runin.runinapp.utils.RuninApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Samuel Kobelkowsky on 7/24/17.
 * Stores the Indoor Application State in a file.
 * <p>
 * The single instance of this class can be obtained in an activity as follows:
 * <p>
 * <code>IndoorAppState appState = ((MyApplication) this.getApplication()).getIndoorAppState();</code>
 */
public class IndoorAppState {
    // region Properties

    /**
     * The classname for Debug purposes.
     */
    private static final String TAG = IndoorAppState.class.getSimpleName();

    /**
     * The filename for storing the purchased products
     */
    private static final String PurchasedProductsFilename = "purchased_products";

    /**
     * The filename for storing the downloaded segments
     */
    private static final String DownloadedSegmentsFilename = "downloaded_segments";

    /**
     * A list with all the indoor tracks to run
     */
    @NonNull
    private final ArrayList<Track> indoorTracks;

    /**
     * A list with all the purchased products from store
     */
    @NonNull
    private final ArrayList<String> purchasedProducts;

    /**
     * A list with all the badges available and won
     */
    @NonNull
    private final ArrayList<Badge> badges;

    /**
     * A list with all the indoor levels
     */
    @NonNull
    private final ArrayList<IndoorLevel> indoorLevels;

    /**
     * A list with all the speed levels
     */
    @NonNull
    private final ArrayList<SpeedLevel> speedLevels;

    /**
     * The application context.
     */
    @NonNull
    private final Context context;
    /**
     * A list with all the finished tracks
     */
    @NonNull
    private final ArrayList<Track> finishedTracks;

    /**
     * The currently selected track
     */
    @Nullable
    private Track selectedTrack;

    // endregion

    //region Basic Operations. Do not change.

    /**
     * Constructor; only one instance is recommended to be used, and that goes in MyApplication.java
     *
     * @param context The application context
     */
    public IndoorAppState(@NonNull Context context) {
        this.context = context;

        indoorTracks = new ArrayList<>();
        finishedTracks = new ArrayList<>();
        purchasedProducts = new ArrayList<>();
        badges = new ArrayList<>();
        indoorLevels = new ArrayList<>();
        speedLevels = new ArrayList<>();

        additionalSetup();
    }

    /**
     * Populate the badges, indoor levels and indoor tracks lists
     */
    private void additionalSetup() {

        Badge newbie = new Badge(Badge.Level.NEWBIE, false);
        Badge endurance = new Badge(Badge.Level.ENDURANCE, false);
        Badge boost = new Badge(Badge.Level.BOOST, false);
        Badge fitness = new Badge(Badge.Level.FITNESS, false);
        Badge muscle = new Badge(Badge.Level.MUSCLE, false);
        Badge resistance = new Badge(Badge.Level.RESISTANCE, false);
        Badge pace = new Badge(Badge.Level.PACE, false);
        Badge speed = new Badge(Badge.Level.SPEED, false);
        Badge strength = new Badge(Badge.Level.STRENGTH, false);
        Badge power = new Badge(Badge.Level.POWER, false);

        badges.clear();
        badges.add(newbie);
        badges.add(endurance);
        badges.add(boost);
        badges.add(fitness);
        badges.add(muscle);
        badges.add(resistance);
        badges.add(pace);
        badges.add(speed);
        badges.add(strength);
        badges.add(power);

        // If any badge has been won, change its status
        for (Badge badge : badges) badge.getStatus(context);

        indoorLevels.clear();
        indoorLevels.add(IndoorLevel.BEGINNER);
        indoorLevels.add(IndoorLevel.COMPETITOR);
        indoorLevels.add(IndoorLevel.ATHLETE);
        indoorLevels.add(IndoorLevel.RUNNER);

        speedLevels.clear();
        speedLevels.add(SpeedLevel.MODERATED);
        speedLevels.add(SpeedLevel.INTENSE);

        // region setupTracks
        Track demo = new Track(0,
                BuildConfig.DEBUG ? Track.DEMO_DEVEL_ID : Track.DEMO_ID,
                "demo",
                "ubicacion_demo",
                "bg_levels_demo",
                "pista_boisboulonge",
                "especificacion_demo",
                0.25,
                "",
                "demo_program_title",
                null,
                newbie,
                null,
                "demo_program_description",
                null,
                Track.Effort.EASY,
                null,
                Track.Effort.EASY,
                null,
                Track.Effort.EASY,
                null,
                Track.Effort.EASY,
                null,
                "",
                1.443884,
                1.443884,
                0,
                0,
                2.166606,
                2.166606,
                0,
                0,
                2.537438,
                2.537438,
                0,
                0,
                2.911504,
                2.911504,
                0,
                0
        );

        Track higher_and_faster = new Track(1,
                Track.HIGHER_AND_FASTER_ID,
                "higher_and_faster",
                "ubicacion_higher",
                "bg_levels_higher",
                "pista_ocotal",
                "especificacion_higher",
                0.125,
                BuildConfig.DEBUG ? Track.DEMO_DEVEL_ID : Track.DEMO_ID,
                "resistance",
                "power",
                resistance,
                power,
                "higher_and_faster_program1_description",
                "higher_and_faster_program2_description",
                Track.Effort.EASY,
                Track.Effort.EASY,
                Track.Effort.INTERMEDIATE,
                Track.Effort.INTERMEDIATE,
                Track.Effort.INTERMEDIATE,
                Track.Effort.INTERMEDIATE,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                "p_higher",
                2.980288933,
                3.215028933,
                2.5989178,
                2.8780978,
                4.504546933,
                4.9329456,
                3.5482658,
                4.1313978,
                5.9575296,
                6.387886267,
                5.0132458,
                5.4069798,
                7.1259936,
                7.321610267,
                6.2241038,
                6.4801838

        );

        Track harder_and_stronger = new Track(2,
                Track.HARDER_AND_STRONGER_ID,
                "harder_and_stronger",
                "ubicacion_harder",
                "bg_levels_stronger",
                "pista_gandhi",
                "especificacion_harder",
                0.125,
                Track.HIGHER_AND_FASTER_ID,
                "fitness",
                "speed",
                fitness,
                speed,
                "harder_and_stronger_program1_description",
                "harder_and_stronger_program2_description",
                Track.Effort.INTERMEDIATE,
                Track.Effort.INTERMEDIATE,
                Track.Effort.INTERMEDIATE,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                "p_harder",
                2.6258448,
                3.0233628,
                2.8448736,
                3.2598156,
                3.67132,
                4.1832088,
                7.1112800,
                4.4562836,
                4.9384908,
                4.9384908,
                4.9864616,
                4.9864616,
                6.0048748,
                6.1942068,
                6.0306256,
                6.1976276
        );

        Track further_and_longer = new Track(3,
                Track.FURTHER_AND_LONGER_ID,
                "further_and_longer",
                "ubicacion_further",
                "bg_levels_longer",
                "pista_tlalpan",
                "especificacion_further",
                0.125,
                Track.HARDER_AND_STRONGER_ID,
                "boost",
                "strength",
                boost,
                strength,
                "further_and_longer_program1_description",
                "further_and_longer_program2_description",
                Track.Effort.INTERMEDIATE,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                Track.Effort.DIFFICULT,
                "p_further",
                2.8502296,
                3.1771936,
                3.5597590,
                3.9566830,
                3.9940676,
                4.6494476,
                4.7869550,
                5.4106330,
                5.4915196,
                5.9501536,
                6.1765190,
                6.5716390,
                6.8741976,
                7.1547416,
                7.3714270,
                7.6371430
        );
        // endregion

        // Demo is always available and no need to purchase it.
        demo.setEnabledForUser();
        demo.setAsPurchased(true);

        indoorTracks.clear();
        indoorTracks.add(demo);
        indoorTracks.add(higher_and_faster);
        indoorTracks.add(harder_and_stronger);
        indoorTracks.add(further_and_longer);

        // Sort the tracks by sequence number
        Collections.sort(indoorTracks, new Comparator<Track>() {
            @Override
            public int compare(Track track1, Track track2) {
                return track1.getSequence() - track2.getSequence();
            }
        });

        getFinishedTracks();
        loadPurchasedProducts();

        // Checks if a new track has been made available for purchase
        updateAvailabilityOfTracks();
    }

    /**
     * Populate the list of finished tracks
     */
    private void getFinishedTracks() {
        finishedTracks.clear();

        CompletedIndoorTrackOperationsDB completedIndoorTrackOperationsDB = new CompletedIndoorTrackOperationsDB();
        List<CompletedIndoorTrack> completedIndoorTracks = completedIndoorTrackOperationsDB.getCompletedTracks(context);

        for (CompletedIndoorTrack completedIndoorTrack : completedIndoorTracks) {
            for (Track track : indoorTracks) {
                if (track.getId().equals(completedIndoorTrack.getId())) {
                    // Deep copy of the track because we'll be modifying internal values
                    Track finishedTrack = new Track(track);
                    finishedTrack.setCompletedDate(completedIndoorTrack.getCompletedDate());
                    finishedTrack.setIndoorLevel(completedIndoorTrack.getIndoorLevel());
                    finishedTrack.setSelectedProgram(completedIndoorTrack.getSelectedProgram());
                    finishedTrack.setSpeedLevel(completedIndoorTrack.getSpeedLevel());

                    Log.i(TAG, String.format("Completed Track %s on %s", finishedTrack.getId(), finishedTrack.getCompletedDate()));
                    finishedTracks.add(finishedTrack);
                }
            }
        }
    }

    /**
     * Save the "won" status of the badges
     */
    private void saveBadgeStatus() {
        for (Badge badge : badges) {
            badge.saveStatus(context);
        }
    }

    /**
     * Populate the list of purchased products
     */
    private void loadPurchasedProducts() {
        Log.i(TAG, "Loading purchased products");
        try {
            FileInputStream fis = context.openFileInput(PurchasedProductsFilename);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();

            if (readObject != null && readObject instanceof ArrayList<?>) {
                purchasedProducts.clear();

                ArrayList<?> list = (ArrayList<?>) readObject;

                for (Object product : list) {
                    if (product instanceof String) {
                        purchasedProducts.add((String) product);
                    }
                    else {
                        Log.e(TAG, "Cannot cast String");
                    }
                }
            }
            else {
                Log.e(TAG, "Cannot cast ArrayList of products");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "IndoorAppState file doesn't exist");
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found", e);
        }

        Log.i(TAG, "Found: " + purchasedProducts.size() + " products");
    }

    /**
     * Save list of purchased products.
     */
    private void savePurchasedProducts() {
        Log.i(TAG, "Save bought products");

        try {
            FileOutputStream fos = context.openFileOutput(PurchasedProductsFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(purchasedProducts);
            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't save bought products", e);
        }
    }

    /**
     * Save objects to persistent memory.
     */
    public void save() {
        saveBadgeStatus();

        savePurchasedProducts();
    }

    //endregion

    //region Methods

    /**
     * Gets the selected Indoor track.
     *
     * @return the selected track
     */
    @NonNull
    public Track getSelectedTrack() {
        if (selectedTrack == null) throw new IllegalStateException("Selected track is null");

        return selectedTrack;
    }

    /**
     * Sets the selected Indoor track.
     *
     * @param selectedTrack the selected track
     */
    public void setSelectedTrack(@Nullable Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    /**
     * Gets the indoor tracks.
     *
     * @return the indoor tracks
     */
    @NonNull
    public ArrayList<Track> getIndoorTracks() {
        return indoorTracks;
    }

    /**
     * Gets the purchased products SKU.
     *
     * @return the purchased products SKU
     */
    @NonNull
    public ArrayList<String> getPurchasedProducts() {
        return purchasedProducts;
    }

    /**
     * Return the indoor levels
     */
    @NonNull
    public ArrayList<IndoorLevel> getIndoorLevels() {
        return indoorLevels;
    }

    /**
     * When a track is finished, mark the following as available
     */
    private void updateAvailabilityOfTracks() {
        for (Track indoorTrack : indoorTracks) {
            for (Track finishedTrack : finishedTracks) {
                String unlockedBy = indoorTrack.getUnlockedBy();
                if (unlockedBy == null || unlockedBy.equals(finishedTrack.getId())) {
                    indoorTrack.setEnabledForUser();
                    break;
                }
            }
        }
    }

    /**
     * Search a Track based on its id
     *
     * @param storeId The track id to search
     * @return The result, or null if not found
     */
    private Track getTrackByStoreId(String storeId) {
        for (Track track : this.indoorTracks) {
            if (track.getId().equals(storeId)) return track;
        }

        return null;
    }

    /**
     * Setup of indoor segments for the Selected Track. This must be populated once a Runner Level and Program has been selected.
     */
    public void setupIndoorSegments(final RuninApi.ResultResponseInterface resultInterface) {
        RuninApi runinApi = new RuninApi(context);

        runinApi.fetchSegments(new RuninApi.NetworkJSONArrayResultInterface() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    decodeSegmentsJsonObject(response);

                    // Save file to cache file
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(IndoorAppState.this.context.openFileOutput(DownloadedSegmentsFilename, Context.MODE_PRIVATE));
                    outputStreamWriter.write(response.toString());
                    outputStreamWriter.close();

                    resultInterface.onNetworkConnectSuccess();
                } catch (JSONException ex) {
                    Log.e(TAG, "Error decoding JSONArray", ex);
                    resultInterface.onNetworkConnectFailure(-2);
                } catch (ParseException ex) {
                    Log.e(TAG, "Error parsing string value", ex);
                    resultInterface.onNetworkConnectFailure(-2);
                } catch (NullPointerException ex) {
                    Log.e(TAG, "Null value", ex);
                    resultInterface.onNetworkConnectFailure(-2);
                } catch (IOException e) {
                    resultInterface.onNetworkConnectFailure(-2);
                    Log.e(TAG, "Unknown error: ", e);
                    Log.e(TAG, "Response: " + response);
                }
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e(TAG, "Failure getting segments");
                resultInterface.onNetworkConnectFailure(statusCode);
            }
        });
    }

    /**
     * Tells whether a file with a copy of the segments JSON returned by the API exists.
     *
     * @return whether a file with a copy of the segments JSON returned by the API exists.
     */
    public boolean segmentsFileExists() {
        File file = new File(context.getFilesDir(), DownloadedSegmentsFilename);

        Log.i(TAG, String.format("File %s exists: %s, File size: %d", DownloadedSegmentsFilename, file.exists(), file.length()));

        return file.exists() && file.length() > 0;
    }

    /**
     * Load the segments from a saved file, used when no network connection available
     */
    public void loadSegmentsFromFile() throws Exception {
        String ret;
        InputStream inputStream = context.openFileInput(DownloadedSegmentsFilename);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString;
        StringBuilder stringBuilder = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null) {
            stringBuilder.append(receiveString);
        }

        inputStream.close();
        ret = stringBuilder.toString();
        JSONArray jsonArray = new JSONArray(ret);
        decodeSegmentsJsonObject(jsonArray);
    }

    /**
     * Decode the segments from a JSON array
     *
     * @param response The JSON array containing the info of the Segments
     */
    private void decodeSegmentsJsonObject(JSONArray response) throws JSONException, ParseException {
        for (int i = 0; i < response.length(); i++) {
            JSONObject object = response.getJSONObject(i);

            Track track = getTrackByStoreId(object.getString("storeID"));
            if (track == null) {
                Log.w(TAG, "Didn't find a track with id: " + object.getString("storeID"));
                continue;
            }

            track.clearSegments();

            JSONArray segments = object.getJSONArray("segmentos");

            for (int j = 0; j < segments.length(); j++) {
                JSONObject segment_object = segments.getJSONObject(j);

                DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date duration_date = formatter.parse(segment_object.getString("duracion"));
                Date day_zero = formatter.parse("00:00:00");

                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.", Locale.getDefault());
                Date uploaded_date = formatter2.parse(segment_object.getString("cargado"));

                long duration = (duration_date.getTime() - day_zero.getTime());

                Segment.Kind kind = Segment.Kind.fromInt(segment_object.getInt("tipo"));
                if (kind == null) kind = Segment.Kind.WARM_UP;

                double slope = segment_object.getDouble("pendiente");
                String videoURI = segment_object.getString("mediaURL");

                int program = segment_object.getInt("programa");
                long bytes = segment_object.getLong("bytes");

                double speedBeginner1 = segment_object.getDouble("velocidadPrincipiante1");
                double speedCompetitor1 = segment_object.getDouble("velocidadCompetidor1");
                double speedAthlete1 = segment_object.getDouble("velocidadAtleta1");
                double speedRunner1 = segment_object.getDouble("velocidadCorredor1");

                double speedBeginner2 = segment_object.getDouble("velocidadPrincipiante2");
                double speedCompetitor2 = segment_object.getDouble("velocidadCompetidor2");
                double speedAthlete2 = segment_object.getDouble("velocidadAtleta2");
                double speedRunner2 = segment_object.getDouble("velocidadCorredor2");

                File file = new File(context.getExternalFilesDir("videos"), track.getId() + "." + String.valueOf(program) + "." + String.valueOf(j));
                String filename = file.getAbsolutePath();
                Uri uri = FileProvider.getUriForFile(context, "com.runin.runinapp.file_provider", file);

                Segment segment = new Segment(duration, kind, slope, j);
                segment.setSpeedBeginner1(speedBeginner1);
                segment.setSpeedCompetitor1(speedCompetitor1);
                segment.setSpeedAthlete1(speedAthlete1);
                segment.setSpeedRunner1(speedRunner1);
                segment.setSpeedBeginner2(speedBeginner2);
                segment.setSpeedCompetitor2(speedCompetitor2);
                segment.setSpeedAthlete2(speedAthlete2);
                segment.setSpeedRunner2(speedRunner2);
                segment.setFilename(filename);
                segment.setVideoRemoteUri(videoURI);
                segment.setByteLength(bytes);
                segment.setApiCreatedDate(uploaded_date);
                segment.setVideoUri(uri.toString());

                track.setSelectedProgram(program);
                track.addSegment(segment);
            }
        }
    }

    /**
     * Marks a track as completed.
     * Update the total, today and last training statistics with the selected track. Also update the badges
     */
    public void markTrackCompleted() {
        if (selectedTrack == null) {
            throw new IllegalStateException("Selected Track is null");
        }

        // Create a copy of the track we ran.
        addFinishedTrack(selectedTrack);

        // Update badge
        setBadgeWon(selectedTrack.getBadge().getLevel());

        // Checks if a new track has been made available for purchase
        updateAvailabilityOfTracks();
    }

    /**
     * The distance ran today
     *
     * @return The distance ran today
     */
    public Double getDistanceToday() {
        CompletedIndoorTrackOperationsDB completedIndoorTrackOperationsDB = new CompletedIndoorTrackOperationsDB();
        return completedIndoorTrackOperationsDB.getDistanceToday(context);
    }

    /**
     * The calories burned today
     *
     * @return The calories burned today
     */
    public Double getCaloriesToday(double weight) {

        Double result = 0.0;
        for (Track track : finishedTracks) {
            try {
                if (DateUtils.dateOnly(track.getCompletedDate()).equals(DateUtils.dateOnly(new Date()))) {
                    result += track.getCalories(weight);
                }
            } catch (ParseException exception) {
                Log.e(TAG, "Invalid date format", exception);
            }
        }
        return result;
    }

    /**
     * Registers a purchased product.
     *
     * @param product the product SKU
     */
    public void addPurchasedProduct(String product) {
        if (!purchasedProducts.contains(product)) {
            purchasedProducts.add(product);
        }
    }

    /**
     * Gets the user badges.
     *
     * @return the user badges
     */
    @NonNull
    public ArrayList<Badge> getBadges() {
        return badges;
    }

    /**
     * Sets a badge as won.
     *
     * @param level the level of the badge
     */
    private void setBadgeWon(Badge.Level level) {
        boolean found = false;
        for (Badge badge : badges) {
            if (badge.getLevel() == level) {
                badge.setCompleted();
                found = true;
            }
        }

        if (!found) {
            badges.add(new Badge(level, true));
        }
    }

    /**
     * Is badge won boolean.
     *
     * @param level the level
     * @return the boolean
     */
    public boolean isBadgeWon(Badge.Level level) {
        for (Badge badge : badges) {
            if (badge.getLevel() == level && badge.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain the badge icon depending if the user has obtained it
     *
     * @param level the badge level
     * @return the resource id
     */
    public int getMyBadgeIcon(Badge.Level level) {
        for (Badge badge : badges) {
            if (badge.getLevel() == level) {
                if (badge.isCompleted()) {
                    return badge.getIcon();
                }
                else {
                    return badge.getGrayIcon();
                }
            }
        }

        Badge badge = new Badge(level, false);
        return badge.getGrayIcon();
    }

    /**
     * Registers a track as finished
     *
     * @param track the finished track
     */
    private void addFinishedTrack(Track track) {
        // Create a deep copy of the track
        Track ran_track = new Track(track);
        Date completedDate = new Date();
        ran_track.setCompletedDate(completedDate);

        finishedTracks.add(ran_track);

        String id = ran_track.getId();
        IndoorLevel level = ran_track.getIndoorLevel();
        int program = ran_track.getSelectedProgram();
        SpeedLevel speedLevel = ran_track.getSpeedLevel();
        double distanceRan = ran_track.getDistance();

        CompletedIndoorTrack completedIndoorTrack = new CompletedIndoorTrack(id, completedDate, level, program, speedLevel, distanceRan);

        // Save into DB
        CompletedIndoorTrackOperationsDB completedIndoorTrackOperationsDB = new CompletedIndoorTrackOperationsDB();
        completedIndoorTrackOperationsDB.add(completedIndoorTrack, context);
    }

    /**
     * Returns the total distance ran by the user ever
     *
     * @return the total distance
     */
    public Double getDistanceTotal() {
        CompletedIndoorTrackOperationsDB completedIndoorTrackOperationsDB = new CompletedIndoorTrackOperationsDB();
        return completedIndoorTrackOperationsDB.getDistanceTotal(context);
    }

    /**
     * Return the distance ran during the last training
     *
     * @return the distance
     */
    public Double getDistanceLastTraining() {
        CompletedIndoorTrackOperationsDB completedIndoorTrackOperationsDB = new CompletedIndoorTrackOperationsDB();
        return completedIndoorTrackOperationsDB.getDistanceLastTrainig(context);
    }

    /**
     * Returns the total number of calories burned by the user
     *
     * @return the calories
     */
    public Double getCaloriesTotal(double weight) {
        Double caloriesTotal = 0.0;

        for (Track track : finishedTracks) {
            caloriesTotal += track.getCalories(weight);
        }

        return caloriesTotal;
    }

    /**
     * Return the calories burned during the last training
     *
     * @return the calories
     */
    public Double getCaloriesLastTraining(double weight) {
        if (finishedTracks.size() == 0) return 0.0;

        return finishedTracks.get(finishedTracks.size() - 1).getCalories(weight);
    }

    /**
     * Returns the percentage of advance for the program
     *
     * @return the percentage as a fraction of 1.
     */
    public Double getPercentageAdvanceIndoor() {
        Double advance = 0.0;
        ArrayList<Track> firstTimeRan = new ArrayList<>();

        for (Track track : finishedTracks) {
            boolean found = false;

            for (Track first : firstTimeRan) {
                if (track.getId().equals(first.getId()) && track.getSelectedProgram() == first.getSelectedProgram()) {
                    found = true;
                }
            }

            if (!found) {
                firstTimeRan.add(track);
                advance += track.getAdvancePercentage();
            }
        }
        return advance;
    }

    /**
     * Return the saved indoor level.
     *
     * @return the level
     */
    public IndoorLevel getIndoorLevel() {
        String indoorLevelPreference = App.sharedPreferencesIndoorLevel;
        String preferencesFile = App.sharedPreferencesFile;

        SharedPreferences sharedPref = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        int level = sharedPref.getInt(indoorLevelPreference, -1);

        for (IndoorLevel indoorLevel : indoorLevels) {
            if (indoorLevel.getValue() == level) {
                return indoorLevel;
            }
        }

        return null;
    }

    /**
     * Saves the selected indoor level
     *
     * @param level the level
     */
    public void setIndoorLevel(@NonNull IndoorLevel level) {
        String indoorLevelPreference = App.sharedPreferencesIndoorLevel;
        String preferencesFile = App.sharedPreferencesFile;

        SharedPreferences sharedPref = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(indoorLevelPreference, level.getValue());
        editor.apply();
    }

    /**
     * Return the saved speed level.
     *
     * @return the level
     */
    public @Nullable
    SpeedLevel getSpeedLevel() {
        String indoorSpeedLevelPreference = App.sharedPreferencesIndoorSpeedLevel;
        String preferencesFile = App.sharedPreferencesFile;

        SharedPreferences sharedPref = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        int level = sharedPref.getInt(indoorSpeedLevelPreference, -1);

        for (SpeedLevel speedLevel : speedLevels) {
            if (speedLevel.getValue() == level) {
                return speedLevel;
            }
        }

        return null;
    }

    /**
     * Saves the selected indoor level
     *
     * @param level the level
     */
    public void setSpeedLevel(@NonNull SpeedLevel level) {
        String indoorSpeedLevelPreference = App.sharedPreferencesIndoorSpeedLevel;
        String preferencesFile = App.sharedPreferencesFile;

        SharedPreferences sharedPref = context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(indoorSpeedLevelPreference, level.getValue());
        editor.apply();
    }

    /**
     * @return String representation of the class.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // Print Selected Track
        if (selectedTrack != null) {
            result.append("SelectedTrack: ").append(selectedTrack.toString());
        }
        else {
            result.append("SelectedTrack is null\n");
        }

        // Print Indoor Tracks
        for (Track track : indoorTracks) {
            result.append("  ").append(track.toString());
        }

        // Finished tracks
        for (Track track : finishedTracks) {
            result.append("  ").append(track.toString());
        }

        // Purchased products
        result.append(purchasedProducts.size()).append(" PurchasedProducts\n");
        for (String product : purchasedProducts) {
            result.append("    ").append(product).append("\n");
        }

        // Badges
        result.append(badges.size()).append(" Badges\n");
        for (Badge badge : badges) {
            result.append("    ").append(context.getString(badge.getTitle())).append(" ").append(badge.isCompleted() ? "obtained" : "").append("\n");
        }

        return result.toString();
    }
    // endregion
}
