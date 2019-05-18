package com.example.moviereview.Retrofit


import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance (private val context: Context) {
    private var retrofit: Retrofit? = null
    private val MY_URL = "https://my-json-server.typicode.com/SambathPich/SampleRESTFulAPI/"

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(CachingInterceptor(context))
        .build()

    fun myJSONInfo(): Retrofit? {
        if (retrofit == null) {
            retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(MY_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }


}
