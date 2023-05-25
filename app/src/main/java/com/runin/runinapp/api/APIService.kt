package com.runin.runinapp.api

import com.runin.runinapp.data.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface APIService {

    @Multipart
    @POST("user/uploadAvatar")
    fun updateAvatar(@Part image: MultipartBody.Part/*, @Part("file") name: RequestBody*/): Observable<User>

    @GET("user/info")
    fun getUserInfo(): Observable<User>

    @PUT("user/update")
    fun updateUser(@Body updateInfo: UserRequest): Observable<User>

    @POST("user/register")
    fun registerUser(@Body registerInfo: UserRequest): Observable<User>

    @GET("user/badges")
    fun getUserBadges(): Observable<Any>

    @PUT("user/badges")
    fun updateUserBadges(@Body badgeRequest: BadgeRequest): Observable<Any>

    @POST("user/fcmToken")
    fun sendFcmToken(@Body fcmTokenRequest: FcmTokenRequest): Observable<Any>

    @GET("badges")
    fun getAllBadges(): Observable<Any>
}