package com.isyaratku.app.di

import android.content.Context
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.data.pref.UserPreference
import com.isyaratku.app.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}