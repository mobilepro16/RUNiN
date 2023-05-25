package com.runin.runinapp.indoor;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.TrackRectAdapter;
import com.runin.runinapp.data.Badge;
import com.runin.runinapp.data.IndoorAppState;
import com.runin.runinapp.data.Track;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.RuninApi.ResultResponseInterface;
import com.runin.runinapp.utils.SegmentsDownloader;
import com.runin.runinapp.utils.Utils;
import com.runin.runinapp.utils.iabHelper.IabHelper;
import com.runin.runinapp.utils.iabHelper.IabHelper.IabAsyncInProgressException;
import com.runin.runinapp.utils.iabHelper.IabResult;
import com.runin.runinapp.utils.iabHelper.Inventory;
import com.runin.runinapp.utils.iabHelper.Purchase;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Who is thoma?
 * Created by thoma on 06/11/2016.
 */
public class SelectTrackFragment extends Fragment {

    private static final String TAG = SelectTrackFragment.class.getSimpleName();

    /**
     * Interface for the download of segments catalog
     */
    private ResultResponseInterface segmentsCatalogDownloadInterface;

    /**
     * Listener for the product catalog from the store
     */
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    /**
     * The adapter for the tracks
     */
    private TrackRectAdapter trackRectAdapter;

    /**
     * The object that processes the in app purchase
     */
    private IabHelper mIabHelper;

    /**
     * This fragment view
     */
    private View view;

    /**
     * False if can't access Google Play Store
     */
    private boolean billingAvailable = false;

    /**
     * Listener that implements the purchase process
     */
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    /**
     * Listener that implements the setup of the in app purchases
     */
    private IabHelper.OnIabSetupFinishedListener mSetupFinishedListener;

    /**
     * Global app state
     */
    private IndoorAppState indoorAppState;

    /**
     * Receiver for changes in network state
     */
    private BroadcastReceiver networkStateReceiver;

    /**
     * Android doesn't provide a method to check if the networkStateReceiver is registered
     */
    private boolean isNetworkStateReceiverRegistered = false;

    /**
     * Tells if the store has already been initialized
     */
    private boolean storeInitialized = false;

    /**
     * The application context
     */
    private Context context;

    /**
     * Used to avoid repeating of setup of segments
     */
    private boolean segmentsSetupSuccess = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the application context
        this.context = getContext();

        // Gets the application state
        if (getActivity() != null) {
            indoorAppState = ((App) this.getActivity().getApplication()).getIndoorAppState();
        }

        // Google Play Store Key for purchases
        String base64EncodedPublicKey = getString(R.string.base64EncodedPublicKey);

        setupSegmentsCatalogDownloadInterface();
        setupSegmentsDownloadInterface();
        setupNetworkStateChangeReceiver();
        setupGotInventoryListener();
        setupPurchaseFinishedListener();

        // Initializes the store
        mIabHelper = new IabHelper(context, base64EncodedPublicKey);
        mIabHelper.enableDebugLogging(true);

