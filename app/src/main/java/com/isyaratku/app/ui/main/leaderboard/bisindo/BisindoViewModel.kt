package com.isyaratku.app.ui.main.leaderboard.bisindo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.data.pref.UserModel

class BisindoViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}