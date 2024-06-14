package com.isyaratku.app.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.data.pref.UserModel
import com.isyaratku.app.databinding.FragmentHomeBinding
import com.isyaratku.app.ui.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val binding get() = _binding!!
    private lateinit var token: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->


            token = user.token
            Log.d("token", token)
            requestUser(token)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
                    if (successResponse.user.aslScore == null ){
                        tvAslScore.text = "Point ASL : 0"
                    } else {
                        tvAslScore.text = "Point ASL : ${successResponse.user.aslScore}"
                    }
                    if (successResponse.user.bisindoScore == null ){
                        tvBisindoScore.text = "Point Bisindo : 0"
                    } else {
                        tvBisindoScore.text = "Point Bisindo : ${successResponse.user.bisindoScore}"
                    }
                    Glide.with(requireContext())
                        .load(successResponse.user.urlPhoto)
                        .centerCrop()
                        .into(circleImageView)


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

    private fun getToken() {



        lifecycleScope.launch {

            try {

                val jsonString = """
                        {
                          "email": "$email",
                          "password": "$password"
                        }
                    """
                val gson = Gson()
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.login(jsonObject)


                try {
                    if (successResponse.message == "User logged in successfully" && successResponse.user?.emailVerified == true) {
                        val token = successResponse.token.toString()
                        Log.d("getToken", token)
                        homeViewModel.saveSession(UserModel(email, token, password))

                    } else {
                        Log.e("Login", "Login failed")
                    }



                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }


            } catch (e: HttpException) {
                // val errorBody = e.response()?.errorBody()?.string()

            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}