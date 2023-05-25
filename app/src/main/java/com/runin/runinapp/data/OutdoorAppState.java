package com.runin.runinapp.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.runin.runinapp.App;
import com.runin.runinapp.R;
import com.runin.runinapp.data.Stage.FinishedStatus;
import com.runin.runinapp.data.database.GPSRunPoint;
import com.runin.runinapp.data.database.GPSRunPointOperationsDB;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistory;
import com.runin.runinapp.data.database.OutdoorWorkoutsHistoryOperationsDB;
import com.runin.runinapp.utils.DateUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Outdoor App State contains all the session information for the outdoor part of the app
 * Created by Samuel Kobelkowsky on 11/23/17.
 */
public class OutdoorAppState {
    // The titles of the different plans
    public static final String PLAN_3K_TITLE = "3K";
    public static final String PLAN_5K_TITLE = "5K";
    public static final String PLAN_10K_TITLE = "10K";
    public static final String PLAN_5K_PLUS_TITLE = "5K+";
    public static final String PLAN_10K_PLUS_TITLE = "10K+";

    // The IDs of the different plans
    static final int PLAN_3K_ID = 0;
    static final int PLAN_5K_LONG_ID = 1;
    static final int PLAN_10K_LONG_ID = 2;
    static final int PLAN_5K_PLUS_LONG_ID = 3;
    static final int PLAN_10K_PLUS_LONG_ID = 4;
    static final int PLAN_5K_SHORT_ID = 5;
    static final int PLAN_10K_SHORT_ID = 6;
    static final int PLAN_5K_PLUS_SHORT_ID = 7;
    static final int PLAN_10K_PLUS_SHORT_ID = 8;

    private static final String TAG = OutdoorAppState.class.getSimpleName();

    // The names of the files where some info is stored
    private static final String PreviouslySelectedPlansFilename = "previously_selected_plans";
    private static final String PurchasedProductsFilename = "outdoor_purchased_products";

    /**
     * The list of plans of the application
     */
    @NonNull
    private final List<Plan> plans;

    /**
     * The list of purchased product names:
     */
    @NonNull
    private final List<String> purchasedProducts;

    /**
     * The application context
     */
    private final Context context;

    /**
     * The list of plans previously selected ("purchased") by the user
     */
    @NonNull
    private final List<String> previouslySelectedPlans;

    /**
     * Used for database operations
     */
    private OutdoorWorkoutsHistory workout;

    /**
     * The focus of the plan selected by the user: Length or Distance
     */
    private Plan.Focus selectedPlanFocus;

    /**
     * The plan selected by the user
     */
    private Plan selectedPlan;

    /**
     * The name of the plan selected by the user. Be aware that more than one plan can share the same name
     * Used when the user decided for a distance but still doesn't select the length
     */
    private String selectedPlanName;

    /**
     * The selected stage of a plan
     */
    private Stage selectedStage;

    /**
     * The selected training of a plan
     */
    private Training selectedTraining;

    /**
     * The selected workout type
     */
    private WorkoutType selectedWorkoutType;

    /**
     * Class constructor
     *
     * @param context The application context
     */
    public OutdoorAppState(Context context) {
        this.context = context;

        plans = new ArrayList<>();
        previouslySelectedPlans = new ArrayList<>();
        purchasedProducts = new ArrayList<>();

        additionalSetup();
    }

