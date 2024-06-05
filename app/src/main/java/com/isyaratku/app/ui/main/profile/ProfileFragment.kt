package com.isyaratku.app.ui.main.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.isyaratku.app.R
import com.isyaratku.app.databinding.FragmentProfileBinding
import com.isyaratku.app.setting.SettingModelFactory
import com.isyaratku.app.setting.SettingPreference
import com.isyaratku.app.setting.SettingViewModel
import com.isyaratku.app.setting.datastore

class ProfileFragment : Fragment(), ProfileAdapter.OnItemClickListerner {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingViewModel: SettingViewModel

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

    private val darkModeSwitchListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        settingViewModel.saveThmSetting(isChecked)
        // Update the switch state to reflect the change
        buttonView.post {
            buttonView.isChecked = isChecked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Clicked: $position", Toast.LENGTH_SHORT).show()
    }
}
