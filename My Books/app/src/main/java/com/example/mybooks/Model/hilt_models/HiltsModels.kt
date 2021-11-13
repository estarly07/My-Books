package com.example.mybooks.Model.hilt_models

import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Models.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltsModels {

    @Singleton
    @Provides
    fun providerSharedPreferences(): SharedPreferences {
        return SharedPreferences()
    }
}