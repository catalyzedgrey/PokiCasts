package com.grey.kotlinpractice.di.component

import com.grey.kotlinpractice.di.module.NetworkModule
import com.grey.kotlinpractice.HomeViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelComponent {
    /**
     * Injects required dependencies into the specified HomeViewModel.
     * @param homeViewModel HomeViewModel in which to inject the dependencies
     */
    fun inject(homeViewModel: HomeViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelComponent

        fun networkModule(networkModule: NetworkModule): Builder
    }
}