        // The actual initialization starts when called from onCreateView
    }

    /**
     * Interface used to download the segments catalog. Not to download each segment.
     */
    private void setupSegmentsCatalogDownloadInterface() {
        segmentsCatalogDownloadInterface = new ResultResponseInterface() {

            // The network connection was successful and the result has been parsed (in IndoorAppState.java), just do some UI stuff here, and other unrelated things
            @Override
            public void onNetworkConnectSuccess() {
                segmentsSetupSuccess = true;

                Log.i(TAG, "We have setup the segments");

                // Initialize the store in order to obtain the purchased products
                if (!storeInitialized) mIabHelper.startSetup(mSetupFinishedListener);
            }

            // The network connection was unsuccessful, try to load the segments from a cached file.
            @Override
            public void onNetworkConnectFailure(int statusCode) {
                Log.e(TAG, "We didn't obtain the segments. Trying with stored file");

                if (indoorAppState.segmentsFileExists()) {
                    try {
                        // We don't initialize the store since there is no connection.
                        indoorAppState.loadSegmentsFromFile();
                        notifyDataSetChanged();
                        return;
                    } catch (Exception ex) {
                        Log.e(TAG, "Error reading segments file: ", ex);
                    }
                }

                // We still haven't obtained the list of segments from the network. Tracks download interface is elsewhere.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // TODO: Process the status code and give a better error

                int message;
                switch (statusCode) {
                    case -2:
                        message = R.string.error_decoding_data;
                        break;
                    case -1:
                        message = R.string.server_not_responding;
                        break;
                    default:
                        message = R.string.network_make_sure_is_ok;
                        break;
                }

                builder.setTitle(R.string.network_error).setMessage(message).setPositiveButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();

                // As we might not have an appropriate network, wait until we have one and retry.
                registerForNetworkChange();
            }
        };
    }

    /**
     * Interface used when initializing the store.
     */
    private void setupSegmentsDownloadInterface() {
        mSetupFinishedListener = new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                storeInitialized = true;

                // Checks that we can access the store
                billingAvailable = result.isSuccess();
                if (!billingAvailable) {
                    notifyDataSetChanged();
                    Log.e(TAG, "InAppPurchase isn't available");
                    return;
                }

                // Maybe another purchase process has finished while we're at it
                if (mIabHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                try {
                    mIabHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabAsyncInProgressException e) {
                    Log.e(TAG, "Error querying inventory. Another async operation in progress.");
                }
            }
        };
    }

    /**
     * Checks if network is connected
     *
     * @return if network is connected
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * The network status has been changed. We retry to download the segments file.
     */
    private void setupNetworkStateChangeReceiver() {
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                if (manager == null) {
                    Log.e(TAG, "Cannot create ConnectivityManager");
                    return;
                }

                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.w(TAG, "Network became available");

                    // Try again to fetch the segments
                    if (!segmentsSetupSuccess) {
                        indoorAppState.setupIndoorSegments(segmentsCatalogDownloadInterface);
                    } else {
                        Log.w(TAG, "Segments already set up");
                    }
                } else {
                    Log.w(TAG, "Network not available");
                }
            }
        };
    }

    /**
     * Interface used after retrieving the inventory from store
     */
    private void setupGotInventoryListener() {
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                // Maybe another purchase process has finished while we're at it
                if (mIabHelper == null) return;

                if (result.isFailure()) {
                    Log.e(TAG, "Failed to get Inventory: " + result);
                    return;
                }

                for (Track track : indoorAppState.getIndoorTracks()) {
                    if (track.getSku().equals("")) continue;

                    if (inv.hasPurchase(track.getSku())) {
                        setTrackAsPurchased(track);
                    } else {
                        if (indoorAppState.getPurchasedProducts().contains(track.getSku())) {
                            indoorAppState.getPurchasedProducts().remove(track.getSku());
                            track.setAsPurchased(false);
                        }
                    }
                }
                indoorAppState.save();

                notifyDataSetChanged();

                Log.i(TAG, result.toString());
                Log.i(TAG, inv.toString());
            }
        };
    }

    /**
     * Interface used after purchasing a product
     */
    private void setupPurchaseFinishedListener() {
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                // Clear purchased products in phone by issuing:  adb shell pm clear com.android.vending
                Log.e(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mIabHelper == null) return;

                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    Toast.makeText(getActivity(), getString(R.string.product_already_purchased), Toast.LENGTH_SHORT).show();

                    setTrackAsPurchased(indoorAppState.getSelectedTrack());
                    indoorAppState.save();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                    Toast.makeText(getActivity(), getString(R.string.resultAddCloseNegativeBody), Toast.LENGTH_SHORT).show();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                    Toast.makeText(getActivity(), getString(R.string.purchase_canceled), Toast.LENGTH_SHORT).show();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_OK && !result.isFailure() && purchase != null) {
                    String sku = purchase.getSku();

                    if (!indoorAppState.getSelectedTrack().getSku().equals(sku)) {
                        Log.e(TAG, "Purchased product is different than requested");
                    }

                    Toast.makeText(getActivity(), getString(R.string.thanks_for_purchasing), Toast.LENGTH_SHORT).show();

                    setTrackAsPurchased(indoorAppState.getSelectedTrack());
                    indoorAppState.save();

                    downloadTrack(indoorAppState.getSelectedTrack());
                    Log.i(TAG, "purchased: " + sku);
                } else {
                    Log.i(TAG, "Failure, code: " + result.getMessage());

                    Toast.makeText(getActivity(), getString(R.string.purchase_failure), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * Update the view to reflect the purchase
     */
    private void setTrackAsPurchased(Track track) {
        track.setAsPurchased(true);
        indoorAppState.addPurchasedProduct(track.getSku());

        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_track, container, false);

        TwoWayView listTwoWayView = view.findViewById(R.id.twoway_view);

        ButterKnife.bind(this, view);

        final ArrayList<Track> indoorTracks = indoorAppState.getIndoorTracks();

        trackRectAdapter = new TrackRectAdapter(getActivity(), indoorTracks);

        listTwoWayView.setAdapter(trackRectAdapter);

        listTwoWayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Track selectedTrack = indoorTracks.get(position);

                indoorAppState.setSelectedTrack(selectedTrack);

                if (selectedTrack.getAllSegments().size() == 0) {
                    Toast.makeText(getActivity(), R.string.track_not_availabe_check_network, Toast.LENGTH_SHORT).show();
                } else if (!(selectedTrack.isEnabledForUser() || BuildConfig.ALL_TRACKS_AVAILABLE)) {
                    Toast.makeText(getActivity(), R.string.addCloseNegative, Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedTrack.isPurchased()) {
                        if (selectedTrack.isDownloaded()) {
                            if (getActivity() != null) {
                                ((IndoorDashboardActivity) getActivity()).startTrack();
                            }
                        } else {
                            downloadTrack(selectedTrack);
                        }
                    } else {
                        try {
                            if (billingAvailable && mIabHelper != null) {
                                String sku = selectedTrack.getSku();

                                Log.i(TAG, "About to purchase product with SKU: " + sku);
                                if (mIabHelper != null) {
                                    mIabHelper.launchPurchaseFlow(getActivity(), sku, 10002, mPurchaseFinishedListener);
                                }
                            } else {
                                Toast.makeText(getContext(), getString(R.string.cannot_connect_to_google), Toast.LENGTH_LONG).show();
                            }
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            Log.e(TAG, "Error in purchase", e);
                            Toast.makeText(getContext(), getString(R.string.purchase_failure), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        for (Track track : indoorTracks) {
            Log.e(TAG, String.format("Track: %s, Enabled for user: %s, Downloaded: %s, Obtained: %s", track.getId(), track.isEnabledForUser() ? "Yes" : "No", track.isDownloaded() ? "Yes" : "No", track.isPurchased() ? "Yes" : "No"));
        }

        // If network is available, try to download the segments file
        if (isNetworkAvailable()) {
            Log.i(TAG, "Network is available. Downloading catalog of segments");
            indoorAppState.setupIndoorSegments(segmentsCatalogDownloadInterface);
        }
        // If network is not available, check if we have the file downloaded already and use it. Otherwise, wait until network is available.
        else if (!tryLoadSegmentsFromFile()) {
            Log.w(TAG, "Network is not available. Waiting for it...");

            registerForNetworkChange();
        }

        return view;
    }

    private void registerForNetworkChange() {
        // Start listening for connectivity changes
        if (!isNetworkStateReceiverRegistered) {
            Log.w(TAG, "Registering for network state change");
            context.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
            isNetworkStateReceiverRegistered = true;
        } else {
            Log.w(TAG, "Already registered for network state change");
        }
    }

    /**
     * Downloads the selectedTrack videos
     *
     * @param selectedTrack The track to download
     */
    private void downloadTrack(final Track selectedTrack) {
        if (!selectedTrack.isDownloading()) {
            if (!Utils.checkWifiOnAndConnected(getContext())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.network_error).setMessage(R.string.connect_to_wifi).setPositiveButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();

                return;
            }

            Toast.makeText(getContext(), getString(R.string.downloading_please_wait), Toast.LENGTH_LONG).show();
            selectedTrack.download(indoorAppState, getContext(), new SegmentsDownloader.Listener() {
                @Override
                public void onProgressUpdate(int progress) {
                    selectedTrack.setPercentDownloaded(progress);
                    notifyDataSetChanged();
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "Success");
                    selectedTrack.setNotDownloading();
                    notifyDataSetChanged();
                    indoorAppState.save();
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Error");
                    selectedTrack.setNotDownloading();
                    Toast.makeText(getContext(), getString(R.string.download_failure), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getContext(), R.string.download_cancelled, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPreExecute() {
                }

                @Override
                public void onPostExecute() {
                }
            });
        }
    }

    /**
     * Fills the user profile data shown in this fragment. Also sets two of the badges that the user can win.
     */
    public void updateData(User userProfile) {
        ImageView all;
        ImageView newbie;
        ImageView boost;

        TextView txt_user = view.findViewById(R.id.user_txt);
        TextView level_txt = view.findViewById(R.id.level_txt);
        ViewPager viewPager = view.findViewById(R.id.circleDisplayImg);

        SwipeAdapter swipeAdapter = new SwipeAdapter(getContext());
        swipeAdapter.setWeight(userProfile.getWeight());
        viewPager.setAdapter(swipeAdapter);

        // For the badges
        newbie = view.findViewById(R.id.newbie);
        boost = view.findViewById(R.id.boost);

        all = view.findViewById(R.id.all);

        txt_user.setText(userProfile.getName());

        level_txt.setText(indoorAppState.getIndoorLevel() == null ? "" : getContext().getString(indoorAppState.getIndoorLevel().getTitleId()));

        newbie.setImageResource(indoorAppState.getMyBadgeIcon(Badge.Level.NEWBIE));

        boost.setImageResource(indoorAppState.getMyBadgeIcon(Badge.Level.BOOST));

        newbie.setOnClickListener(v -> {
            Badge badge = new Badge(Badge.Level.NEWBIE, indoorAppState.isBadgeWon(Badge.Level.NEWBIE));

            showBadgePopup(badge);
        });

        boost.setOnClickListener(view -> {
            Badge badge = new Badge(Badge.Level.BOOST, indoorAppState.isBadgeWon(Badge.Level.BOOST));

            showBadgePopup(badge);
        });

        all.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((IndoorDashboardActivity) getActivity()).showAllBadges();
            }
        });
    }

    /**
     * If the badge was obtained by the user, show a fragment dialog with it. Otherwise just tell the user it has not been obtained.
     *
     * @param badge The badge to show
     */
    private void showBadgePopup(Badge badge) {
        if (badge.isCompleted() && getActivity() != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogBadgeFullFragment newFragment = DialogBadgeFullFragment.newInstance(badge);
            newFragment.show(ft, "dialog");
        } else {
            Toast.makeText(getActivity(), getString(R.string.badge_not_obtained), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mIabHelper != null) {
            mIabHelper.disposeWhenFinished();
            mIabHelper = null;
        }

        if (networkStateReceiver != null && isNetworkStateReceiverRegistered) {
            context.unregisterReceiver(networkStateReceiver);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIabHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        notifyDataSetChanged();
    }

    /**
     * Notifies the adapter that the data has changed. Uses the main thread, otherwise the message would be ignored.
     */
    private void notifyDataSetChanged() {
        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                trackRectAdapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * Checks if a file with the segment data exists. If so, sets up the segments using it. Otherwise, returns false.
     *
     * @return Whether it was possible to load the segments JSON from a saved file
     */
    private boolean tryLoadSegmentsFromFile() {
        Log.w(TAG, "Trying to load segments from file");
        if (indoorAppState.segmentsFileExists()) {
            try {
                // We don't initialize the store since there is no connection.
                // if (!storeInitialized) mIabHelper.startSetup(mSetupFinishedListener);
                indoorAppState.loadSegmentsFromFile();
                notifyDataSetChanged();
                return true;
            } catch (Exception ex) {
                Log.e(TAG, "Error reading segments file: ", ex);
            }
        } else {
            Log.w(TAG, "Segments file doesn't exist");
        }
        return false;
    }
}