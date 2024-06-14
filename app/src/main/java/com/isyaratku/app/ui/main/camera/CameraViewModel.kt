package com.isyaratku.app.ui.main.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.data.pref.UserModel

class CameraViewModel (private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}