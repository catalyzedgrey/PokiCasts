package com.grey.kotlinpractice.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tw.ktrssreader.Reader
import tw.ktrssreader.model.channel.*
import javax.inject.Inject


class Repository @Inject constructor(private val webservice: ItunesService) {

    private lateinit var subscription: Disposable
    val podcastDao = DatabaseHandler.db.podcastDao()
    val episodeDao = DatabaseHandler.db.episodeDao()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val podcastSearchResultLiveData: MutableLiveData<Model.Results> by lazy {
        MutableLiveData<Model.Results>()
    }


    private val remoteEpisodeListLiveData: MutableLiveData<ITunesChannelData> by lazy {
        MutableLiveData<ITunesChannelData>()
    }

    private val subscribedPodcastListLiveData: MutableLiveData<List<Model.Podcast>> by lazy {
        MutableLiveData<List<Model.Podcast>>()
    }

    val episodeLiveList: MutableLiveData<List<Model.Episode>> by lazy {
        MutableLiveData<List<Model.Episode>>()
    }


    fun searchForPodcast(searchQuery: String): MutableLiveData<Model.Results> {
        if (searchQuery != "") {
            subscription =
                webservice.getResults(searchQuery, "podcast").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
        }
        return podcastSearchResultLiveData
    }


//    fun getEpisodesList(feedUrl: String): MutableLiveData<List<Model.Episode>> {
//        coroutineScope.launch {
//            val pod = podcastDao.findByFeedUrl(feedUrl)
//            val m: ITunesChannelData = Reader.read<ITunesChannelData>(pod.feedUrl)
//
//            val episodes: List<Model.Episode> =
//                episodeDao.transformItunesDatatoEpisode(m.items!!, pod.uid, pod.collectionName!!)
//            episodeDao.insertAll(episodes)
//            episodeLiveList.postValue(episodes)
//        }
//        return episodeLiveList
//
//    }

    fun getSubscribedPodcastList(): MutableLiveData<List<Model.Podcast>> {
        coroutineScope.launch {
            subscribedPodcastListLiveData.postValue(podcastDao.getAll())
        }
        return subscribedPodcastListLiveData
    }

    fun getEpisodeListLocally(feedUrl: String): MutableLiveData<List<Model.Episode>> {
        if (feedUrl != "") {
            coroutineScope.launch {
                val pod = podcastDao.findByFeedUrl(feedUrl)
                val m: ITunesChannelData = Reader.read<ITunesChannelData>(pod.feedUrl)

                val episodes: List<Model.Episode> =
                    episodeDao.transformItunesDatatoEpisode(
                        m.items!!,
                        pod.uid,
                        pod.collectionName!!
                    )
                episodeDao.insertAll(episodes)
                episodeLiveList.postValue(episodes)
            }
        }
        return episodeLiveList
    }

    fun getEpisodeListRemotely(feedUrl: String): MutableLiveData<ITunesChannelData> {
        if (feedUrl != "") {
            coroutineScope.launch {
                remoteEpisodeListLiveData.postValue(
                    Reader.read<ITunesChannelData>(feedUrl)
                )
            }
        }
        return remoteEpisodeListLiveData
    }

    fun unsubscribeAndDeletePodcast(feedUrl: String) {
        coroutineScope.launch {
            podcastDao.delete(podcastDao.findByFeedUrl(feedUrl))
            subscribedPodcastListLiveData.postValue(podcastDao.getAll())
        }
    }

    fun insertPodcastOnSubscribe(podcast: Model.Podcast) {
        coroutineScope.launch {
            podcast.isSubscribed = true
            podcastDao.insertAll(podcast)
            subscribedPodcastListLiveData.postValue(podcastDao.getAll())
        }
    }


    private fun onFailure(t: Throwable?) {
        //TODO("Not yet implemented")
        Log.e("error", t?.printStackTrace().toString())
    }

    private fun onResponse(response: Model.Results) {
        podcastSearchResultLiveData.value = response
    }


    fun deleteAllPodcastItems() {
        coroutineScope.launch {
            podcastDao.deleteAll()
        }
    }


    fun dispose() {


        //subscription.dispose()
    }


    object DatabaseHandler {
        var db: AppDatabase = AppDatabase.DatabaseProvider.getInstance()
        private val coroutineScope = CoroutineScope(Dispatchers.IO)
    }
}