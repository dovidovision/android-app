package com.example.meow_diary

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable

interface API {
    @POST("/image")
    fun uploadImage(
        @Body catImage: CatImage
    ): Call<CatImage>

    @GET("/hello")
    fun getHello() :Call<CatImage>
}

