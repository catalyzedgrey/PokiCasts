package com.grey.kotlinpractice

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.grey.kotlinpractice.data.*
import com.grey.kotlinpractice.di.component.DaggerViewModelComponent
import com.grey.kotlinpractice.di.component.ViewModelComponent
import kotlinx.coroutines.*
import tw.ktrssreader.model.channel.ITunesChannelData
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {
    @Inject
    lateinit var itunesService: ItunesService

    private val injector: ViewModelComponent = DaggerViewModelComponent.builder()
        .networkModule(com.grey.kotlinpractice.di.module.NetworkModule).build()
    private var repository: Repository


    //private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var isPlayerfExpanded: Boolean = false
    var isEpisodePreviewExpanded: Boolean = false

    var currentEpisode: Model.Episode? = null

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

    fun searchForPodcast(s: String): LiveData<Model.Results> {
        return repository.searchForPodcast(s)
    }


    fun getSubscribedPodcasts(): LiveData<List<Model.Podcast>> {


        return repository.getSubscribedPodcastList()
    }

    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
        repository.dispose()
    }


//    fun getLastPlayedEpisode(): LiveData<Model.CurrentEpisode> {
//        return repository.getLastPlayerPodcastInfo()
//    }


    fun subscribeToPodcast(pod: Model.Podcast) {
        repository.insertPodcastOnSubscribe(pod)
//        val podcastDao = Repository.DatabaseHandler.db.podcastDao()
//        coroutineScope.launch {
//            subscribedPodList.postValue(podcastDao.getAll())
//        }
    }

    fun getLocalEpisodeList(podId: Int): LiveData<List<Model.Episode>> {
        repository.episodeLiveList.value = null
        return repository.getEpisodeListLocally(podId)
    }

    fun getRemoteEpisodeList(url: String): LiveData<ITunesChannelData> {
        return repository.getEpisodeListRemotely(url)
    }

    fun unsubscribe(feedUrl: String) {
        repository.unsubscribeAndDeletePodcast(feedUrl)
    }


    suspend fun getPod(url: String): Model.Podcast = withContext(Dispatchers.IO) {
        // do your network request logic here and return the result
        repository.podcastDao.findByName(url)
    }

    fun getEpisodeByFeed(feedUrl: String): LiveData<Model.Episode> {
        return repository.getEpisodeByFeedUrl(feedUrl)
    }

    fun saveLastPlayedPodcastInfo(episode: Model.Episode) {

        repository.saveLastPlayedPodcastInfo(episode)
    }

    fun updateEpisode() {
        if (currentEpisode != null)
            repository.updateEpisode(currentEpisode!!)
    }

//    fun saveLastPlayedPodcastInfo(episode: Model.CurrentEpisode) {
//        repository.saveLastPlayedPodcastInfo(episode)
//    }


}