package com.runin.runinapp.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.runin.runinapp.App
import com.runin.runinapp.data.User


fun SharedPreferences.putString(key: String, value: String) {
    edit().apply {
        putString(key, value)
        apply()
    }

}

fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    edit().apply {
        putBoolean(key, value)
        apply()
    }
}

fun SharedPreferences.saveUser(currentUser: User, newUser: User) {
    currentUser.copy(newUser)
    edit().apply {
        putString(App.sharedPreferencesUserProfile, Gson().toJson(newUser))
        apply()
    }
}

fun SharedPreferences.getUser(): User {
    return Gson().fromJson(getString(App.sharedPreferencesUserProfile, ""), User::class.java)
            ?: User()
}