package com.isyaratku.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.data.pref.UserModel
import com.isyaratku.app.databinding.ActivityMainBinding
import com.isyaratku.app.setting.SettingModelFactory
import com.isyaratku.app.setting.SettingPreference
import com.isyaratku.app.setting.SettingViewModel
import com.isyaratku.app.setting.datastore
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.account.login.LoginActivity
import com.isyaratku.app.ui.main.camera.CameraActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingViewModel : SettingViewModel
    private lateinit var email: String
    private lateinit var password: String

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
                startCameraX()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getThemeSetting()
        checkSession()


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)





        binding.fabCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
                startCameraX()
            }
        }


    }

    private fun startCameraX() {
        Log.d("CameraFragment", "Starting CameraActivity")
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            Log.d("CameraFragment", "Image URI: $currentImageUri")
        }
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }

    private fun getThemeSetting(){
        val pref = SettingPreference.getInstance(this.datastore)
        settingViewModel = ViewModelProvider(this, SettingModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSetting().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun checkSession() {

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                Log.d("check session","logout")

            }
        }
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
