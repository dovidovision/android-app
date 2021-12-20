package com.example.meow_diary

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application() {
    lateinit var service : API

    override fun onCreate() {
        super.onCreate()

    }

    fun createRetrofit(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://118.67.129.17:6019/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(API::class.java)
    }
}