package com.isyaratku.app.ui.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}