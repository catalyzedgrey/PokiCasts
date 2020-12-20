package com.grey.kotlinpractice.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


class Repository @Inject constructor(private val webservice: ItunesService) {

    private lateinit var subscription: Disposable
    val mutableLiveData: MutableLiveData<Model.Results> by lazy {
        MutableLiveData<Model.Results>()
    }
//    fun getUser(searchQuery: String): LiveData<Model.Results> {
//        // This isn't an optimal implementation. We'll fix it later.
//        val data = MutableLiveData<Model.Results>()
//        webservice.getResults(searchQuery).enqueue(object : Callback<Model.Results> {
//            override fun onResponse(call: Call<Model.Results>, response: Response<Model.Results>) {
//                data.value = response.body()
//            }
//
//            // Error case is left out for brevity.
//            override fun onFailure(call: Call<Model.Results>, t: Throwable) {
//                TODO()
//            }
//        })
//        return data
//    }

    fun getResult(searchQuery: String): LiveData<Model.Results>{
        subscription = webservice.getResults("waypoint", "podcast").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        return mutableLiveData
    }

    private fun onFailure(t: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun onResponse(response: Model.Results) {
        mutableLiveData.value=response
        val data = MutableLiveData<Model.Results>()
        data.value = response

    }

//    fun getResult(srsearch: String): LiveData<Model.Results> {
//        disposable =
//            itunesApiService.getResults(srsearch)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result -> Log.e("beeeeeeept", result.results[0].artworkUrl600) },
//                    { error -> Log.e("beeeeeeeep", error.toString()) }
//                )
//    }
}