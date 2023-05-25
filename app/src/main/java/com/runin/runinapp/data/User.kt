package com.runin.runinapp.data

import com.google.gson.Gson
import com.runin.runinapp.api.UserRequest
import com.runin.runinapp.utils.DateUtils
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class User {

    var id = ""
    var birthDate = ""
    var name = ""
    var weight = 0f
    var height = 0f
    var gender = 0 /*Male(0),Female(1);*/
    var profilePicture = ""

    fun getHeightString(): String {
        return String.format(Locale.getDefault(), "%.0f", height)
    }

    fun getWeightString(): String {
        return String.format(Locale.getDefault(), "%.0f", weight)
    }
    var tempPhotoFile: File? = null

    fun getAge(): Int {
        val date = getBirthDateInDate() ?: return 0
        val calendar = Calendar.getInstance()
        calendar.time = date
        return DateUtils.getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
    }

    private fun getBirthDateInDate(): Date? {
        return try {
            UserRequest.formatter.parse(birthDate)
        } catch (ex: ParseException) {
            null
        }
    }

    fun getBirthDateInView(): String {
        val date = getBirthDateInDate() ?: return ""
        return viewFormatter.format(date)
    }

    fun setBirthDate(calendar: Calendar) {
        birthDate = UserRequest.formatter.format(calendar.time)
    }

    fun clone(): User {
        val stringProject = gson.toJson(this, User::class.java)
        return gson.fromJson<User>(stringProject, User::class.java)
    }

    fun copy(user: User) {
        this.name = user.name
        this.birthDate = user.birthDate
        this.weight = user.weight
        this.height = user.height
        this.gender = user.gender
    }

    companion object {
        private val gson = Gson()
        private val viewFormatter: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
}