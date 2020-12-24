package com.grey.kotlinpractice.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grey.kotlinpractice.data.ItunesService
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.data.Repository
import com.grey.kotlinpractice.data.Repository_Factory
import com.grey.kotlinpractice.di.component.DaggerViewModelComponent
import com.grey.kotlinpractice.di.component.ViewModelComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class HomeViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var itunesService: ItunesService

    private val injector: ViewModelComponent = DaggerViewModelComponent.builder()
        .networkModule(com.grey.kotlinpractice.di.module.NetworkModule).build()
    private lateinit var subscription: Disposable

    //@Inject
    lateinit var repository: Repository

//    val currentName: MutableLiveData<Model.Results> by lazy {
//        MutableLiveData<Model.Results>()
//    }
    lateinit var  data : LiveData<Model.Results>


    var disposable: Disposable? = null

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

//        subscription = itunesService.getResults("waypoint", "podcast").subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })


    }


    //    fun getResults(): LiveData<Model.Results> {
//
////        return Repository.
//    }
    override fun onCleared() {
        super.onCleared()
        // Dispose All your Subscriptions to avoid memory leaks
        subscription?.dispose()
    }


}