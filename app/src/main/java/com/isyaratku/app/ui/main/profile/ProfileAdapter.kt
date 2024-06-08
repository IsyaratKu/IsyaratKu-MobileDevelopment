package com.isyaratku.app.ui.main.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.databinding.ItemDarkModeBinding
import com.isyaratku.app.databinding.ItemHeaderBinding
import com.isyaratku.app.databinding.ItemProfileBinding
import retrofit2.HttpException

class ProfileAdapter(
    private val context: Context,
    val items: List<profile_Item>,
    private val listener: OnItemClickListerner,
    private val darkModeSwitchListener: CompoundButton.OnCheckedChangeListener,
    private val token: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface OnItemClickListerner {
        fun onItemClick(position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            profile_Item.TYPER_HEADER -> {
                val binding =
                    ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }

            profile_Item.TYPER_DARK_MODE -> {
                val binding =
                    ItemDarkModeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DarkModeViewHolder(binding)
            }

            else -> {
                val binding =
                    ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProfileViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = items[position]
        when (getItemViewType(position)) {
            profile_Item.TYPER_HEADER -> {
                (holder as HeaderViewHolder).binding.headerTitle.text = currentItem.title
            }

            profile_Item.TYPER_DARK_MODE -> {
                val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)
                val darkModeBinding = (holder as DarkModeViewHolder).binding
                //darkModeBinding.switchTheme.setOnCheckedChangeListener(null)
                darkModeBinding.switchTheme.isChecked = isDarkModeEnabled
                darkModeBinding.switchTheme.setOnCheckedChangeListener(darkModeSwitchListener)
            }

            else -> {
                val profileBinding = (holder as ProfileViewHolder).binding
                profileBinding.icon.setImageResource(currentItem.iconResId)
                profileBinding.title.text = currentItem.title
                profileBinding.card.setOnClickListener {
                    Log.d("card", currentItem.title.toString())
                }

                profileBinding.card.setOnClickListener {
                    when (currentItem) {
                        items.get(4) -> {
                            requestLogin()

                        }
                        items.get(7) -> {
                            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                            context.startActivity(intent) //
                        }


                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size

    inner class ProfileViewHolder(val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    inner class HeaderViewHolder(val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DarkModeViewHolder(val binding: ItemDarkModeBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun requestLogin() {


        val userToken: String = "Bearer $token"

        // showLoading(true)


        try {


            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.logout(userToken)

            // showLoading(false)

            try {
                if (successResponse.message == "User logged out successfully") {


                } else {
                    Log.e("logout", "logout failed")
                    showToast("Logout Failed")
                }


            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
            }


        } catch (e: HttpException) {
            // val errorBody = e.response()?.errorBody()?.string()
            showToast("error")
        }


    }



    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}

