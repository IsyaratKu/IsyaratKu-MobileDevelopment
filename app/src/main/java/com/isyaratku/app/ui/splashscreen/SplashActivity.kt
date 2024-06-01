package com.isyaratku.app.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.isyaratku.app.R
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.login.LoginActivity
import com.isyaratku.app.ui.main.MainActivity
import com.isyaratku.app.ui.main.MainViewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkSession()

    }

    fun checkSession() {

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                Handler().postDelayed({
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                },2000)
            } else {
                Handler().postDelayed({
                    val intent = Intent(this@SplashActivity,MainActivity::class.java)
                    startActivity(intent)
                },2000)
            }
        }
    }

}