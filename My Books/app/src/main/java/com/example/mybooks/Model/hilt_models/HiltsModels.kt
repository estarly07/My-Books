package com.example.mybooks.Model.hilt_models

import android.content.Context
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Model.socket.ServerSocket
import com.example.mybooks.Model.socket.SocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun providerUseCase(@ApplicationContext context:Context):UseCase{
        return UseCase(context = context)
    }
   
    @Provides
    fun providerServerSocket(useCase: UseCase):ServerSocket{
        return ServerSocket(useCase)
    }

    @Provides
    fun providerSocketClient(useCase: UseCase):SocketClient = SocketClient(useCase)

}