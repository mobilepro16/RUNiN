package com.runin.runinapp.api

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class BadgeRequest(var badge: String = "")
data class FcmTokenRequest(var fcmToken: String = "")
data class UserRequest(var birthDate: String = "2020-06-26T08:44:34+0000",
                       var name: String = "",
                       var weight: String = "",
                       var height: String = "",
                       var gender: String = ""
) {
    fun setBirthDate(date: Date?) {
        date?.let {
            birthDate = formatter.format(it)
        }
    }

    companion object {
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
    }
}