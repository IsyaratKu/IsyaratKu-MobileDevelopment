package com.isyaratku.app.ui.account.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig.getApiService
import com.isyaratku.app.api.ErrorResponse
import com.isyaratku.app.customview.AccButton
import com.isyaratku.app.customview.AccEditText
import com.isyaratku.app.databinding.ActivityRegisterBinding
import com.isyaratku.app.ui.account.login.LoginActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding

    private lateinit var accButton: AccButton
    private lateinit var accPassText: AccEditText
    private lateinit var accEmailText: TextInputEditText
    private lateinit var accUsernameText : TextInputEditText
    private lateinit var accPassConfText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            requestRegister()
        }


    }

    private fun setupView() {
        accButton = binding.registerButton
        accPassText = binding.passwordEditText
        accPassConfText = binding.passwordConfirmEditText
        accEmailText = binding.emailEditText
        accUsernameText = binding.usernameEditText

        setMyButtonEnable()


        accPassText.addTextChangedListener(object : TextWatcher {
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

        accUsernameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        accPassConfText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun requestRegister() {
        showLoading(true)

        lifecycleScope.launch {

            val email: String = binding.emailEditText.text.toString()
            val password: String = binding.passwordEditText.text.toString()
            val username: String = binding.usernameEditText.text.toString()
            try {
                val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
                intent.putExtra("email", email)
                val jsonString = """
                        {
                          "email": "$email",
                          "password": "$password",
                          "username" : "$username"
                        }
                    """
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

                val apiService = getApiService()
                apiService.register(jsonObject)
                showToast("User Account Created, Check Email Verification at your email for login")
                showLoading(false)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterActivity as Activity).toBundle())

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                showToast(errorResponse.error.toString())
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

    private fun setMyButtonEnable() {

        val password = accPassText.text
        val email = accEmailText.text
        val passwordConfirm = accPassConfText.text
        val username = accUsernameText.text


        accButton.isEnabled = password != null && password.toString().isNotEmpty()
                && email != null && email.toString().isNotEmpty()
                && password.toString().length >= 8
                && passwordConfirm != null && passwordConfirm.toString().isNotEmpty()
                && username != null && username.toString().isNotEmpty()
                && password.toString() == passwordConfirm.toString()


    }

}