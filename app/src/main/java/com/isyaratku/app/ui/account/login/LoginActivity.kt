package com.isyaratku.app.ui.account.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.customview.AccButton
import com.isyaratku.app.customview.AccEditText
import com.isyaratku.app.data.pref.UserModel
import com.isyaratku.app.databinding.ActivityLoginBinding
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.ForgotPasswordActivity
import com.isyaratku.app.ui.account.register.RegisterActivity
import com.isyaratku.app.ui.main.MainActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    private lateinit var accButton: AccButton
    private lateinit var accPasstext: AccEditText
    private lateinit var accEmailText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        accEmailText = binding.emailEditText

        val inputEmail: String? = intent.getStringExtra("email")

        accEmailText.setText(inputEmail)

        setupView()
        setupAction()


    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            requestLogin()
        }

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        accButton = binding.loginButton
        accPasstext = binding.passwordEditText
        accEmailText = binding.emailEditText

        setMyButtonEnable()


        accPasstext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        accEmailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })


    }

    private fun setMyButtonEnable() {

        val password = accPasstext.text
        val email = accEmailText.text


        accButton.isEnabled = password != null && password.toString().isNotEmpty()
                && email != null && email.toString().isNotEmpty()
                && password.toString().length >= 8

    }

    private fun requestLogin() {
        showLoading(true)

        lifecycleScope.launch {
            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()
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

                showLoading(false)

                try {
                    if (successResponse.message == "User logged in successfully" && successResponse.user?.emailVerified == true) {
                        val token = successResponse.token.toString()
                        Log.d("Token", token)
                        viewModel.saveSession(UserModel(email, token, password))

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle())

                    } else {
                        Log.e("Login", "Login failed")
                        showToast("Please verify your account, check your email inbox")
                    }



                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                } catch (e : SocketTimeoutException){
                    Log.e("JSON", "Error No internet: ${e.message}")
                    showToast("Internet not detected, Try Again")
                }


            } catch (e: HttpException) {
                // val errorBody = e.response()?.errorBody()?.string()
                showToast("Email or Password is Wrong, Try again")
                showLoading(false)
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}