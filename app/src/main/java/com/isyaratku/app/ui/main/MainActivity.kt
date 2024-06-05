package com.isyaratku.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isyaratku.app.R
import com.isyaratku.app.databinding.ActivityMainBinding
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.main.cameraActivity.CameraActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

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


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard,R.id.navigation_camera, R.id.navigation_rank,R.id.navigation_profile
            )
        )*/
        // setupActionBarWithNavController(navController, appBarConfiguration)
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



}
