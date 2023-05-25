package com.runin.runinapp.outdoor.plans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.runin.runinapp.BuildConfig;
import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.adapters.OutdoorPlansSwipeAdapter;
import com.runin.runinapp.adapters.StageRectAdapter;
import com.runin.runinapp.data.OutdoorAppState;
import com.runin.runinapp.data.Plan;
import com.runin.runinapp.data.Stage;
import com.runin.runinapp.data.User;
import com.runin.runinapp.utils.iabHelper.IabHelper;
import com.runin.runinapp.utils.iabHelper.IabResult;
import com.runin.runinapp.utils.iabHelper.Inventory;
import com.runin.runinapp.utils.iabHelper.Purchase;

import org.lucasr.twowayview.TwoWayView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Please document this class
 * Created by Citrus01 on 11/06/2017.
 */

public class PlansMainFragment extends Fragment {

    private static final String TAG = PlansMainFragment.class.getSimpleName();

    @BindView(R.id.user_txt)
    TextView txt_user;

    @BindView(R.id.desc_plan)
    TextView desc_plan;

    @BindView(R.id.twoway_view)
    TwoWayView stagesList;

    @BindView(R.id.circleDisplayImg)

    ViewPager viewPager;

    /**
     * Listener for the product catalog from the store
     */
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;

    /**
     * The object that processes the in app purchase
     */
    private IabHelper mIabHelper;

    /**
     * Listener that implements the purchase process
     */
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    /**
     * False if can't access Google Play Store
     */
    private boolean billingAvailable = false;

    @Inject
    User userProfile;

    private OutdoorAppState outdoorAppState;
    private Context context;
    private List<Stage> stages;
    private Stage selectedStage;
    private StageRectAdapter adapter;
    private Plan selectedPlan;

    public PlansMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the application context
        this.context = getContext();

        // Gets the application state
        if (getActivity() == null) {
            return;
        }

        App app = (App) getActivity().getApplication();
        outdoorAppState = app.getOutdoorAppState();
        selectedPlan = outdoorAppState.getSelectedPlan();
        stages = selectedPlan.getStages();
        adapter = new StageRectAdapter(getActivity(), stages);

        String base64EncodedPublicKey = getString(R.string.base64EncodedPublicKey);

        // Interface used when initializing the store.
        IabHelper.OnIabSetupFinishedListener mSetupFinishedListener = new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                // Checks that we can access the store
                billingAvailable = result.isSuccess();
                if (!billingAvailable) {
                    Log.e(TAG, "InAppPurchase isn't available");
                    return;
                }

                // Maybe another purchase process has finished while we're at it
                if (mIabHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                try {
                    mIabHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "Error querying inventory. Another async operation in progress.");
                }

