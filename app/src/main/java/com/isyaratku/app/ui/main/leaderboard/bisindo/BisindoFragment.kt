package com.isyaratku.app.ui.main.leaderboard.bisindo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.api.BisindoLeaderboardResponse
import com.isyaratku.app.api.ErrorResponse
import com.isyaratku.app.databinding.FragmentBisindoBinding
import com.isyaratku.app.ui.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException


class BisindoFragment : Fragment() {

    private lateinit var rvAdapter: BisindoRecyclerView
    private lateinit var usersList: BisindoLeaderboardResponse
    private lateinit var token: String

    private val bisindoViewModel by viewModels<BisindoViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentBisindoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBisindoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bisindoViewModel.getSession().observe(viewLifecycleOwner) { user ->

            token = user.token
            Log.d("token", token)

            requestLeaderboardAsl(token)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLeaderboardAsl(userToken : String) {


        lifecycleScope.launch {

            showLoading(true)

            try {

                val token = "Bearer $userToken"

                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getLeaderboardBisindo(token)
                try {
                    usersList = successResponse
                    val userList = usersList.users

                    binding.rvRank.apply {
                        rvAdapter = BisindoRecyclerView(userList)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = rvAdapter
                        showLoading(false)

                    }

                } catch (e: Exception) {
                    Log.e("JSON", "Error parsing JSON: ${e.message}")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast(getString(R.string.internet_not_detected))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}