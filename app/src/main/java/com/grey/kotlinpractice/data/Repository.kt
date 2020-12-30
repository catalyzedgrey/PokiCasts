package com.grey.kotlinpractice.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tw.ktrssreader.Reader
import tw.ktrssreader.model.channel.*
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


class Repository @Inject constructor(private val webservice: ItunesService) {


    private lateinit var subscription: Disposable
    private val mutableLiveData: MutableLiveData<Model.Results> by lazy {
        MutableLiveData<Model.Results>()
    }

    private val mutableRSSData: MutableLiveData<ITunesChannelData> by lazy {
        MutableLiveData<ITunesChannelData>()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
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

    fun getResult(searchQuery: String): MutableLiveData<Model.Results> {
        subscription = webservice.getResults(searchQuery, "podcast").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        return mutableLiveData
    }

    fun getXMLResult(index: Int): MutableLiveData<ITunesChannelData> {
        coroutineScope.launch {
//            val result: RssStandardChannelData =
//                Reader.read<RssStandardChannelData>(mutableLiveData.value!!.results[index].feedUrl)
//            result.items?.size

            mutableRSSData.postValue(
                Reader.read<ITunesChannelData>(mutableLiveData.value!!.results[index].feedUrl)
            )

            Log.v("XML", "xml result from repo--------")
        }
        return mutableRSSData


    }

    private fun onFailure(t: Throwable?) {
        //TODO("Not yet implemented")
        Log.e("error", t?.printStackTrace().toString())
    }

    private fun onResponse(response: Model.Results) {
        mutableLiveData.value = response
//        val data = MutableLiveData<Model.Results>()
//        data.value = response

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