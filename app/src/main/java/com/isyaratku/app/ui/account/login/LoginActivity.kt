package com.isyaratku.app.ui.account.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.isyaratku.app.R
import com.isyaratku.app.data.pref.UserModel
import com.isyaratku.app.databinding.ActivityLoginBinding
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.register.RegisterActivity
import com.isyaratku.app.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // enableEdgeToEdge() // disable dulu, gambar ga mentok atas soalnya
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        setupAction()

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val username = binding.emailEditText.text.toString()
            viewModel.saveSession(UserModel(username, "sample_token"))
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Anda berhasil login.")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}