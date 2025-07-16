package com.example.testtaskworkmate.data.di

import com.example.testtaskworkmate.data.network.RamApi
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.repos.RamRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

private const val BASE_URL = "https://rickandmortyapi.com/api/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()

    @Provides
    @Singleton
    fun provideRamApi(retrofit: Retrofit): RamApi = retrofit.create(RamApi::class.java)

    @Provides
    @Singleton
    fun provideRamRepository(api: RamApi): RamRepository = RamRepositoryImpl(api)

}
