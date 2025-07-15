package com.example.testtaskworkmate.data.di

import com.example.testtaskworkmate.data.model.RamRepository
import com.example.testtaskworkmate.data.model.RamRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindRepository(impl: RamRepositoryImpl): RamRepository
}
