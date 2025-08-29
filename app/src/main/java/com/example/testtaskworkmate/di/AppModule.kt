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

// Базовый URL для всех запросов к API..
private const val BASE_URL = "https://rickandmortyapi.com/api/"

// Hilt-модуль для предоставления зависимостей уровня приложения..
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    // Предоставляет единственный экземпляр Retrofit для всего приложения..
    fun provideRetrofit(): Retrofit {
        // Настраиваем парсер JSON, чтобы он игнорировал неизвестные поля..
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            // Добавляем конвертер для Kotlinx Serialization, чтобы Retrofit мог парсить JSON..
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            // Устанавливаем базовый URL..
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    // Создает реализацию нашего ApiService интерфейса с помощью Retrofit..
    fun provideRamApi(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    // Предоставляет реализацию основного репозитория..
    fun provideRamRepository(networkRepo: NetworkRepository, dao: CharacterDao): RamRepository =
        RamRepositoryImpl(networkRepository = networkRepo, characterDao = dao)

    @Provides
    @Singleton
    // Предоставляет реализацию сетевого репозитория..
    fun provideNetworkRepository(api: ApiService): NetworkRepository = NetworkRepositoryImpl(api)

    @Provides
    @Singleton
    // Создает единственный экземпляр базы данных Room для приложения..
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Provides
    // Предоставляет DAO для работы с таблицей персонажей из экземпляра базы данных..
    fun provideCharacterDao(database: AppDatabase) = database.characterDao
}