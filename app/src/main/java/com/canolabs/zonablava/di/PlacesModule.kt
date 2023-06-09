package com.canolabs.zonablava.di

import android.app.Application
import com.canolabs.zonablava.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    @Provides
    @Singleton
    fun providePlacesClient(application: Application): PlacesClient {
        Places.initialize(application, BuildConfig.MAPS_API_KEY)
        return Places.createClient(application)
    }
}