package com.isyaratku.app.ui.main.profile

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

            /*val profileItem = listOf(
                profile_Item(profile_Item.TYPER_HEADER, title = "Account Setting"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_person_24, "Change Username"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_lock_24, "Change Password"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_alternate_email_24, "Change Email"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_exit_to_app_24, "Logout"),
                profile_Item(profile_Item.TYPER_HEADER, title = "Application Setting"),
                profile_Item(profile_Item.TYPER_DARK_MODE, R.drawable.baseline_dark_mode_24, "Dark Mode"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_language_24, "Language"),
                profile_Item(profile_Item.TYPER_ITEM, R.drawable.baseline_accessibility_new_24, "Accessibility"),
            )

            val adapter = ProfileAdapter(requireContext(), profileItem, this, darkModeSwitchListener, token)
            binding.recyclerViewProfile.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewProfile.adapter = adapter */

        }

        sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)


        // Initialize ViewModel and observe theme settings

        val switchTheme = binding.switchTheme

        val pref = SettingPreference.getInstance(requireContext().datastore)
        settingViewModel =
            ViewModelProvider(this, SettingModelFactory(pref)).get(SettingViewModel::class.java)

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

    /*

    private val logout = binding1.card.setOnClickListener {
        profileViewModel.logout()
    } */

    /*
    private val darkModeSwitchListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked : Boolean ->
        settingViewModel.saveThmSetting(isChecked)
        Log.d("iscek",isChecked.toString())
        buttonView.isChecked = isChecked

    } */

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

                relative.post {
                    popUpWindow.showAtLocation(relative, Gravity.CENTER, 0, 0)
                }
                val cancel = popUpView?.findViewById<Button>(R.id.cancelButton)
                val ok = popUpView?.findViewById<Button>(R.id.okButton)

                cancel?.setOnClickListener {
                    popUpWindow.dismiss()
                }
                ok?.setOnClickListener {
                    val newusername =
                        popUpView?.findViewById<TextInputEditText>(R.id.newUsernameEditText)?.text
                    Log.d("newUsername", newusername.toString())
                    changeUsername(token, newusername.toString())
                    popUpWindow.dismiss()

                }

            }
            cardEmail.setOnClickListener {

                val inflater = LayoutInflater.from(context) // Get LayoutInflater from context
                val popUpView = inflater.inflate(R.layout.email_layout, null) // Inflate the layout

                val width = 800
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                val focusable = true

                val popUpWindow = PopupWindow(popUpView, width, height, focusable)

                relative.post {
                    popUpWindow.showAtLocation(relative, Gravity.CENTER, 0, 0)
                }
                val cancel = popUpView?.findViewById<Button>(R.id.cancelButton)
                val ok = popUpView?.findViewById<Button>(R.id.okButton)

                cancel?.setOnClickListener {
                    popUpWindow.dismiss()
                }
                ok?.setOnClickListener {
                    val newEmail =
                        popUpView?.findViewById<TextInputEditText>(R.id.newEmailEditText)?.text
                    Log.d("newEmail", newEmail.toString())
                    changeEmail(token, newEmail.toString())
                    popUpWindow.dismiss()

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

    /* override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked: $position", Toast.LENGTH_SHORT).show()
        val item = (binding.recyclerViewProfile.adapter as ProfileAdapter).items[position]
        when(item.title){
            "Language"->{
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
    }*/

    @SuppressLint("SuspiciousIndentation")
    private fun requestUser(token: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getProfile(tokenUser)
                showLoading(false)

                binding.apply {
                    tvUsername.text = successResponse.user!!.username
                    tvEmail.text = successResponse.user.email
                    if (successResponse.user.score == null){
                        tvPoint.text = "Point : 0"
                    } else {
                        tvPoint.text = "Point : ${successResponse.user.score}"
                    }
                    Glide.with(requireContext())
                        .load(successResponse.user.urlPhoto)
                        .centerCrop()
                        .into(ivProfile)

                    username = successResponse.user!!.username.toString()
                    email = successResponse.user.email.toString()

                }


            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast("Internet not detected")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: ${errorBody}")
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
                showToast("Username Changed")

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast("Internet not detected")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: ${errorBody}")
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

                showToast("Email Changed")

                profileViewModel.logout()

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast("Internet not detected")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: ${errorBody}")
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

                showToast("User Logout")

                profileViewModel.logout()

            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast("Internet not detected")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: ${errorBody}")
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
            showToast("Image Uploading in Process")
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

                    showToast("Image Updated")

                    requestUser(token)


                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                } catch (e: SocketTimeoutException) {
                    Log.e("JSON", "Error No internet: ${e.message}")
                    showToast("Internet not detected")
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("JSON", "Error parsing JSON: ${errorBody}")
                }
            }

        } ?: showToast("No Image Selected")
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}
