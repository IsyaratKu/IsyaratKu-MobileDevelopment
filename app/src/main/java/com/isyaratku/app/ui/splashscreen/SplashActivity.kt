package com.isyaratku.app.ui.splashscreen

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.data.pref.UserModel
import com.isyaratku.app.databinding.ActivitySplashBinding
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.login.LoginActivity
import com.isyaratku.app.ui.main.MainActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding : ActivitySplashBinding
    private lateinit var email: String
    private lateinit var password: String
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

                email = user.email
                password = user.password
                getToken()

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

    private fun getToken() {


        lifecycleScope.launch {

            try {

                val jsonString = """
                        {
                          "email": "$email",
                          "password": "$password"
                        }
                    """
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.login(jsonObject)


                try {
                    if (successResponse.message == "User logged in successfully" && successResponse.user?.emailVerified == true) {
                        val token = successResponse.token.toString()
                        Log.d("getToken", token)
                        viewModel.saveSession(UserModel(email, token, password))

                    } else {
                        Log.e("Login", "Login failed")
                    }



                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }


            } catch (e: HttpException) {
                // val errorBody = e.response()?.errorBody()?.string()

            }

        }
    }


}