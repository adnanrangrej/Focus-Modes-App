package com.github.adnanrangrej.focusmodes.di

import android.content.Context
import com.github.adnanrangrej.focusmodes.data.local.dao.ModesDao
import com.github.adnanrangrej.focusmodes.data.local.dao.SessionDao
import com.github.adnanrangrej.focusmodes.data.repository.FocusModesRepositoryImpl
import com.github.adnanrangrej.focusmodes.data.repository.PomodoroTimerRepositoryImpl
import com.github.adnanrangrej.focusmodes.data.repository.UserStatsRepositoryImpl
import com.github.adnanrangrej.focusmodes.domain.repository.FocusModesRepository
import com.github.adnanrangrej.focusmodes.domain.repository.PomodoroTimerRepository
import com.github.adnanrangrej.focusmodes.domain.repository.UserStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePomodoroTimerRepository(sessionDao: SessionDao): PomodoroTimerRepository {
        return PomodoroTimerRepositoryImpl(sessionDao)
    }

    @Provides
    @Singleton
    fun provideFocusModeRepository(
        focusModesDao: ModesDao,
        @ApplicationContext context: Context
    ): FocusModesRepository {
        return FocusModesRepositoryImpl(focusModesDao, context)
    }

    @Provides
    @Singleton
    fun provideUserStatsRepository(sessionDao: SessionDao): UserStatsRepository {
        return UserStatsRepositoryImpl(sessionDao)
    }
}