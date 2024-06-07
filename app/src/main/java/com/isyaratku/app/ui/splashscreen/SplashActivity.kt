package com.isyaratku.app.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.isyaratku.app.R
import com.isyaratku.app.databinding.ActivitySplashBinding
import com.isyaratku.app.setting.SettingModelFactory
import com.isyaratku.app.setting.SettingPreference
import com.isyaratku.app.setting.SettingViewModel
import com.isyaratku.app.setting.datastore
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.login.LoginActivity
import com.isyaratku.app.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding : ActivitySplashBinding
    private lateinit var settingViewModel : SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        checkSession()

    }

    fun checkSession() {

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                Handler().postDelayed({
                    startActivity(intent)
                },2000)
            } else {
                Handler().postDelayed({
                    val intent = Intent(this@SplashActivity,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                },2000)
            }
        }
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.desc, View.ALPHA, 1f).setDuration(300)
        val logo = ObjectAnimator.ofFloat(binding.circleImage, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(title,desc)
        }

        AnimatorSet().apply {
            playSequentially(logo, together)
            startDelay = 300
        }.start()
    }



}