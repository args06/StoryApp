package com.example.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.storyapp.BuildConfig
import com.example.storyapp.data.process.StoryRepositoryImpl
import com.example.storyapp.data.local.service.StoryDatabase
import com.example.storyapp.data.preferences.AppPreferences
import com.example.storyapp.data.remote.service.StoryAPI
import com.example.storyapp.domain.repository.StoryRepository
import com.example.storyapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): Interceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        } else {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: Interceptor
    ): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.apply {
            addInterceptor(loggingInterceptor)
        }
        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()

    @Provides
    @Singleton
    fun provideStoryApi(retrofit: Retrofit): StoryAPI {
        return retrofit.create(StoryAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = {
            context.preferencesDataStoreFile(
                Constant.DATASTORE_NAME
            )
        })
    }

    @Provides
    @Singleton
    fun provideRepository(
        storyAPI: StoryAPI, pref: AppPreferences, storyDatabase: StoryDatabase
    ): StoryRepository {
        return StoryRepositoryImpl(storyAPI, pref, storyDatabase)
    }

    @Provides
    @Singleton
    fun provideRoomDb(context: Context): StoryDatabase = Room.databaseBuilder(
        context, StoryDatabase::class.java, Constant.DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideStoryDao(storyDatabase: StoryDatabase) = storyDatabase.storyDao()

    @Provides
    @Singleton
    fun provideRemoteKeysDao(storyDatabase: StoryDatabase) = storyDatabase.remoteKeysDao()

}