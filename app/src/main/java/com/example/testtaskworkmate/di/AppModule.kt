package com.example.testtaskworkmate.di

import android.content.Context
import androidx.room.Room
import com.example.testtaskworkmate.data.api.ApiService
import com.example.testtaskworkmate.data.repos.RamRepository
import com.example.testtaskworkmate.data.repos.RamRepositoryImpl
import com.example.testtaskworkmate.data.source.local.AppDatabase
import com.example.testtaskworkmate.data.source.local.CharacterDao
import com.example.testtaskworkmate.data.source.network.NetworkRepository
import com.example.testtaskworkmate.data.source.network.NetworkRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideRetrofit(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideRamApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRamRepository(networkRepo: NetworkRepository, dao: CharacterDao): RamRepository =
        RamRepositoryImpl(networkRepository = networkRepo, characterDao = dao)

    @Provides
    @Singleton
    fun provideNetworkRepository(api: ApiService): NetworkRepository = NetworkRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Provides
    fun provideCharacterDao(database: AppDatabase) = database.characterDao
}
