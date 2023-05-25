package com.runin.runinapp

import android.app.Application
import android.util.Log
import com.facebook.appevents.AppEventsLogger
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.runin.runinapp.data.IndoorAppState
import com.runin.runinapp.data.OutdoorAppState
import com.runin.runinapp.di.DaggerAppComponent
import com.runin.runinapp.utils.TTS
import com.runin.runinapp.utils.TTS.InitCallback
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


/**
 * Globals
 * Created by Cesar on 12/08/2016
 */
class App : Application(), InitCallback, HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private var outdoorAppState: OutdoorAppState? = null
    private var indoorAppState: IndoorAppState? = null
    private var tts: TTS? = null

    /**
     * Gets the Application State
     *
     * @return the app state
     */
    fun getIndoorAppState(): IndoorAppState {
        checkNotNull(indoorAppState) { "indoorAppState is null" }
        return indoorAppState!!
    }

    /**
     * Gets the Outdoor App State
     *
     * @return the app state
     */
    fun getOutdoorAppState(): OutdoorAppState {
        checkNotNull(outdoorAppState) { "outdoorAppState is null" }
        return outdoorAppState!!
    }


    override fun androidInjector() = dispatchingAndroidInjector
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        // Load and print the IndoorAppState
        indoorAppState = IndoorAppState(this.applicationContext)
        Log.i(TAG, indoorAppState.toString())
        outdoorAppState = OutdoorAppState(this.applicationContext)
        Log.i(TAG, outdoorAppState.toString())
        // Load TextToSpeech
        tts = TTS(this, this)
        AppEventsLogger.activateApp(this)

        DaggerAppComponent.builder().application(this).build().inject(this)

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        FirebaseApp.initializeApp(this)
    }

    /**
     * Text to Speech
     *
     * @param stringToRead What to say
     */
    fun speakOut(stringToRead: String) {
        if (tts != null) {
            Log.i(TAG, "Reading: $stringToRead")
            tts!!.speak(stringToRead)
        }
    }

    /**
     * Text to Speech but cleans the queue
     *
     * @param stringToRead What to say
     */
    fun speakOutFlush(stringToRead: String) {
        if (tts != null) {
            Log.i(TAG, "Reading (flush): $stringToRead")
            tts!!.speakFlush(stringToRead)
        }
    }

    /**
     * Interrupts the current utterance and discards others in queue.
     */
    fun cancelSpeak() {
        if (tts != null) {
            Log.i(TAG, "Cancel reading")
            tts!!.stop()
        }
    }

    /**
     * For TTS
     */
    override fun initSuccess(tts: TTS) {
        Log.i(TAG, "TTS Enabled")
    }

    /**
     * For TTS
     */
    override fun initFail(reason: Int) {
        tts = null
    }

    enum class LengthUnit(val value: Int) {
        Meter(0), Kilometer(1);

    }

    companion object {
        //Shared Preferences
        const val sharedPreferencesFile = "com.runin.runinapp.preferences_app_runin"
        const val sharedPreferencesPropertyAdsFreeStatus = "adFreeStatus"
        const val sharedPreferencesPropertyMusicPlayer = "musicPlayer"
        const val sharedPreferencesPropertyTutorialVisited = "tutorial"
        const val sharedPreferencesPropertyProfileSaved = "savedDataProfile"
        const val sharedPreferencesPropertyTypeOfRunner = "typeOfRunner"
        const val sharedPreferencesUserProfile = "userProfile"
        const val sharedPreferencesIndoorLevel = "indoorLevel"
        const val sharedPreferencesIndoorSpeedLevel = "speedLevel"
        const val sharedPreferencesBadgeStatus = "badgeStatus"
        const val sharedPreferencesIndoorLevelSelectedAndConfirmed = "indoorLevelSelectedAndConfirmed"
        const val googlePlaySkuAddFree = "adFree"
        const val adFreePurchased = "hide"
        const val adFreeNotPurchased = "show"
        const val extrasConfirmIndoorLevel = "confirmIndoorLevel"
        const val extrasCurrentSegment = "currentSegment"
        const val extrasEdit = "edit"
        const val extrasElapsedTimeMilliSeconds = "timeMilliSeconds"
        const val extrasFragment = "fragment"
        const val extrasFrom = "from"
        const val extrasImproveYourselfFocus = "focus"
        const val extrasImproveYourselfMeasure = "measure"
        const val extrasImproveYourselfObjectivePace = "recommendedPace"
        const val extrasImproveYourselfObjectiveTimeMillis = "timeIMP"
        const val extrasImproveYourselfObjectiveDistanceKm = "distancesRun"
        const val extrasMaxSpeedKmPerHour = "maxSpeed"
        const val extrasPaceMinPerKm = "pace"
        const val extrasPlansElapsedTimeMilliSeconds = "timeR"
        const val extrasScreenName = "screenName"
        const val extrasSettingsAboutUs = "aboutUs"
        const val extrasSettingsHelp = "help"
        const val extrasSettingsPrivacy = "privacy"
        const val extrasSettingsTerms = "terms"
        const val extrasSpeedKmPerHour = "speed"
        const val extrasTotalDistanceKm = "distance"
        const val extrasShowFinishButton = "extrasShowFinishButton"
        const val extrasCurrentRunningPhase = "extrasCurrentRunningPhase"
        const val screenNameBadges = "badges"
        const val screenNameImprove = "improve"
        const val screenNamePlansMain = "main"
        const val screenNamePlans = "plans"
        const val screenNameQuick = "quick"
        const val screenNameSettings = "settings"
        const val screenNameTracks = "tracks"
        const val screenNameTutorials = "tutorialsScreen"
        const val runnerTypeIndoor = "indoor"
        const val runnerTypeOutdoor = "outdoor"
        const val runnerTypeQuickstart = "quickstart"
        const val focusCalories = "calories"
        const val focusDistance = "distance"
        const val measureMeters = "meters"
        const val measureKilometers = "kilometers"
        const val maximumPacePercentageForEquality = 0.125
        const val minimumPacePercentageForCompleteStage = 0.6
        const val minimumPacePercentageForCompleteTraining = 0.85
        const val accessToken = "accessToken"

        // Private vars
        private val TAG = App::class.java.simpleName
    }


}