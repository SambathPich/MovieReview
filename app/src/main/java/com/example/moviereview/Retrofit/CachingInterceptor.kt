package com.example.moviereview.Retrofit

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class CachingInterceptor (context: Context): Interceptor {


    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) {
            Log.d("INTERNET_STATUS", "NO INTERNET")
            return chain.proceed(chain.request())

        } else {
            Log.d("INTERNET_STATUS", "YES THERE IS INTERNET")
            return chain.proceed(chain.request())
        }

    }

    private fun isOnline(): Boolean {
        val connectivityManager = appContext.getSystemService((Context.CONNECTIVITY_SERVICE)) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}