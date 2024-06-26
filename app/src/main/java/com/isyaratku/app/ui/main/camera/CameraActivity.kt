package com.isyaratku.app.ui.main.camera

import SignLanguageDetector
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.api.ErrorResponse
import com.isyaratku.app.api.SentenceResponse
import com.isyaratku.app.databinding.ActivityCameraBinding
import com.isyaratku.app.ui.ViewModelFactory
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.net.SocketTimeoutException
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), SignLanguageDetector.DetectorListener {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var overlayView: OverlayView
    private lateinit var previewView: PreviewView
    private lateinit var startButton: Button
    private lateinit var detectionResultTextView: TextView
    private lateinit var pointv: TextView
    private lateinit var signLanguageDetectorHelper: SignLanguageDetector
    private lateinit var aslDetectorHelper: ASLSignLanguageDetectorHelper
    private lateinit var bisindoInterpreter: Interpreter
    private lateinit var aslInterpreter: Interpreter
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSetupManager: CameraSetupManager
    private lateinit var sentences: SentenceResponse
    private lateinit var sentenceText: String
    private var token: String = ""
    private var score: Int = 0
    private var gameTimer: CountDownTimer? = null
    private var isGameRunning: Boolean = false
    private var modelType: String? = null
    private var aslScore: String = ""
    private var bisindoScore: String = " "
    private var timeLeftInMillis: Long = 20000
    private val defaultTimeToAdd = 8000L
    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detectionResultTextView = binding.detectionResultTextView
        pointv = binding.pointv
        previewView = binding.viewFinder
        startButton = binding.startButton
        overlayView = findViewById(R.id.overlay)


        cameraSetupManager = CameraSetupManager(this, previewView, ImageAnalyzer())
        cameraExecutor = Executors.newSingleThreadExecutor()


        viewModel.getSession().observe(this@CameraActivity) { user ->
            token = user.token
            Log.d("token", token)

        }

        val switchCameraButton = findViewById<ImageView>(R.id.switchCamera)
        switchCameraButton.setOnClickListener {
            cameraSetupManager.switchCamera()
        }

        startButton.setOnClickListener {
            showModelSelectionDialog()
        }
    }

    private fun showModelSelectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Model")
        val models = arrayOf("ASL", "Bisindo")
        builder.setItems(models) { dialog, which ->
            when (which) {
                0 -> {
                    initializeASLModel()
                    requestSentencesASL()
                }

                1 -> {
                    initializeBisindoModel()
                    requestSentencesBisindo()
                }
            }
            dialog.dismiss()
            countDownToStart()
        }
        builder.show()
    }

    private fun initializeASLModel() {
        try {
            val aslModel = FileUtil.loadMappedFile(this, "model_asl.tflite")
            aslInterpreter = Interpreter(aslModel)
            aslDetectorHelper = ASLSignLanguageDetectorHelper(
                this,
                aslInterpreter,
                object : ASLSignLanguageDetectorHelper.DetectorListener {
                    override fun onResults(results: FloatArray) {
                        if (isGameRunning && modelType == "ASL") {
                            handleResults(results, "ASL")
                        }
                    }
                })
            modelType = "ASL"
        } catch (e: FileNotFoundException) {
            Log.e("CameraActivity", "ASL model file not found: ${e.message}")
            Toast.makeText(this, "ASL model file not found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("CameraActivity", "Error initializing ASL model: ${e.message}")
        }
    }

    private fun initializeBisindoModel() {
        try {
            val bisindoModel = FileUtil.loadMappedFile(this, "bisindo_model.tflite")
            bisindoInterpreter = Interpreter(bisindoModel)
            signLanguageDetectorHelper = SignLanguageDetector(
                this,
                bisindoInterpreter,
                object : SignLanguageDetector.DetectorListener {
                    override fun onResults(results: FloatArray) {
                        if (isGameRunning && modelType == "Bisindo") {
                            handleResults(results, "Bisindo")
                        }
                    }
                })
            modelType = "Bisindo"
        } catch (e: FileNotFoundException) {
            Log.e("CameraActivity", "Bisindo model file not found: ${e.message}")
            Toast.makeText(this, "Bisindo model file not found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("CameraActivity", "Error initializing Bisindo model: ${e.message}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startGame() {

        binding.apply {
            detectionResultTextView.visibility = View.VISIBLE
            Timer.visibility = View.VISIBLE
            pointv.visibility = View.VISIBLE
            tvChar.visibility = View.VISIBLE
        }
        startButton.visibility = View.GONE
        score = 0
        pointv.text = "Score : $score"
        isGameRunning = true
        startTimer()
        cameraSetupManager.startCamera()
    }

    private fun countDownToStart() {
        binding.tvCountDown.visibility = View.VISIBLE
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                binding.tvCountDown.text = "$remainingSeconds s"
            }

            override fun onFinish() {
                binding.tvCountDown.visibility = View.GONE
                startGame()
            }

        }.start()

    }

    private fun startTimer() {
        gameTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                binding.Timer.text = "$remainingSeconds s"
            }

            override fun onFinish() {
                endGame()
            }

        }.start()
    }

    private fun addTime(additionalTime: Long) {

        gameTimer?.cancel()
        timeLeftInMillis += additionalTime
        startTimer()

    }


    private fun endGame() {
        isGameRunning = false
        gameTimer?.cancel()
        cameraSetupManager.stopCamera()
        showScorePopUp()

        if (modelType == "ASL") {
            sendScore("ASL", score)
        } else if (modelType == "Bisindo") {
            sendScore("Bisindo", score)
        }
    }

    private fun showScorePopUp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("Your Score : $score \n Full Text : $sentenceText")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            startButton.visibility = View.VISIBLE
            finish()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        try {
            hideSystemUI()
            cameraSetupManager.startCamera()
        } catch (e: Exception) {
            Log.e("CameraActivity", "Error in onResume: ${e.message}")
        }
    }

    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            if (::aslDetectorHelper.isInitialized) {
                aslDetectorHelper.detectSign(image)
            } else if (::signLanguageDetectorHelper.isInitialized) {
                signLanguageDetectorHelper.detectSign(image)
            }
            image.close()
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

    private fun handleResults(results: FloatArray, modelType: String) {
        val maxProbability = results.maxOrNull() ?: 0.00f
        val trimValue = String.format("%.2f", maxProbability)
        val maxIndex = results.indexOfFirst { it == maxProbability } // Find index of max value
        val detectedSign =
            (maxIndex + 'A'.code).toChar() // Convert index to corresponding letter

        val sentence = sentenceText.replace("\\s".toRegex(), "").uppercase(Locale.getDefault()).toCharArray()
        Log.d("char",String(sentence))
        var position = 0

        runOnUiThread {
            pointv.text = "Score: $score"
            detectionResultTextView.text =
                "Detected sign: $detectedSign with probability: $trimValue using model: $modelType"
            binding.tvChar.text = sentence[position].toString()
        }


        if (maxProbability > 0.55) {
            while (position < sentence.size && sentence[position] == detectedSign) {
                score += 10
                playPointSound()
                runOnUiThread { addTime(defaultTimeToAdd) }
                position++
            }

            if (modelType == "ASL") {
                aslScore += 10
            } else if (modelType == "Bisindo") {
                bisindoScore += 10
            }

            runOnUiThread {
                pointv.text = "Score: $score"
                detectionResultTextView.text =
                    "Detected sign: $detectedSign with probability: $trimValue using model: $modelType"
                binding.tvChar.text = sentence[position].toString()
            }

            if (position == sentence.size) {
                endGame()
                position = 0
            }
        }

    }


    private fun sendScore(modelType: String, score: Int) {
        lifecycleScope.launch {
            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val jsonObject = JsonObject().apply {
                    if (modelType == "ASL") {

                        addProperty("asl_score", score)
                    } else if (modelType == "Bisindo") {
                        addProperty("bisindo_score", score)
                    }
                }
                Log.d("API Request", "Sending $modelType score: $jsonObject")
                if (modelType == "ASL") {
                    val response = apiService.aslScore(tokenUser, jsonObject)
                    Log.d("API Response", "ASL Score Updated: ${response.newAslScore}")
                } else if (modelType == "Bisindo") {
                    val response = apiService.bisindoScore(tokenUser, jsonObject)
                    Log.d("API Response", "Bisindo Score Updated: ${response.newBisindoScore}")
                }
            } catch (e: Exception) {
                Log.e("API Error", "Failed to send score: ${e.message}")
            }
        }
    }

    private fun playPointSound() {
        val pointSound = MediaPlayer.create(this, R.raw.sfx_point)
        if (pointSound.isPlaying) {
            pointSound.stop()
            pointSound.release()
        }
        pointSound.start()
    }

    override fun onResults(results: FloatArray) {

    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
    }

    private fun requestSentencesBisindo() {


        lifecycleScope.launch {

            try {

                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getBisindoSentences()
                try {
                    sentences = successResponse
                    sentenceText = sentences.sentence.toString()


                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
            }
        }
    }

    private fun requestSentencesASL() {


        lifecycleScope.launch {

            try {

                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getASLSentences()
                try {
                    sentences = successResponse
                    sentenceText = sentences.sentence.toString()


                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
            }
        }
    }

}