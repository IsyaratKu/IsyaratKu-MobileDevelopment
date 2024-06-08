package com.isyaratku.app.ui.main.profile

<<<<<<< Updated upstream
=======
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
>>>>>>> Stashed changes
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.isyaratku.app.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
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
