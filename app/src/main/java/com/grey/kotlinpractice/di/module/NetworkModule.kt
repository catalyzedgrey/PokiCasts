package com.grey.kotlinpractice.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.grey.kotlinpractice.data.ItunesService
import com.grey.kotlinpractice.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provedItunesApi(retrofit: Retrofit): ItunesService {
        return retrofit.create(ItunesService::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    fun provideRetrofitInterface(): Retrofit {

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

    }
}