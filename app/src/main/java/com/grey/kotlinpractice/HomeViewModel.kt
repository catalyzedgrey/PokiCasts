package com.grey.kotlinpractice

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.grey.kotlinpractice.data.ItunesService
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.data.Repository
import com.grey.kotlinpractice.data.Repository_Factory
import com.grey.kotlinpractice.di.component.DaggerViewModelComponent
import com.grey.kotlinpractice.di.component.ViewModelComponent
import io.reactivex.disposables.Disposable
import tw.ktrssreader.model.channel.ITunesChannelData
import javax.inject.Inject


class HomeViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var itunesService: ItunesService

    private val injector: ViewModelComponent = DaggerViewModelComponent.builder()
        .networkModule(com.grey.kotlinpractice.di.module.NetworkModule).build()

    lateinit var repository: Repository

    lateinit var  data : LiveData<Model.Results>
    var  rssData : LiveData<ITunesChannelData>? = null

    init {
        inject()
        repository = Repository_Factory.newInstance(itunesService)
        loadResults("")
    }

    private fun inject() {
        when (this) {
            is HomeViewModel -> injector.inject(this)
        }
    }

    public fun loadResults(s: String) {
        data = repository.getResult(s)
    }

    fun getXMLResult(index: Int){
        rssData = repository.getXMLResult(index)
    }

}