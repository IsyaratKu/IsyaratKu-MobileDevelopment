package com.isyaratku.app.ui.main.cameraActivity

import SignLanguageDetectorHelper
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.isyaratku.app.R

import com.isyaratku.app.databinding.ActivityCameraBinding
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), SignLanguageDetectorHelper.DetectorListener {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var previewView: PreviewView
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var detectionResultTextView: TextView
    private lateinit var pointv: TextView
    private lateinit var signLanguageDetectorHelper: SignLanguageDetectorHelper
    private var imageCapture: ImageCapture? = null
    private lateinit var interpreter: Interpreter
    private lateinit var cameraExecutor: ExecutorService
    private var score: Int = 0 // Variabel untuk menyimpan poin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detectionResultTextView = binding.detectionResultTextView
        pointv= binding.pointv

        val model = FileUtil.loadMappedFile(this, "bisindo_model.tflite")
        interpreter = Interpreter(model)

        previewView = binding.viewFinder
        signLanguageDetectorHelper = SignLanguageDetectorHelper(this, interpreter, this)

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                CameraSelector.DEFAULT_FRONT_CAMERA
            else
                CameraSelector.DEFAULT_BACK_CAMERA

            startCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal Memunculkan Camera",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "StartCamera : ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            signLanguageDetectorHelper.detectSign(image)
            image.close() //
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
    }

    fun onError(error: String) {
        Log.e(TAG, error)
    }
    override fun onResults(results: FloatArray) {
        runOnUiThread {
            val detectedSign = results.indices.maxByOrNull { results[it] } ?: -1
            if (detectedSign != -1) {
                score += 10 // Tambahkan poin jika ada deteksi yang valid
                val detectedSignText = "Detected sign: ${detectedSign} in ${results[detectedSign]}"
                detectionResultTextView.text = "$detectedSignText\n"
                pointv.text = "$score"
            }
        }
        Log.i(TAG, "Detected sign: ${results.contentToString()}")

    }
}
