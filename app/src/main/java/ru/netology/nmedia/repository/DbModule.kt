package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.App
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDb = Room
        .databaseBuilder(context, AppDb::class.java, "app.db")
        .build()

    @Provides
    fun providePostDao(db: AppDb) : PostDao = db.postDao()
}