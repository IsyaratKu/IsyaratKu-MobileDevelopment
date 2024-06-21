package com.isyaratku.app.ui.main.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CompoundButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.databinding.FragmentProfileBinding
import com.isyaratku.app.reduceFileImage
import com.isyaratku.app.setting.SettingModelFactory
import com.isyaratku.app.setting.SettingPreference
import com.isyaratku.app.setting.SettingViewModel
import com.isyaratku.app.setting.datastore
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var email: String
    private var currentImageUri: Uri? = null


    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()


        profileViewModel.getSession().observe(viewLifecycleOwner) { user ->

            token = user.token
            Log.d("token", token)

            requestUser(token)
            changePhoto()

        }

        sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)


        // Initialize ViewModel and observe theme settings

        val switchTheme = binding.switchTheme

        val pref = SettingPreference.getInstance(requireContext().datastore)
        settingViewModel =
            ViewModelProvider(this, SettingModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSetting()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThmSetting(isChecked)
        }
    }


    private fun playAnimation() {

        val username = ObjectAnimator.ofFloat(binding.tvUsername, View.ALPHA, 1f).setDuration(200)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val pointAsl = ObjectAnimator.ofFloat(binding.tvAslScore, View.ALPHA, 1f).setDuration(300)
        val pointBisindo = ObjectAnimator.ofFloat(binding.tvBisindoScore, View.ALPHA, 1f).setDuration(300)
        val profileImage = ObjectAnimator.ofFloat(binding.ivProfile, View.ALPHA, 1f).setDuration(500)

        val together2 = AnimatorSet().apply {
            playTogether(pointAsl,pointBisindo)
        }
        val together1 = AnimatorSet().apply {
            playTogether(username,email)
        }

        AnimatorSet().apply {
            playSequentially(profileImage, together1, together2)
            startDelay = 300
        }.start()
    }

    private fun setupAction() {
        binding.apply {
            cardUsername.setOnClickListener {

                val inflater = LayoutInflater.from(context) // Get LayoutInflater from context
                val popUpView =
                    inflater.inflate(R.layout.username_layout, null) // Inflate the layout

                val width = 800
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                val focusable = true

                val popUpWindow = PopupWindow(popUpView, width, height, focusable)

                val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.popup_fade_out)
                fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        popUpWindow.dismiss()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                relative.post {
                    val fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.popup_fade_in)
                    popUpView.startAnimation(fadeInAnim)
                    popUpWindow.showAtLocation(relative, Gravity.CENTER, 0, 0)
                }
                val cancel = popUpView?.findViewById<Button>(R.id.cancelButton)
                val ok = popUpView?.findViewById<Button>(R.id.okButton)

                cancel?.setOnClickListener {
                    popUpView.startAnimation(fadeOutAnim)
                }
                ok?.setOnClickListener {
                    val newusername =
                        popUpView.findViewById<TextInputEditText>(R.id.newUsernameEditText)?.text
                    Log.d("newUsername", newusername.toString())
                    changeUsername(token, newusername.toString())
                    popUpView.startAnimation(fadeOutAnim)

                }

            }
            cardEmail.setOnClickListener {

                val inflater = LayoutInflater.from(context) // Get LayoutInflater from context
                val popUpView = inflater.inflate(R.layout.email_layout, null) // Inflate the layout

                val width = 800
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                val focusable = true

                val popUpWindow = PopupWindow(popUpView, width, height, focusable)

                val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.popup_fade_out)
                fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        popUpWindow.dismiss()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })


                relative.post {
                    val fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.popup_fade_in)
                    popUpView.startAnimation(fadeInAnim)
                    popUpWindow.showAtLocation(relative, Gravity.CENTER, 0, 0)
                }
                val cancel = popUpView?.findViewById<Button>(R.id.cancelButton)
                val ok = popUpView?.findViewById<Button>(R.id.okButton)

                cancel?.setOnClickListener {
                    popUpView.startAnimation(fadeOutAnim)
                }
                ok?.setOnClickListener {
                    val newEmail =
                        popUpView.findViewById<TextInputEditText>(R.id.newEmailEditText)?.text
                    Log.d("newEmail", newEmail.toString())
                    changeEmail(token, newEmail.toString())
                    popUpView.startAnimation(fadeOutAnim)

                }


            }
            cardProfile.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            cardLogout.setOnClickListener {
                logout(token)
            }
            cardLanguage.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            cardAccessibility.setOnClickListener {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("SuspiciousIndentation")
    private fun requestUser(token: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getProfile(tokenUser)
                showLoading(false)
                playAnimation()

                binding.apply {
                    tvUsername.text = successResponse.user!!.username
                    tvEmail.text = successResponse.user.email
                    if (successResponse.user.bisindoScore == null){
                        tvBisindoScore.text = getString(R.string.point_bisindo,"0")
                    } else {
                        tvBisindoScore.text = getString(R.string.point_bisindo,successResponse.user.bisindoScore)
                    }
                    if (successResponse.user.aslScore == null){
                        tvAslScore.text = getString(R.string.point_asl,"0")
                    } else {
                        tvAslScore.text = getString(R.string.point_asl,successResponse.user.aslScore)
                    }
                    Glide.with(requireContext())
                        .load(successResponse.user.urlPhoto)
                        .centerCrop()
                        .error(R.drawable.def_profile)
                        .into(ivProfile)

                    username = successResponse.user.username.toString()
                    email = successResponse.user.email.toString()

                }


            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast(getString(R.string.internet_not_detected))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: $errorBody")
            }
        }
    }

    private fun changeUsername(token: String, newUsername: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val jsonString = """
                        {
                          "oldUsername" : "$username",
                          "newUsername" : "$newUsername"
                        }
                    """
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                binding.tvUsername.text = newUsername

                apiService.changeUsername(tokenUser, jsonObject)
                requestUser(tokenUser)
                showToast(getString(R.string.username_changed))

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast(getString(R.string.internet_not_detected))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: $errorBody")
            }
            showLoading(false)
        }
    }

    private fun changeEmail(token: String, newEmail: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val jsonString = """
                        {
                          "oldEmail" : "$email",
                          "newEmail" : "$newEmail"
                        }
                    """
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

                apiService.changeEmail(tokenUser, jsonObject)

                showToast(getString(R.string.email_changed))

                profileViewModel.logout()

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast(getString(R.string.internet_not_detected))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: $errorBody")
            }
            showLoading(false)
        }
    }

    private fun logout(token: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                apiService.logout(tokenUser)

                showToast(getString(R.string.user_logout))

                profileViewModel.logout()

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast(getString(R.string.internet_not_detected))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: $errorBody")
            }
            showLoading(false)
        }
    }

    private fun changePhoto() {

        binding.ivProfile.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            uploadImage()
            showToast(getString(R.string.image_uploading_in_process))
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")

            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaType())

            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("newPhoto", imageFile.name, requestImageFile)
                .build()

            lifecycleScope.launch {

                try {
                    val tokenUser = "Bearer $token"
                    val apiService = ApiConfig.getApiService()
                    apiService.changeProfPicture(tokenUser, body)

                    showToast(getString(R.string.image_updated))

                    requestUser(token)


                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                } catch (e: SocketTimeoutException) {
                    Log.e("JSON", "Error No internet: ${e.message}")
                    showToast(getString(R.string.internet_not_detected))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("JSON", "Error parsing JSON: $errorBody")
                }
            }

        } ?: showToast(getString(R.string.no_image_selected))
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}
