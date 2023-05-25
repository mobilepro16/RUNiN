package com.runin.runinapp.di

import com.runin.runinapp.AppFirebaseMessagingService
import com.runin.runinapp.LoginActivity
import com.runin.runinapp.SignUpActivity
import com.runin.runinapp.SplashScreenActivity
import com.runin.runinapp.indoor.IndoorDashboardActivity
import com.runin.runinapp.outdoor.OutdoorDashboardActivity
import com.runin.runinapp.settings.ProfileActivity
import com.runin.runinapp.tutorial.TutorialActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ContributesBuilder {

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun contributeProfileActivity(): ProfileActivity

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashScreenActivity

    @ContributesAndroidInjector
    abstract fun contributeSignUpActivity(): SignUpActivity

    @ContributesAndroidInjector
    abstract fun contributeIndoorDashboardActivity(): IndoorDashboardActivity

    @ContributesAndroidInjector
    abstract fun contributeAppFirebaseMessagingService(): AppFirebaseMessagingService

    @ContributesAndroidInjector
    abstract fun contributeTutorialActivity(): TutorialActivity

    @ContributesAndroidInjector
    abstract fun contributeOutdoorDashboardActivity(): OutdoorDashboardActivity


}