                Log.e(TAG, "Billing setup success");
            }
        };

        // Interface used after retrieving the inventory from store
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                // Maybe another purchase process has finished while we're at it
                if (mIabHelper == null) return;

                if (result.isFailure()) {
                    Log.e(TAG, "Failed to get Inventory: " + result);
                    return;
                }

                for (Stage stage : stages) {
                    if (stage.getSku() == null || stage.getSku().equals("")) {
                        Log.e(TAG, "Stage has no SKU setup yet");
                        continue;
                    }

                    if (inv.hasPurchase(stage.getSku())) {
                        setStageAsPurchased(stage);
                    } else {
                        if (outdoorAppState.getPurchasedProducts().contains(stage.getSku())) {
                            outdoorAppState.getPurchasedProducts().remove(stage.getSku());
                            stage.setPurchased(false);
                        }
                    }
                }
                outdoorAppState.save();

                if (adapter != null) {
                    notifyDataSetChanged();
                }

                Log.i(TAG, result.toString());
                Log.i(TAG, inv.toString());
            }
        };

        // Interface used after purchasing a product
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                // Clear purchased products in phone by issuing:  adb shell pm clear com.android.vending
                Log.e(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mIabHelper == null) return;

                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    Toast.makeText(getActivity(), getString(R.string.product_already_purchased), Toast.LENGTH_SHORT).show();

                    setStageAsPurchased(outdoorAppState.getSelectedStage());
                    outdoorAppState.save();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                    Toast.makeText(getActivity(), getString(R.string.resultAddCloseNegativeBody), Toast.LENGTH_SHORT).show();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                    Toast.makeText(getActivity(), getString(R.string.purchase_canceled), Toast.LENGTH_SHORT).show();
                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_OK && !result.isFailure() && purchase != null) {
                    String sku = purchase.getSku();

                    if (!outdoorAppState.getSelectedStage().getSku().equals(sku)) {
                        Log.e(TAG, "Purchased product is different than requested");
                    }

                    Toast.makeText(getActivity(), getString(R.string.thanks_for_purchasing), Toast.LENGTH_SHORT).show();

                    setStageAsPurchased(outdoorAppState.getSelectedStage());
                    outdoorAppState.save();
                    Log.i(TAG, "purchased: " + sku);
                } else {
                    Log.i(TAG, "Failure, code: " + result.getMessage());

                    Toast.makeText(getActivity(), getString(R.string.purchase_failure), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Initializes the store
        mIabHelper = new IabHelper(context, base64EncodedPublicKey);
        mIabHelper.enableDebugLogging(true);
        mIabHelper.startSetup(mSetupFinishedListener);
    }

    /**
     * Update the view to reflect the purchase
     */
    private void setStageAsPurchased(Stage stage) {
        stage.setPurchased(true);
        outdoorAppState.addPurchasedProduct(stage.getSku());

        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_plans, container, false);

        ButterKnife.bind(this, view);

        OutdoorPlansSwipeAdapter swipeAdapter = new OutdoorPlansSwipeAdapter(getContext(), outdoorAppState);
        viewPager.setAdapter(swipeAdapter);
        txt_user.setText(userProfile.getName());

        desc_plan.setText(selectedPlan.getDescription());

        stagesList.setAdapter(adapter);
        stagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStage = stages.get(i);
                outdoorAppState.setSelectedStage(selectedStage);

                // If is the first one in the plan let us continue.
                if (selectedStage.getSequence() == 0) {
                    showNextScreen();
                    return;
                }

                // On the emulator, it is always purchased
                /*if (BuildConfig.DEBUG && Utils.isEmulator()) {
                    selectedStage.setPurchased(true);
                }*/

                double stagePercentFinished = outdoorAppState.stagePercentFinished(selectedPlan.getId(), selectedStage.getSequence() - 1, context);

                Log.i(TAG, String.format("Percentage finished for stage %d: %.1f, Minimum percentage: %.1f", selectedStage.getSequence() - 1, outdoorAppState.stagePercentFinished(selectedPlan.getId(), selectedStage.getSequence() - 1, context), App.minimumPacePercentageForCompleteStage));

                // If the user didn't finish at least 60% of the previous training, don't allow to continue
                if (!BuildConfig.ALL_TRACKS_AVAILABLE && stagePercentFinished < App.minimumPacePercentageForCompleteStage) {
                    Toast.makeText(PlansMainFragment.this.context, getString(R.string.you_didnt_complete_previous_stage, App.minimumPacePercentageForCompleteStage * 100.0), Toast.LENGTH_SHORT).show();
                }
                // If we have purchased the stage let us continue.
                else if (selectedStage.isPurchased()) {
                    showNextScreen();
                }
                // Allow the use to purchase the stage
                else {
                    purchaseStage();
                }
            }
        });

        notifyDataSetChanged();
        return view;
    }

    private void purchaseStage() {
        try {
            if (billingAvailable && mIabHelper != null) {
                String sku = selectedStage.getSku();

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

    private void showNextScreen() {
        Intent intentOutdoor = new Intent(getContext(), SelectTrainingActivity.class);
        startActivity(intentOutdoor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mIabHelper != null) {
            mIabHelper.disposeWhenFinished();
            mIabHelper = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, String.format("Got an activity result of requestCode: %d, resultCode: %d", requestCode, resultCode));

        if (mIabHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                adapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(myRunnable);
    }
}
