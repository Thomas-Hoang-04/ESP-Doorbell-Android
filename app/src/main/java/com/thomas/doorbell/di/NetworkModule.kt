package com.thomas.doorbell.di

import com.thomas.doorbell.network.APIInterface
import com.thomas.doorbell.network.AuthInterceptor
import com.thomas.doorbell.network.AuthInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // TODO: Implement base URL here
    private const val BASE_URL = ""

    private fun createClientBase(): OkHttpClient.Builder
        = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)

    @Provides
    @Singleton
    fun provideAuthInterface(): AuthInterface
        = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClientBase().build())
            .build()
            .create(AuthInterface::class.java)

    @Provides
    @Singleton
    fun provideAPIInterface(
        authInterceptor: AuthInterceptor
    ): APIInterface
        = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                createClientBase()
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .build()
            .create(APIInterface::class.java)

}