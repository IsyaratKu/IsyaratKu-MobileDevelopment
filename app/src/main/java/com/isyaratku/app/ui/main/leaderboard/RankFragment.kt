package com.isyaratku.app.ui.main.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.api.ErrorResponse
import com.isyaratku.app.api.LeaderboardResponse
import com.isyaratku.app.databinding.FragmentRankBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RankFragment : Fragment() {

    private lateinit var rvAdapter: RankRecyclerView
    private lateinit var usersList: LeaderboardResponse
    private var _binding: FragmentRankBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentRankBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requestLogin()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLogin() {


        lifecycleScope.launch {

            showLoading(true)

            try {

                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getLeaderboard()
                try {
                    usersList = successResponse
                    val userList = usersList.users

                    binding.rvRank.apply {
                        rvAdapter = RankRecyclerView(userList)
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