package com.isyaratku.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.di.Injection
import com.isyaratku.app.ui.account.login.LoginViewModel
import com.isyaratku.app.ui.main.MainViewModel
import com.isyaratku.app.ui.main.camera.CameraViewModel
import com.isyaratku.app.ui.main.home.HomeViewModel
import com.isyaratku.app.ui.main.leaderboard.asl.AslViewModel
import com.isyaratku.app.ui.main.leaderboard.bisindo.BisindoViewModel
import com.isyaratku.app.ui.main.profile.ProfileViewModel
import com.isyaratku.app.ui.splashscreen.SplashViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BisindoViewModel::class.java) -> {
                BisindoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AslViewModel::class.java) -> {
                AslViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}