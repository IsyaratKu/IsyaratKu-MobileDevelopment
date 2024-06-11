package com.isyaratku.app.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.databinding.FragmentHomeBinding
import com.isyaratku.app.ui.ViewModelFactory
import com.isyaratku.app.ui.main.profile.ProfileViewModel
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
                    if (successResponse.user.score == null){
                        tvScore.text = "Point : 0"
                    } else {
                        tvScore.text = "Point : ${successResponse.user.score}"
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}