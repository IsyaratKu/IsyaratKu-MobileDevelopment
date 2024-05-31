package com.isyaratku.app.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.isyaratku.app.data.UserRepository
import com.isyaratku.app.data.pref.UserModel

class HomeViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}