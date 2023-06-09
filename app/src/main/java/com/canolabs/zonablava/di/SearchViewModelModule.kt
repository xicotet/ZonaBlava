package com.canolabs.zonablava.di

import com.canolabs.zonablava.ui.search.SearchViewModel
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // or FragmentComponent, depending on your setup
object SearchViewModelModule {
    @Provides
    @Singleton
    fun provideSearchViewModel(placesClient: PlacesClient): SearchViewModel {
        return SearchViewModel(placesClient)
    }
}