    /**
     * Create the different plans, assign stages to them and trainings to the stages. Also load the list of purchased products and other initialization of the class
     */
    private void additionalSetup() {

        loadPreviouslySelectedPlans();

        Plan plan3K = new Plan(PLAN_3K_ID, PLAN_3K_TITLE, Plan.Length.Long, Plan.Focus.Distance);
        Plan plan5KShort = new Plan(PLAN_5K_SHORT_ID, PLAN_5K_TITLE, Plan.Length.Short, Plan.Focus.Distance);
        Plan plan5KLong = new Plan(PLAN_5K_LONG_ID, PLAN_5K_TITLE, Plan.Length.Long, Plan.Focus.Distance);
        Plan plan10KShort = new Plan(PLAN_10K_SHORT_ID, PLAN_10K_TITLE, Plan.Length.Short, Plan.Focus.Distance);
        Plan plan10KLong = new Plan(PLAN_10K_LONG_ID, PLAN_10K_TITLE, Plan.Length.Long, Plan.Focus.Distance);
        Plan plan5KPlusShort = new Plan(PLAN_5K_PLUS_SHORT_ID, PLAN_5K_PLUS_TITLE, Plan.Length.Short, Plan.Focus.Speed);
        Plan plan5KPlusLong = new Plan(PLAN_5K_PLUS_LONG_ID, PLAN_5K_PLUS_TITLE, Plan.Length.Long, Plan.Focus.Speed);
        Plan plan10KPlusShort = new Plan(PLAN_10K_PLUS_SHORT_ID, PLAN_10K_PLUS_TITLE, Plan.Length.Short, Plan.Focus.Speed);
        Plan plan10KPlusLong = new Plan(PLAN_10K_PLUS_LONG_ID, PLAN_10K_PLUS_TITLE, Plan.Length.Long, Plan.Focus.Speed);

        plans.clear();
        plans.add(plan3K);
        plans.add(plan5KShort);
        plans.add(plan5KLong);
        plans.add(plan10KShort);
        plans.add(plan10KLong);
        plans.add(plan5KPlusShort);
        plans.add(plan5KPlusLong);
        plans.add(plan10KPlusShort);
        plans.add(plan10KPlusLong);

        // region Stages
        plan3K.getStages().clear();
        plan3K.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan3K.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan3k_e2"));

        plan5KLong.getStages().clear();
        plan5KLong.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan5KLong.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan5k_e2"));
        plan5KLong.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan5k_e3"));
        plan5KLong.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan5k_e4"));
        plan5KLong.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan5k_e5"));

        plan10KLong.getStages().clear();
        plan10KLong.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan10KLong.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan10k_e2"));
        plan10KLong.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan10k_e3"));
        plan10KLong.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan10k_e4"));
        plan10KLong.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan10k_e5"));

        plan5KPlusLong.getStages().clear();
        plan5KPlusLong.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan5KPlusLong.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan5k_plus_e2"));
        plan5KPlusLong.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan5k_plus_e3"));
        plan5KPlusLong.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan5k_plus_e4"));
        plan5KPlusLong.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan5k_plus_e5"));

        plan10KPlusLong.getStages().clear();
        plan10KPlusLong.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan10KPlusLong.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan10k_plus_e2"));
        plan10KPlusLong.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan10k_plus_e3"));
        plan10KPlusLong.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan10k_plus_e4"));
        plan10KPlusLong.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan10k_plus_e5"));

        plan5KShort.getStages().clear();
        plan5KShort.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan5KShort.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan5k_e2"));
        plan5KShort.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan5k_e3"));
        plan5KShort.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan5k_e4"));
        plan5KShort.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan5k_e5"));

        plan10KShort.getStages().clear();
        plan10KShort.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan10KShort.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan10k_e2"));
        plan10KShort.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan10k_e3"));
        plan10KShort.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan10k_e4"));
        plan10KShort.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan10k_e5"));

        plan5KPlusShort.getStages().clear();
        plan5KPlusShort.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan5KPlusShort.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan5k_plus_e2"));
        plan5KPlusShort.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan5k_plus_e3"));
        plan5KPlusShort.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan5k_plus_e4"));
        plan5KPlusShort.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan5k_plus_e5"));

        plan10KPlusShort.getStages().clear();
        plan10KPlusShort.getStages().add(new Stage(context.getString(R.string.stage1), 0, context.getString(R.string.etapa_uno), null));
        plan10KPlusShort.getStages().add(new Stage(context.getString(R.string.stage2), 1, context.getString(R.string.etapa_dos), "plan10k_plus_e2"));
        plan10KPlusShort.getStages().add(new Stage(context.getString(R.string.stage3), 2, context.getString(R.string.etapa_tres), "plan10k_plus_e3"));
        plan10KPlusShort.getStages().add(new Stage(context.getString(R.string.stage4), 3, context.getString(R.string.etapa_cuatro), "plan10k_plus_e4"));
        plan10KPlusShort.getStages().add(new Stage(context.getString(R.string.stage5), 4, context.getString(R.string.etapa_cinco), "plan10k_plus_e5"));

        plan3K.getStage(0).setPurchased(true);
        plan5KLong.getStage(0).setPurchased(true);
        plan10KLong.getStage(0).setPurchased(true);
        plan5KPlusLong.getStage(0).setPurchased(true);
        plan10KPlusLong.getStage(0).setPurchased(true);
        plan5KShort.getStage(0).setPurchased(true);
        plan10KShort.getStage(0).setPurchased(true);
        plan5KPlusShort.getStage(0).setPurchased(true);
        plan10KPlusShort.getStage(0).setPurchased(true);
        // endregion

        // region Trainings
        plan3K.getStage(0).getTrainings().clear();
        plan3K.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.not_available)));
        plan3K.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_3_strong_intervals)));
        plan3K.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_4_strong_intervals)));
        plan3K.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_4_strong_intervals)));
        plan3K.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_5_strong_intervals)));
        plan3K.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_5_strong_intervals)));
        plan3K.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan3K.getStage(1).getTrainings().clear();
        plan3K.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_4_strong_intervals)));
        plan3K.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.not_available)));
        plan3K.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.not_available)));
        plan3K.getStage(1).getTrainings().add(new Training(10, false, context.getString(R.string.not_available)));
        plan3K.getStage(1).getTrainings().add(new Training(11, false, context.getString(R.string.not_available)));
        plan3K.getStage(1).getTrainings().add(new Training(12, false, context.getString(R.string.not_available)));
        plan3K.getStage(1).getTrainings().add(new Training(13, true, context.getString(R.string.test)));

        plan5KLong.getStage(0).getTrainings().clear();
        plan5KLong.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_7_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_11_strong_intervals)));
        plan5KLong.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan5KLong.getStage(1).getTrainings().clear();
        plan5KLong.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.plan_7_strong_intervals)));
        plan5KLong.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(1).getTrainings().add(new Training(10, false, context.getString(R.string.plan_11_strong_intervals)));
        plan5KLong.getStage(1).getTrainings().add(new Training(11, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(1).getTrainings().add(new Training(12, true, context.getString(R.string.test)));

        plan5KLong.getStage(2).getTrainings().clear();
        plan5KLong.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(2).getTrainings().add(new Training(14, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KLong.getStage(2).getTrainings().add(new Training(15, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(2).getTrainings().add(new Training(16, true, context.getString(R.string.test)));

        plan5KLong.getStage(3).getTrainings().clear();
        plan5KLong.getStage(3).getTrainings().add(new Training(17, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(3).getTrainings().add(new Training(18, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KLong.getStage(3).getTrainings().add(new Training(19, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KLong.getStage(3).getTrainings().add(new Training(20, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KLong.getStage(3).getTrainings().add(new Training(21, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(3).getTrainings().add(new Training(22, true, context.getString(R.string.test)));

        plan5KLong.getStage(4).getTrainings().clear();
        plan5KLong.getStage(4).getTrainings().add(new Training(23, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(4).getTrainings().add(new Training(24, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KLong.getStage(4).getTrainings().add(new Training(25, false, context.getString(R.string.plan_2_strong_intervals)));
        plan5KLong.getStage(4).getTrainings().add(new Training(26, false, context.getString(R.string.preparation)));
        plan5KLong.getStage(4).getTrainings().add(new Training(27, true, context.getString(R.string.final_test)));
        plan5KLong.getStage(4).getTrainings().add(new Training(28, false, context.getString(R.string.preparation)));
        plan5KLong.getStage(4).getTrainings().add(new Training(29, false, context.getString(R.string.run_simulation)));

        plan10KLong.getStage(0).getTrainings().clear();
        plan10KLong.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KLong.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KLong.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KLong.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KLong.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KLong.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KLong.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan10KLong.getStage(1).getTrainings().clear();
        plan10KLong.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KLong.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.plan_7_strong_intervals)));
        plan10KLong.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KLong.getStage(1).getTrainings().add(new Training(10, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KLong.getStage(1).getTrainings().add(new Training(11, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KLong.getStage(1).getTrainings().add(new Training(12, true, context.getString(R.string.test)));

        plan10KLong.getStage(2).getTrainings().clear();
        plan10KLong.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KLong.getStage(2).getTrainings().add(new Training(14, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KLong.getStage(2).getTrainings().add(new Training(15, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KLong.getStage(2).getTrainings().add(new Training(16, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KLong.getStage(2).getTrainings().add(new Training(17, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KLong.getStage(2).getTrainings().add(new Training(18, true, context.getString(R.string.test)));

        plan10KLong.getStage(3).getTrainings().clear();
        plan10KLong.getStage(3).getTrainings().add(new Training(19, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KLong.getStage(3).getTrainings().add(new Training(20, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KLong.getStage(3).getTrainings().add(new Training(21, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KLong.getStage(3).getTrainings().add(new Training(22, false, context.getString(R.string.plan_4_strong_intervals)));
        plan10KLong.getStage(3).getTrainings().add(new Training(23, false, context.getString(R.string.plan_2_strong_intervals)));
        plan10KLong.getStage(3).getTrainings().add(new Training(24, true, context.getString(R.string.test)));

        plan10KLong.getStage(4).getTrainings().clear();
        plan10KLong.getStage(4).getTrainings().add(new Training(25, false, context.getString(R.string.plan_4_strong_intervals)));
        plan10KLong.getStage(4).getTrainings().add(new Training(26, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KLong.getStage(4).getTrainings().add(new Training(27, false, context.getString(R.string.preparation)));
        plan10KLong.getStage(4).getTrainings().add(new Training(28, false, context.getString(R.string.run_simulation)));

        plan5KPlusLong.getStage(0).getTrainings().clear();
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KPlusLong.getStage(0).getTrainings().add(new Training(5, true, context.getString(R.string.test)));

        plan5KPlusLong.getStage(1).getTrainings().clear();
        plan5KPlusLong.getStage(1).getTrainings().add(new Training(6, false, context.getString(R.string.plan_7_strong_intervals)));
        plan5KPlusLong.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusLong.getStage(1).getTrainings().add(new Training(8, true, context.getString(R.string.test)));

        plan5KPlusLong.getStage(2).getTrainings().clear();
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(9, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(10, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(11, false, context.getString(R.string.plan_1_strong_interval)));
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(12, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KPlusLong.getStage(2).getTrainings().add(new Training(14, true, context.getString(R.string.test)));

        plan5KPlusLong.getStage(3).getTrainings().clear();
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(15, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(16, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(17, false, context.getString(R.string.plan_1_strong_interval)));
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(18, false, context.getString(R.string.plan_4_strong_intervals)));
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(19, false, context.getString(R.string.plan_2_strong_intervals)));
        plan5KPlusLong.getStage(3).getTrainings().add(new Training(20, true, context.getString(R.string.test)));

        plan5KPlusLong.getStage(4).getTrainings().clear();
        plan5KPlusLong.getStage(4).getTrainings().add(new Training(21, false, context.getString(R.string.plan_4_strong_intervals)));
        plan5KPlusLong.getStage(4).getTrainings().add(new Training(22, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KPlusLong.getStage(4).getTrainings().add(new Training(23, false, context.getString(R.string.preparation)));
        plan5KPlusLong.getStage(4).getTrainings().add(new Training(24, false, context.getString(R.string.run_simulation)));

        plan10KPlusLong.getStage(0).getTrainings().clear();
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusLong.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan10KPlusLong.getStage(1).getTrainings().clear();
        plan10KPlusLong.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusLong.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusLong.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.plan_2_strong_intervals)));
        plan10KPlusLong.getStage(1).getTrainings().add(new Training(10, true, context.getString(R.string.test)));

        plan10KPlusLong.getStage(2).getTrainings().clear();
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(11, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(12, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(14, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(15, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusLong.getStage(2).getTrainings().add(new Training(16, true, context.getString(R.string.test)));

        plan10KPlusLong.getStage(3).getTrainings().clear();
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(17, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(18, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(19, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(20, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(21, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusLong.getStage(3).getTrainings().add(new Training(22, true, context.getString(R.string.test)));

        plan10KPlusLong.getStage(4).getTrainings().clear();
        plan10KPlusLong.getStage(4).getTrainings().add(new Training(23, false, context.getString(R.string.plan_4_strong_intervals)));
        plan10KPlusLong.getStage(4).getTrainings().add(new Training(24, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusLong.getStage(4).getTrainings().add(new Training(25, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KPlusLong.getStage(4).getTrainings().add(new Training(26, false, context.getString(R.string.preparation)));
        plan10KPlusLong.getStage(4).getTrainings().add(new Training(27, false, context.getString(R.string.run_simulation)));

        plan5KShort.getStage(0).getTrainings().clear();
        plan5KShort.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.initial_test)));
        plan5KShort.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KShort.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_11_strong_intervals)));
        plan5KShort.getStage(0).getTrainings().add(new Training(3, true, context.getString(R.string.test)));

        plan5KShort.getStage(1).getTrainings().clear();
        plan5KShort.getStage(1).getTrainings().add(new Training(4, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KShort.getStage(1).getTrainings().add(new Training(5, false, context.getString(R.string.plan_11_strong_intervals)));
        plan5KShort.getStage(1).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan5KShort.getStage(2).getTrainings().clear();
        plan5KShort.getStage(2).getTrainings().add(new Training(7, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KShort.getStage(2).getTrainings().add(new Training(8, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KShort.getStage(2).getTrainings().add(new Training(9, true, context.getString(R.string.test)));

        plan5KShort.getStage(3).getTrainings().clear();
        plan5KShort.getStage(3).getTrainings().add(new Training(10, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KShort.getStage(3).getTrainings().add(new Training(11, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KShort.getStage(3).getTrainings().add(new Training(12, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KShort.getStage(3).getTrainings().add(new Training(13, true, context.getString(R.string.test)));

        plan5KShort.getStage(4).getTrainings().clear();
        plan5KShort.getStage(4).getTrainings().add(new Training(14, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KShort.getStage(4).getTrainings().add(new Training(15, false, context.getString(R.string.plan_2_strong_intervals)));
        plan5KShort.getStage(4).getTrainings().add(new Training(16, true, context.getString(R.string.test)));
        plan5KShort.getStage(4).getTrainings().add(new Training(17, false, context.getString(R.string.preparation)));
        plan5KShort.getStage(4).getTrainings().add(new Training(18, false, context.getString(R.string.run_simulation)));

        plan10KShort.getStage(0).getTrainings().clear();
        plan10KShort.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KShort.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KShort.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KShort.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KShort.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KShort.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KShort.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan10KShort.getStage(1).getTrainings().clear();
        plan10KShort.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KShort.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.plan_7_strong_intervals)));
        plan10KShort.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KShort.getStage(1).getTrainings().add(new Training(10, true, context.getString(R.string.test)));

        plan10KShort.getStage(2).getTrainings().clear();
        plan10KShort.getStage(2).getTrainings().add(new Training(11, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KShort.getStage(2).getTrainings().add(new Training(12, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KShort.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KShort.getStage(2).getTrainings().add(new Training(14, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KShort.getStage(2).getTrainings().add(new Training(15, true, context.getString(R.string.test)));

        plan10KShort.getStage(3).getTrainings().clear();
        plan10KShort.getStage(3).getTrainings().add(new Training(16, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KShort.getStage(3).getTrainings().add(new Training(17, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KShort.getStage(3).getTrainings().add(new Training(18, false, context.getString(R.string.plan_4_strong_intervals)));
        plan10KShort.getStage(3).getTrainings().add(new Training(19, false, context.getString(R.string.plan_2_strong_intervals)));
        plan10KShort.getStage(3).getTrainings().add(new Training(20, true, context.getString(R.string.test)));

        plan10KShort.getStage(4).getTrainings().clear();
        plan10KShort.getStage(4).getTrainings().add(new Training(21, false, context.getString(R.string.plan_4_strong_intervals)));
        plan10KShort.getStage(4).getTrainings().add(new Training(22, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KShort.getStage(4).getTrainings().add(new Training(23, false, context.getString(R.string.preparation)));
        plan10KShort.getStage(4).getTrainings().add(new Training(24, false, context.getString(R.string.run_simulation)));

        plan5KPlusShort.getStage(0).getTrainings().clear();
        plan5KPlusShort.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_4_strong_intervals)));
        plan5KPlusShort.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_6_strong_intervals)));
        plan5KPlusShort.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusShort.getStage(0).getTrainings().add(new Training(3, true, context.getString(R.string.test)));

        plan5KPlusShort.getStage(1).getTrainings().clear();
        plan5KPlusShort.getStage(1).getTrainings().add(new Training(4, false, context.getString(R.string.plan_7_strong_intervals)));
        plan5KPlusShort.getStage(1).getTrainings().add(new Training(5, false, context.getString(R.string.plan_3_strong_intervals)));
        plan5KPlusShort.getStage(1).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan5KPlusShort.getStage(2).getTrainings().clear();
        plan5KPlusShort.getStage(2).getTrainings().add(new Training(7, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KPlusShort.getStage(2).getTrainings().add(new Training(8, false, context.getString(R.string.plan_1_strong_interval)));
        plan5KPlusShort.getStage(2).getTrainings().add(new Training(9, false, context.getString(R.string.plan_5_strong_intervals)));
        plan5KPlusShort.getStage(2).getTrainings().add(new Training(10, true, context.getString(R.string.test)));

        plan5KPlusShort.getStage(3).getTrainings().clear();
        plan5KPlusShort.getStage(3).getTrainings().add(new Training(11, false, context.getString(R.string.plan_8_strong_intervals)));
        plan5KPlusShort.getStage(3).getTrainings().add(new Training(12, false, context.getString(R.string.plan_1_strong_interval)));
        plan5KPlusShort.getStage(3).getTrainings().add(new Training(13, false, context.getString(R.string.plan_2_strong_intervals)));
        plan5KPlusShort.getStage(3).getTrainings().add(new Training(14, true, context.getString(R.string.test)));

        plan5KPlusShort.getStage(4).getTrainings().clear();
        plan5KPlusShort.getStage(4).getTrainings().add(new Training(15, false, context.getString(R.string.plan_4_strong_intervals)));
        plan5KPlusShort.getStage(4).getTrainings().add(new Training(16, false, context.getString(R.string.preparation)));
        plan5KPlusShort.getStage(4).getTrainings().add(new Training(17, false, context.getString(R.string.run_simulation)));

        plan10KPlusShort.getStage(0).getTrainings().clear();
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(0, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(1, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(2, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(3, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(4, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(5, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusShort.getStage(0).getTrainings().add(new Training(6, true, context.getString(R.string.test)));

        plan10KPlusShort.getStage(1).getTrainings().clear();
        plan10KPlusShort.getStage(1).getTrainings().add(new Training(7, false, context.getString(R.string.plan_6_strong_intervals)));
        plan10KPlusShort.getStage(1).getTrainings().add(new Training(8, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusShort.getStage(1).getTrainings().add(new Training(9, false, context.getString(R.string.plan_2_strong_intervals)));
        plan10KPlusShort.getStage(1).getTrainings().add(new Training(10, true, context.getString(R.string.test)));

        plan10KPlusShort.getStage(2).getTrainings().clear();
        plan10KPlusShort.getStage(2).getTrainings().add(new Training(11, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusShort.getStage(2).getTrainings().add(new Training(12, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusShort.getStage(2).getTrainings().add(new Training(13, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusShort.getStage(2).getTrainings().add(new Training(14, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusShort.getStage(2).getTrainings().add(new Training(15, true, context.getString(R.string.test)));

        plan10KPlusShort.getStage(3).getTrainings().clear();
        plan10KPlusShort.getStage(3).getTrainings().add(new Training(16, false, context.getString(R.string.plan_3_strong_intervals)));
        plan10KPlusShort.getStage(3).getTrainings().add(new Training(17, false, context.getString(R.string.plan_5_strong_intervals)));
        plan10KPlusShort.getStage(3).getTrainings().add(new Training(18, true, context.getString(R.string.test)));

        plan10KPlusShort.getStage(4).getTrainings().clear();
        plan10KPlusShort.getStage(4).getTrainings().add(new Training(19, false, context.getString(R.string.plan_1_strong_interval)));
        plan10KPlusShort.getStage(4).getTrainings().add(new Training(20, false, context.getString(R.string.plan_8_strong_intervals)));
        plan10KPlusShort.getStage(4).getTrainings().add(new Training(21, false, context.getString(R.string.preparation)));
        plan10KPlusShort.getStage(4).getTrainings().add(new Training(22, false, context.getString(R.string.run_simulation)));
        //endregion

        loadPurchasedProducts();

    }

    /**
     * Returns a list of phases for a given combination of plan, stage and training
     *
     * @param planId         The plan id
     * @param stageNumber    The stage sequence number
     * @param trainingNumber The training sequence number
     * @return The list of phases for the given plan, stage and training
     */
    private List<Phase> getPhases(int planId, int stageNumber, int trainingNumber) {
        List<Phase> phases = new ArrayList<>();

        switch (planId) {
            case PLAN_3K_ID:
                switch (stageNumber) {
                    // Long 3K plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.67));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.66));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.66));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.66));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.67));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.67));
                                break;
                            case 1:
                                phases.add(new Phase(9, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.64));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.64));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 113.64));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.67));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 10, 300));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.3));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.3));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.3));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 150));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 285.715));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.2857143));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 9.5, 105.2631579));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.2857143));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 9.5, 105.2631579));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.2857143));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 9.5, 105.2631579));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 7, 214.2857143));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 9.5, 105.2631579));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.8, 147.06));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.8, 147.06));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.8, 147.06));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.8, 147.06));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.8, 147.06));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.RECOVERY, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 9, 166.6666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.8, 220.5882353));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.8, 220.5882353));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.8, 220.5882353));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.8, 294.1176471));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 541.6666667));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 7.5, 1066.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 3K plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 9, 333.6666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 230.7692308));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 9, 333.6666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 230.7692308));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 9, 333.6666666));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 230.7692308));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 9, 333.6666666));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 541.6666667));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 8, 375));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 9, 333.6666666));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 461.54));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 9, 166.6666667));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 461.54));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 9, 166.6666667));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 7.2, 416.6666667));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 541.6666667));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 8, 375));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.5, 307.6923077));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 210.5263158));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.5, 461.54));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 210.5263158));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.5, 461.54));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 210.5263158));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.5, 461.54));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 8, 375));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.9, 434.7826087));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.9, 579.72));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.9, 724.6376812));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 923.0769231));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 8.8, 340.91));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.9, 434.7826087));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.9, 579.72));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.9, 724.6376812));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 923.0769231));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.WALK, 8.8, 340.909091));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.9, 434.782609));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.263158));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.9, 579.710145));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.263158));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.9, 724.64));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 105.2631579));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.5, 923.0769231));
                                phases.add(new Phase(9, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.8, 340.909091));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 9.5, 315.789474));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 6.9, 1449.28));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_5K_LONG_ID:
                switch (stageNumber) {
                    // Long 5K plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 583.4));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 583.4));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.58, 350));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.58, 350));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.58, 350));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_WALK, 8.58, 350));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 583.4));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(16, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(110, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(13, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(16, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 450));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.715, 1750));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 5K plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 58.4));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 58.4));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 58.4));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 116.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 116.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 116.6666667));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 600));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(17, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(18, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(19, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(20, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(21, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 175));
                                phases.add(new Phase(22, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.4));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 450));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.715, 2100));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 5K plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 750));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 116.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 750));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 875));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 700));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.715, 2625));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 5K plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1225));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.575, 233.3333333));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1225));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1225));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1225));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 450));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1750));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1400));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1050));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1400));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1400));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1400));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.715, 3150));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 5K plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 23:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 1000));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 350));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 24:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 2100));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 2100));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 2100));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 25:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 1750));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.5715, 233.3333333));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.715, 2625));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 26:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 400));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 400));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 400));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 833.34));
                                break;
                            case 27:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.715, 3850));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 28:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 833.3333333));
                                break;
                            case 29:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.RUN, Phase.Instruction.FAST_JOG, 5.715, 5425));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_10K_LONG_ID:
                switch (stageNumber) {
                    // Long 10K plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.3, 318));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.5, 1454.7));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.3, 318));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.5, 1454.7));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.3, 318));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.5, 1454.7));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.3, 318));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.3, 6033));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 458.3333333));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 334.3));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 266.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.84));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1250));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.84));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1250));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.3, 6032.6));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 334.3));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 4950));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                    // Long 10K plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6966.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 550));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.9));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1108.666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 10, 500));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 800));
                                break;
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 4433.666666));
                                phases.add(new Phase(1, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 5500));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Long 10K plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 3391.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6966.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2383.666666));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 633.6666666));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 5866.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                    // Long 10K plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6016.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 7499));
                                phases.add(new Phase(1, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.6666666));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.6666666));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.6666666));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.6666666));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.6666666));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 23:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1187.5));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.866666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.866666));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                            case 24:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 7883.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                    // Long 10K plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 25:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.6666666));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 26:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 275));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 27:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.6666666));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 2083.666666));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 28:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.6666666));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.FAST_JOG, 5.454545455, 9900));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_5K_PLUS_LONG_ID:
                switch (stageNumber) {
                    // Long 5K+ plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 375));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 375));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 458.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 375));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.8666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1145.8666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.8666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1145.8666666));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 8.571428571, 525.2));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6333.666666));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.866666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.866666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.866666));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3020.866666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Long 5K+ plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 870.8666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 550));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.6666666));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.8666666));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.666666));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3541.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                    // Long 5K+ plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 291.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 291.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1833.666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(11, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 7333.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(11, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1504.166667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 633.666666));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3958.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.666666));
                                break;
                        }
                        break;
                    // Long 5K+ plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.666666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 275));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(17, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1979.166667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.666666));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.666666));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 7125));
                                phases.add(new Phase(1, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.666666));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 712.5));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.36666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2016.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2016.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 4375));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.6666666));
                                break;
                        }
                        break;
                    // Long 5K+ plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.6666666));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.6666666));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 275));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(17, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 23:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.666666));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 2083.666666));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.666667));
                                break;
                            case 24:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 635));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.VERY_FAST, 4.8, 5000));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_10K_PLUS_LONG_ID:
                switch (stageNumber) {
                    case 0:
                        // Long 10K+ plan stage 1
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 333.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.666666));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 333.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4052.2));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 2500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 5941.7));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 2000));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 2000));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 452));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 452));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 452));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 10997));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 300));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1576));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.66666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1576));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.66666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1576));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 6528));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                        }
                        break;
                    case 1:
                        // Long 10K+ plan stage 2
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6, 7000));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 766.67));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.53));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.53));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.54));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 452));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.666666));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.6666666));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.666666));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 6977));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    case 2:
                        // Long 10K+ plan stage 3
                        switch (trainingNumber) {
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 800));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 12000));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 300));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2476));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2476));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2476));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 250));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4952));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.666666));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2026));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2026));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1576));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 250));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 291.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.6666666));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 4983.666666));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.666666));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.666666));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 7426));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    case 3:
                        // Long 10K+ plan stage 4
                        switch (trainingNumber) {
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.52));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.52));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.52));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.52));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 675.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 675.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 675.2));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 675.2));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.6666666));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 11875));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1166.666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 766.67));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 901));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 901));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 9, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 901));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.6666666));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 7875.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    case 4:
                        // Long 10K+ plan stage 5
                        switch (trainingNumber) {
                            case 23:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.6666666));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 666.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.7));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.7));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.7));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1012.7));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 24:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 11875));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 25:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 479.1666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                            case 26:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.666666));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.666666));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 27:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 666.6666667));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.VERY_FAST, 4.444444444, 10125.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 333.6666666));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_5K_SHORT_ID:
                switch (stageNumber) {
                    // Short 5K plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 416.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 750));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 3501));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(1, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 225));
                                phases.add(new Phase(16, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(17, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(18, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(19, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(20, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(21, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(22, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 450));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 1750.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 5K plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 600));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(17, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(18, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(19, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(20, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(21, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 175.2));
                                phases.add(new Phase(22, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 2101));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 5K plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 750));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 116.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 750));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 875.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 875.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 875.2));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 150));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 700.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 5, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 2625.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 5K plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1225.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1225.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1225.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1225.2));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 450));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1050.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1050.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1050.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1050.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 50));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1050.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 500));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1400.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1400.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1400.2));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 600));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 3151));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 5K plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 250));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 100));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 2100.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 2100.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 2100.2));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 1750.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 233.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.714285714, 2625.2));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.714285714, 3850.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 200));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.666666667, 150));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 833.34));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 166.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.666666667, 300));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.WALK, 10, 300));
                                phases.add(new Phase(3, Phase.Kind.RUN, Phase.Instruction.FAST_JOG, 5.714285714, 5425.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_10K_SHORT_ID:
                switch (stageNumber) {
                    // Short 10K plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6016.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 458.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 266.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.834));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1250));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.834));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1250));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6016.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1008.34));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 3, 333.34));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 4950));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 10K plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6966.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 550));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 800));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 5500));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 10K plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 3391.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.34));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 6966.666667));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1466.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2383.34));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 5866.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 10K plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 625));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 7498));
                                phases.add(new Phase(1, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.34));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.34));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.34));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 833.34));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1187.5));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.834));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 3020.834));
                                phases.add(new Phase(4, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.FAST_JOG, 5.454545455, 7883.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 10K plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 275));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 23:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.34));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 2083.34));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 24:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.FAST_JOG, 5.454545455, 9900));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_5K_PLUS_SHORT_ID:
                switch (stageNumber) {
                    // Short 5K+ plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3020.834));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 375));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 458.3333333));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(12, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 375));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 1191.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.834));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1145.834));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 395.834));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1145.834));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3020.834));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 5K+ plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 870.834));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 550));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.FAST_JOG, 5.454545455, 183.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 520.834));
                                phases.add(new Phase(14, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 625));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1458.34));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 950));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3541.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 5K+ plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 237.5));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 416.6666667));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6.315789474, 1900));
                                phases.add(new Phase(11, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 266.6666667));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 7333.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 1504.166667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 316.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 3958.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 5K+ plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 275));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 312.5));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(17, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 7125));
                                phases.add(new Phase(1, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2016.666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.454545455, 2016.666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 475));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.8, 4375));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                    // Short 5K+ plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 633.34));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(8, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 1041.666667));
                                phases.add(new Phase(9, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 158.34));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 300));
                                break;
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 333.34));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.8, 2083.34));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6.315789474, 791.6666667));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6.315789474, 635));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.VERY_FAST, 4.8, 5000));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 15, 333.34));
                                break;
                        }
                        break;
                }
                break;
            case PLAN_10K_PLUS_SHORT_ID:
                switch (stageNumber) {
                    // Short 10K+ plan stage 1
                    case 0:
                        switch (trainingNumber) {
                            case 0:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 4, 500));
                                break;
                            case 1:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 2:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 2500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 5941.67));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 2000));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 3:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 2000));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 333.34));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 4:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 10994));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 300));
                                break;
                            case 5:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1575.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1575.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1575.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 6:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 6525.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                        }
                        break;
                    // Short 10K+ plan stage 2
                    case 1:
                        switch (trainingNumber) {
                            case 7:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6, 7000));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 8:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 766.67));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.6));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.6));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 787.6));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 452));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 9:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FAST_WALK, 8.571428571, 408.34));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 1533.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(5, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                            case 10:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 6975.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 10K+ plan stage 3
                    case 2:
                        switch (trainingNumber) {
                            case 11:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2475.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2475.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2475.2));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 250));
                                break;
                            case 12:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 500));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4952));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 13:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 1575.2));
                                phases.add(new Phase(6, Phase.Kind.COOL_DOWN, Phase.Instruction.JOG, 6, 500));
                                phases.add(new Phase(7, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 250));
                                break;
                            case 14:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 291.6666667));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 4983.34));
                                phases.add(new Phase(3, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 15:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 7425.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 10K+ plan stage 4
                    case 3:
                        switch (trainingNumber) {
                            case 16:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.34));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 1000));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(4, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(5, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(6, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 2025.2));
                                phases.add(new Phase(7, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 250));
                                phases.add(new Phase(8, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 17:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 766.67));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 900.3));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 900.3));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 900.3));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 900.3));
                                phases.add(new Phase(10, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 18:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRIAL, Phase.Instruction.VERY_FAST, 4.444444444, 7875.2));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 416.6666667));
                                break;
                        }
                        break;
                    // Short 10K+ plan stage 5
                    case 4:
                        switch (trainingNumber) {
                            case 19:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 15, 300));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.JOG, 6.315789474, 11875));
                                phases.add(new Phase(2, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 15, 500));
                                break;
                            case 20:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(1, Phase.Kind.TRAINING, Phase.Instruction.FAST_JOG, 5.217391304, 479.1666667));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(3, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(5, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(6, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(7, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(8, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(9, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(10, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(11, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(12, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(13, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(14, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(15, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 450.2));
                                phases.add(new Phase(16, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 166.6666667));
                                phases.add(new Phase(17, Phase.Kind.COOL_DOWN, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                            case 21:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.INITIAL_WALK, 12, 333.34));
                                phases.add(new Phase(1, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(2, Phase.Kind.TRAINING, Phase.Instruction.VERY_FAST, 4.444444444, 4050.2));
                                phases.add(new Phase(3, Phase.Kind.RECOVERY, Phase.Instruction.JOG, 6, 833.34));
                                phases.add(new Phase(4, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 500));
                                break;
                            case 22:
                                phases.add(new Phase(0, Phase.Kind.WARM_UP, Phase.Instruction.JOG, 6, 666.6666667));
                                phases.add(new Phase(1, Phase.Kind.RUN, Phase.Instruction.VERY_FAST, 4.444444444, 10126));
                                phases.add(new Phase(2, Phase.Kind.RECOVERY, Phase.Instruction.FINAL_WALK, 12, 333.34));
                                break;
                        }
                        break;
                }
                break;
        }
        return phases;
    }

    /**
     * Resets the app state to the initial value
     */
    public void clear() {
        workout = null;
        selectedPlan = null;
        selectedPlanFocus = null;
        selectedPlanName = null;
        selectedStage = null;
        selectedTraining = null;
        selectedWorkoutType = null;
    }

    /**
     * Returns the plan that has a given title and length
     *
     * @param title  The title of the plan to search
     * @param length The length of the plan to search
     * @return The plan with the given title and length
     */
    public Plan getPlanByTitleAndLength(String title, Plan.Length length) {
        for (Plan plan : plans) {
            if (plan.getTitle().equals(title) && plan.getLength() == length) {
                return plan;
            }
        }

        throw new IllegalStateException("No such plan");
    }

    /**
     * Return the plan which has the given ID
     *
     * @param id The plan id
     * @return The plan which ID corresponds to the parameter given
     */
    public Plan getPlanById(int id) {
        for (Plan plan : plans) {
            if (plan.getId() == id) {
                return plan;
            }
        }

        throw new IllegalStateException("No such plan");
    }

    /**
     * Returns the selected focus: Distance or Speed
     *
     * @return the selected focus
     */
    public Plan.Focus getSelectedPlanFocus() {
        return selectedPlanFocus;
    }

    /**
     * Sets the selected focus: Distance or Speed
     *
     * @param selectedPlanFocus The selected focus
     */
    public void setSelectedPlanFocus(Plan.Focus selectedPlanFocus) {
        this.selectedPlanFocus = selectedPlanFocus;
    }

    /**
     * Returns the selected plan
     *
     * @return The selected plan
     */
    public Plan getSelectedPlan() {
        return selectedPlan;
    }

    /**
     * Sets the selected plan
     *
     * @param selectedPlan The selected plan
     */
    public void setSelectedPlan(Plan selectedPlan) {
        this.selectedPlan = selectedPlan;
    }

    /**
     * Populate the list of purchased products
     */
    private void loadPreviouslySelectedPlans() {
        try {
            FileInputStream fis = context.openFileInput(PreviouslySelectedPlansFilename);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();

            if (readObject instanceof List<?>) {
                previouslySelectedPlans.clear();

                List<?> list = (List<?>) readObject;

                for (Object plan : list) {
                    if (plan instanceof String) {
                        previouslySelectedPlans.add((String) plan);
                    }
                    else {
                        Log.e(TAG, "Cannot cast String");
                    }
                }
            }
            else {
                Log.e(TAG, "Cannot cast List of plans");
            }
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "OutdoorAppState file doesn't exist");
        } catch (ClassNotFoundException ex) {
            Log.e(TAG, "Class not found", ex);
        } catch (IOException ex) {
            Log.e(TAG, "IOException", ex);
        } catch (Exception ex) {
            Log.e(TAG, "Unknown exception", ex);
        }

        Log.i(TAG, "Found: " + previouslySelectedPlans.size() + " previously selected plans");
    }

    /**
     * Save the list of purchased products.
     */
    private void savePreviouslySelectedPlans() {
        if (context == null) {
            throw new NullPointerException("Context is empty for OutdoorAppState save");
        }

        try {
            FileOutputStream fos = context.openFileOutput(PreviouslySelectedPlansFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(previouslySelectedPlans);
            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't save previously selected plans", e);
        }
    }

    /**
     * Save objects to persistent memory.
     */
    public void save() {
        savePreviouslySelectedPlans();

        savePurchasedProducts();
    }

    /**
     * Returns the title of a plan we're interested in (doesn't set any plan yet)
     *
     * @return The title of a plan we're interested
     */
    public String getSelectedPlanName() {
        return selectedPlanName;
    }

    /**
     * Sets the title of the plan we're interested in. Also saves it in the list of previously selected plans.
     *
     * @param selectedPlanName The title of a plan we're interested
     */
    public void setSelectedPlanName(String selectedPlanName) {
        if (!previouslySelectedPlans.contains(selectedPlanName)) {
            previouslySelectedPlans.add(selectedPlanName);
            save();
        }
        this.selectedPlanName = selectedPlanName;
    }

    /**
     * Returns the plan small icon for a given title
     *
     * @param title The title of the plan
     * @return The small icon for the plan
     */
    public int getPlanIcon(String title) {
        switch (title) {
            case PLAN_3K_TITLE:
                return R.mipmap.kilo_3;
            case PLAN_5K_TITLE:
                return R.mipmap.kilo5;
            case PLAN_10K_TITLE:
                return R.mipmap.kilo_10;
            case PLAN_5K_PLUS_TITLE:
                return R.mipmap.kilo5_plus;
            case PLAN_10K_PLUS_TITLE:
                return R.mipmap.kilo_10_plus;
            default:
                throw new IllegalArgumentException("No such plan");
        }
    }

    /**
     * Returns the plan large icon for a given title
     *
     * @param title The title of the plan
     * @return The large icon for the plan
     */
    public int getPlanLargeIcon(String title) {
        switch (title) {
            case PLAN_3K_TITLE:
                return R.drawable.badge_3k;
            case PLAN_5K_TITLE:
                return R.drawable.badge_5k;
            case PLAN_10K_TITLE:
                return R.drawable.badge_10k;
            case PLAN_5K_PLUS_TITLE:
                return R.drawable.badge_5kplus;
            case PLAN_10K_PLUS_TITLE:
                return R.drawable.badge_10kplus;
            default:
                throw new IllegalArgumentException("No such plan");
        }
    }

    /**
     * Returns the plan description for a given title
     *
     * @param title The title of the plan
     * @return The description for the plan
     */
    public int getPlanDescription(String title) {
        switch (title) {
            case PLAN_3K_TITLE:
                return R.string.desc_plan_3k;
            case PLAN_5K_TITLE:
                return R.string.desc_plan_5k;
            case PLAN_10K_TITLE:
                return R.string.desc_plan_10k;
            case PLAN_5K_PLUS_TITLE:
                return R.string.desc_plan_5k_plus;
            case PLAN_10K_PLUS_TITLE:
                return R.string.desc_plan_5k_plus;
            default:
                throw new IllegalArgumentException("No such plan");
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

            if (readObject instanceof ArrayList<?>) {
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
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "IndoorAppState file doesn't exist");
        } catch (ClassNotFoundException ex) {
            Log.e(TAG, "Class not found", ex);
        } catch (IOException ex) {
            Log.e(TAG, "IOException", ex);
        } catch (Exception ex) {
            Log.e(TAG, "Unknown exception", ex);
        }

        Log.i(TAG, "Found: " + purchasedProducts.size() + " products");
    }

    /**
     * Save list of purchased products.
     */
    private void savePurchasedProducts() {
        Log.i(TAG, "Save bought products");

        if (context == null) {
            throw new NullPointerException("Context is empty for IndoorAppState save");
        }

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
     * Gets the purchased products SKU.
     *
     * @return the purchased products SKU
     */
    @NonNull
    public List<String> getPurchasedProducts() {
        return purchasedProducts;
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
     * Gets the selected stage
     *
     * @return the selected stage
     */
    public Stage getSelectedStage() {
        return selectedStage;
    }

    /**
     * Sets the selected stage
     *
     * @param selectedStage The selected stage
     */
    public void setSelectedStage(Stage selectedStage) {
        this.selectedStage = selectedStage;
    }

    /**
     * Gets the selected training
     *
     * @return The selected training
     */
    public Training getSelectedTraining() {
        return selectedTraining;
    }

    /**
     * Sets the selected training
     *
     * @param selectedTraining The selected training
     */
    public void setSelectedTraining(Training selectedTraining) {
        this.selectedTraining = selectedTraining;
        populatePhases();
    }

    /**
     * Populates the phases for the combination of the selected plan, stage and training
     */
    public void populatePhases() {
        if (selectedPlan != null && selectedStage != null && selectedTraining != null) {
            selectedTraining.setPhases(getPhases(selectedPlan.getId(), selectedStage.getSequence(), selectedTraining.getSequence()));
        }
    }

    /**
     * Obtains the percentage of finished trainings of a given stage
     *
     * @param planId  The plan id for the stage
     * @param stage   The stage sequence number
     * @param context The application context
     * @return The percentage of finished trainings of a given stage
     */
    public double stagePercentFinished(int planId, int stage, Context context) {
        final OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        List<Training> stageTrainings = getPlanById(planId).getStage(stage).getTrainings();

        int completedTrainings = outdoorWorkoutsHistoryOperationsDB.getDistinctCompletedTrainings(planId, stage, context);

        double percentageFinished = (double) completedTrainings / (double) (stageTrainings.size());
        Log.i(TAG, String.format("Percentage finished for plan: %d and stage: %d = %s", planId, stage, percentageFinished));
        return percentageFinished;
    }

    /**
     * Returns the percentage of finished stages of a given plan
     *
     * @param planId  The plan id
     * @param context The application context
     * @return The percentage of finished stages of a given plan
     */
    public double planPercentFinished(int planId, Context context) {
        final OutdoorWorkoutsHistoryOperationsDB outdoorWorkoutsHistoryOperationsDB = new OutdoorWorkoutsHistoryOperationsDB();

        List<Stage> stages = getPlanById(planId).getStages();

        int totalPlanTrainings = 0;
        int finishedTrainings = 0;

        for (Stage stage : stages) {
            finishedTrainings += outdoorWorkoutsHistoryOperationsDB.getDistinctCompletedTrainings(planId, stage.getSequence(), context);
            totalPlanTrainings += stage.getTrainings().size();
        }

        if (totalPlanTrainings == 0) throw new IllegalStateException("Plan has no trainings");

        return (double) finishedTrainings / (double) totalPlanTrainings;
    }

    /**
     * Returns the list of previously selected plans
     *
     * @return the list of previously selected plans
     */
    @NonNull
    public List<String> getPreviouslySelectedPlans() {
        return previouslySelectedPlans;
    }

    /**
     * Gets the workout ID for database operations
     *
     * @return The workout ID
     */
    public OutdoorWorkoutsHistory getWorkout() {
        return workout;
    }

    /**
     * Sets the workout for database operations
     *
     * @param workout the workout
     */
    public void setWorkout(@Nullable OutdoorWorkoutsHistory workout) {
        this.workout = workout;
    }

    /**
     * Gets the current workout type
     *
     * @return The current workout type
     */
    public WorkoutType getSelectedWorkoutType() {
        return selectedWorkoutType;
    }

    /**
     * Sets the current workout type
     *
     * @param selectedWorkoutType the current workout type
     */
    public void setSelectedWorkoutType(WorkoutType selectedWorkoutType) {
        this.selectedWorkoutType = selectedWorkoutType;
    }

    /**
     * Returns the finished status of the current training for a given phase
     *
     * @param phase The phase to query for its finished status
     * @return The finished status of the given training's phase
     */
    public FinishedStatus getStageFinishedStatus(int phase) {
        if (selectedTraining == null || selectedStage == null || selectedPlan == null || workout == null) {
            return FinishedStatus.NotStarted;
        }

        GPSRunPointOperationsDB gpsRunPointOperationsDB = new GPSRunPointOperationsDB();
        List<GPSRunPoint> points = gpsRunPointOperationsDB.getPoints(workout.getId(), context);
        if (points.size() == 0) return FinishedStatus.NotStarted;

        long objectiveTimeMillis = 0;
        long ranTimeMillis = 0;
        GPSRunPoint currentPoint = points.get(0);
        //Log.i(TAG, String.format("Listing phases for plan: %d, stage: %d, training: %d, phase: %d", selectedPlan.getId(), selectedStage.getSequence(), selectedTraining.getSequence(), phase));

        List<Phase> phases = getPhases(selectedPlan.getId(), selectedStage.getSequence(), selectedTraining.getSequence());

        int i = 0;
        do {
            objectiveTimeMillis += phases.get(i).getDurationMillis();
            i++;
        } while (i <= phase);

        for (GPSRunPoint runPoint : points) {
            ranTimeMillis += runPoint.getTime().getTime() - currentPoint.getTime().getTime();
            currentPoint = runPoint;
        }

        Log.e(TAG, String.format(Locale.getDefault(), "Phase: %d, Objective time: %s, Ran time: %s", phase, DateUtils.millisToMinSec(objectiveTimeMillis), DateUtils.millisToMinSec(ranTimeMillis)));

        if (ranTimeMillis >= objectiveTimeMillis * App.minimumPacePercentageForCompleteTraining) {
            return FinishedStatus.Finished;
        }

        return FinishedStatus.NotFinished;
    }

    /**
     * The list of different workouts.
     */
    public enum WorkoutType {
        QuickStart(0), ImproveYourself(1), Plans(2);

        private final int value;

        WorkoutType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * The list of different workout status
     */
    public enum WorkoutStatus {
        NotStarted(0), InProgress(1), Finished(2);

        private final int value;

        WorkoutStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
