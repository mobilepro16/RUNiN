package com.runin.runinapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.runin.runinapp.App
import com.runin.runinapp.api.APIService
import com.runin.runinapp.utils.getUser
import com.runin.runinapp.utils.putString
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Singleton

const val BASE_URL = "https://us-central1-runin-eaad2.cloudfunctions.net/api/"

@Module
class AppModule {
    @Provides
    @Singleton
    internal fun provideApplication(application: Application): Context = application

    @Provides
    @Singleton
    internal fun provideUser(sharedPreference: SharedPreferences) = sharedPreference.getUser()

    @Provides
    @Singleton
    internal fun provideAppSharedPreference(application: Application): SharedPreferences {
        val preferencesFile = App.sharedPreferencesFile
        return application.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun provideApi(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideHttpClient(sharedPreference: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor {
                    val request: Request = it.request()
                    val response: Response = it.proceed(request)
                    // refresh token if needed
                    if (response.code() == 401) {
                        try {
                            val tokenTask = FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                            tokenTask?.let { task ->
                                val result = Tasks.await(task)
                                result.token?.let { token ->
                                    Timber.d("AppModule provideHttpClient refresh $token")
                                    sharedPreference.putString(App.accessToken, token)
                                    response.close()
                                    addTokenToHeader(it, sharedPreference, true)
                                }
                            }
                        } catch (ex: ExecutionException) {
                            Timber.d("AppModule provideHttpClient $ex")
                        } catch (ex: InterruptedException) {
                            Timber.d("AppModule provideHttpClient $ex")
                        }
                    }
                    response

                }
                .addNetworkInterceptor {
                    addTokenToHeader(it, sharedPreference)
                }
                .addNetworkInterceptor(StethoInterceptor())
                .build()
    }

    private fun addTokenToHeader(chain: Interceptor.Chain, sharedPreference: SharedPreferences, shouldRemoveOldHeader: Boolean = false): Response {
        val requestBuilder = chain.request().newBuilder()
        if (shouldRemoveOldHeader) {
            requestBuilder.removeHeader("Authorization")
        }
        requestBuilder.addHeader("Authorization", "Bearer " + sharedPreference.getString(App.accessToken, ""))
        return chain.proceed(requestBuilder.build())
    }

    @Provides
    @Singleton
    internal fun provideRetrofitInterface(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}