package com.thomas.doorbell.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.thomas.doorbell.data.RealtimeStreamingClient
import com.thomas.doorbell.data.RealtimeStreamingClientImpl
import com.thomas.doorbell.keystore.UserData
import com.thomas.doorbell.keystore.UserDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    @Singleton
    @Provides
    fun provideRealtimeStreamingClient(httpClient: HttpClient): RealtimeStreamingClient
        = RealtimeStreamingClientImpl(httpClient)

    @Singleton
    @Provides
    fun provideDataStore(
        @ApplicationContext context: Context,
        userDataSerializer: UserDataSerializer
    ): DataStore<UserData>
        = DataStoreFactory.create(
            serializer = userDataSerializer,
            produceFile = {
                context.dataStoreFile("doorbell_user_prefs.json")
            }
        )

}