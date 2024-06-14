package com.isyaratku.app.ui.main.camera

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class CameraSetupManager(
    private val activity: AppCompatActivity,
    private val previewView: PreviewView,
    private val imageAnalyzer: ImageAnalysis.Analyzer
) {
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(Executors.newSingleThreadExecutor(), imageAnalyzer)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    activity,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Toast.makeText(activity, "Gagal Memunculkan Camera", Toast.LENGTH_SHORT).show()
                Log.e("CameraSetupManager", "StartCamera : ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA
        startCamera()
    }


    fun stopCamera(){
        cameraProvider?.unbindAll()
    }
}
