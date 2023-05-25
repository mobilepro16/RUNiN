package com.runin.runinapp.utils

import android.content.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.runin.runinapp.App
import com.runin.runinapp.api.APIService
import com.runin.runinapp.api.FcmTokenRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object AuthUtils {
    fun saveAccessToken(sharedPref: SharedPreferences, apiService: APIService, onSuccess: (() -> Unit)? = null, onError: (() -> Unit)? = null) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnCompleteListener {
            if (it.isSuccessful) {
                sharedPref.putString(App.accessToken, it.result?.token ?: "")
                FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                onError?.invoke()
                                return@OnCompleteListener
                            }
                            val dispose = apiService.sendFcmToken(FcmTokenRequest(task.result?.token
                                    ?: ""))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe({
                                        onSuccess?.invoke()
                                    }, {
                                        onError?.invoke()
                                    })

                        })
            } else {
                onError?.invoke()
            }
        }
    }
}