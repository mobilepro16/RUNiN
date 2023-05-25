package com.runin.runinapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.runin.runinapp.api.APIService
import com.runin.runinapp.api.FcmTokenRequest
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AppFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var apiService: APIService
    var dispose: Disposable? = null

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        dispose?.dispose()
        dispose = apiService.sendFcmToken(FcmTokenRequest(s))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                }, {
                })
    }

    override fun onDestroy() {
        dispose?.dispose()
        super.onDestroy()
    }

}