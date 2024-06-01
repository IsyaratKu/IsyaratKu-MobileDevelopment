package com.isyaratku.app.ui.main.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RankViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Rank Fragment"
    }
    val text: LiveData<String> = _text
}