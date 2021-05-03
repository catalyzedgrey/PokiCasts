package com.grey.kotlinpractice.data

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ItunesService {

    @GET("search?")
    fun getResults(
        @Query("term") term: String,
        @Query("media") media: String ="podcast"): Observable<Model.Results>

    @GET("lookup?")
    fun getUpdatedPodcast(
        @Query("id") collectionId: String
    ): Observable<Model.Results>


//    companion object {
//        fun create(): ItunesService {
//            val retrofit = Retrofit.Builder()
//                .addCallAdapterFactory(
//                    RxJava2CallAdapterFactory.create())
//                .addConverterFactory(
//                    GsonConverterFactory.create())
//                .baseUrl("https://itunes.apple.com/")
//                .build()
//
//            return retrofit.create(ItunesService::class.java)
//        }
//    }
}