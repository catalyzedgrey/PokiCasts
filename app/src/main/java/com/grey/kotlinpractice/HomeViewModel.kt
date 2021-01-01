package com.grey.kotlinpractice

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grey.kotlinpractice.data.*
import com.grey.kotlinpractice.di.component.DaggerViewModelComponent
import com.grey.kotlinpractice.di.component.ViewModelComponent
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tw.ktrssreader.model.channel.ITunesChannelData
import javax.inject.Inject


class HomeViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var itunesService: ItunesService

    private val injector: ViewModelComponent = DaggerViewModelComponent.builder()
        .networkModule(com.grey.kotlinpractice.di.module.NetworkModule).build()

    var repository: Repository
    lateinit var  data : LiveData<Model.Results>
    var  rssData : LiveData<ITunesChannelData>? = null

    lateinit var subscribedPodList: LiveData<List<Model.Podcast>>


    var disposable: Disposable? = null
    //private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        inject()
        repository = Repository_Factory.newInstance(itunesService)
        getSubscribedPodcasts()

    }

    private fun inject() {
        when (this) {
            is HomeViewModel -> injector.inject(this)
        }
    }

    public fun loadResults(s: String) {
        data = repository.getResult(s)

//        subscription = itunesService.getResults("waypoint", "podcast").subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })


    }

    fun getSubscribedPodcasts(){

        subscribedPodList = repository.getAllSubscribed()
    }

    fun getXMLResult(index: Int){
        rssData = repository.getXMLResult(index)
    }


    //    fun getResults(): LiveData<Model.Results> {
//
////        return Repository.
//    }
    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
        repository.dispose()
    }


    fun subscribeToPodcast(pod: Model.Podcast){
        repository.insertPodcastOnSubscribe(pod)
    }

}