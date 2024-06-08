package com.isyaratku.app.ui.main.profile

<<<<<<< HEAD
<<<<<<< Updated upstream
=======
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
>>>>>>> Stashed changes
import android.os.Bundle
import android.provider.Settings
=======
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
>>>>>>> 6024966cb1bdfdd84f416b9479cc894bd81f1421
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.databinding.FragmentProfileBinding
import com.isyaratku.app.setting.SettingModelFactory
import com.isyaratku.app.setting.SettingPreference
import com.isyaratku.app.setting.SettingViewModel
import com.isyaratku.app.setting.datastore
import com.isyaratku.app.ui.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileFragment : Fragment(), ProfileAdapter.OnItemClickListerner {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var token : String

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

        profileViewModel.getSession().observe(viewLifecycleOwner) { user ->

            token = user.token
            Log.d("token",token)

            requestUser(token)

            val profileItem = listOf(
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
            binding.recyclerViewProfile.adapter = adapter

<<<<<<< HEAD
<<<<<<< Updated upstream
        val textView: TextView = binding.textProfile
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
=======
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        val profileItem = listOf(
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

        val adapter = ProfileAdapter(requireContext(), profileItem, this, darkModeSwitchListener)
        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProfile.adapter = adapter



        val pref = SettingPreference.getInstance(requireContext().datastore)
        settingViewModel = ViewModelProvider(this, SettingModelFactory(pref)).get(SettingViewModel::class.java)
        settingViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


    private val darkModeSwitchListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        settingViewModel.saveThmSetting(isChecked)
        // Update the switch state to reflect the change
        buttonView.post {
            buttonView.isChecked = isChecked
>>>>>>> Stashed changes
=======
>>>>>>> 6024966cb1bdfdd84f416b9479cc894bd81f1421
        }

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)



        // Initialize ViewModel and observe theme settings

        val pref = SettingPreference.getInstance(requireContext().datastore)
        settingViewModel = ViewModelProvider(this, SettingModelFactory(pref)).get(SettingViewModel::class.java)

        settingViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    /*

    private val logout = binding1.card.setOnClickListener {
        profileViewModel.logout()
    } */

    private val darkModeSwitchListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked : Boolean ->
        settingViewModel.saveThmSetting(isChecked)
        Log.d("iscek",isChecked.toString())
        buttonView.isChecked = isChecked

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
<<<<<<< HEAD
<<<<<<< Updated upstream
}
=======

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked: $position", Toast.LENGTH_SHORT).show()
        val item = (binding.recyclerViewProfile.adapter as ProfileAdapter).items[position]
        when(item.title){
            "Language"->{
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }

    }
}
>>>>>>> Stashed changes
=======

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked: $position", Toast.LENGTH_SHORT).show()
    }

    private fun requestUser(token: String) {

        lifecycleScope.launch {

            showLoading(true)

            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getProfile(tokenUser)
                try {

                    binding.apply {
                        tvUsername.text = successResponse.user!!.username
                        tvEmail.text = successResponse.user.email
                        tvPoint.text = "Point : ${successResponse.user.score}"
                        Glide.with(requireContext())
                            .load(successResponse.user.urlPhoto)
                            .centerCrop()
                            .into(ivProfile)

                        showLoading(false)
                    }

                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: ${errorBody}")
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
>>>>>>> 6024966cb1bdfdd84f416b9479cc894bd81f1421
