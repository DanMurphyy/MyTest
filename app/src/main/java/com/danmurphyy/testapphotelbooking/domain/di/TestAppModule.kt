package com.danmurphyy.testapphotelbooking.domain.di

import com.danmurphyy.testapphotelbooking.domain.repository.HotelRepository
import com.danmurphyy.testapphotelbooking.domain.repository.HotelRepositoryImp
import com.danmurphyy.testapphotelbooking.domain.retrofit.HotelApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://run.mocky.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHotelApiService(retrofit: Retrofit): HotelApiService {
        return retrofit.create(HotelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHotelRepository(retrofit: Retrofit): HotelRepository {
        return HotelRepositoryImp(retrofit)
    }

}