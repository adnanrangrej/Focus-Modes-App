package com.github.adnanrangrej.focusmodes.di

import android.content.Context
import androidx.room.Room
import com.github.adnanrangrej.focusmodes.data.local.FocusModesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusModesDatabase {
        return Room.databaseBuilder(
            context,
            FocusModesDatabase::class.java,
            "focus_modes_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: FocusModesDatabase) = database.sessionDao()

    @Provides
    @Singleton
    fun provideFocusModeDao(database: FocusModesDatabase) = database.focusModeDao()

}