package com.isyaratku.app.ui.main.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.bumptech.glide.Glide
import com.isyaratku.app.R
import com.isyaratku.app.api.ApiClient
import com.isyaratku.app.api.ApiConfig
import com.isyaratku.app.api.ItemNews
import com.isyaratku.app.api.NewsResponse
import com.isyaratku.app.databinding.FragmentHomeBinding
import com.isyaratku.app.ui.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        Newsrv()
        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            token = user.token
            Log.d("token", token)
            requestUser(token)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.tvnewsrc.layoutManager = LinearLayoutManager(requireContext())
        binding.tvnewsrc.adapter = newsAdapter
    }

    private fun requestUser(token: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val tokenUser = "Bearer $token"
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getProfile(tokenUser)
                showLoading(false)

                if (_binding != null) { // Check if binding is not null
                    binding.apply {
                        startAnimation()
                        tvUsername.text = successResponse.user!!.username
                        tvAslScore.text = "Point ASL : ${successResponse.user.aslScore ?: 0}"
                        tvBisindoScore.text = "Point Bisindo : ${successResponse.user.bisindoScore ?: 0}"
                        Glide.with(requireContext())
                            .load(successResponse.user.urlPhoto)
                            .centerCrop()
                            .error(R.drawable.baseline_person_24)
                            .into(circleImageView)
                    }
                }
            } catch (e: Exception) {
                Log.e("JSON", "Error parsing JSON: ${e.message}")
                showLoading(false)
            } catch (e: SocketTimeoutException) {
                Log.e("JSON", "Error No internet: ${e.message}")
                showToast("Internet not detected")
                showLoading(false)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("JSON", "Error parsing JSON: $errorBody")
                showLoading(false)
            }
        }
    }

    private fun startAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvUsername, View.ALPHA, 1f).setDuration(500)
        val scoreBisindo = ObjectAnimator.ofFloat(binding.tvBisindoScore, View.ALPHA, 1f).setDuration(300)
        val scoreAsl = ObjectAnimator.ofFloat(binding.tvAslScore, View.ALPHA, 1f).setDuration(300)
        val image = ObjectAnimator.ofFloat(binding.circleImageView, View.ALPHA, 1f).setDuration(500)
        val card = ObjectAnimator.ofFloat(binding.cardUser, View.ALPHA, 1f).setDuration(200)
        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(200)
        val desc = ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(200)

        val together1 = AnimatorSet().apply { playTogether(scoreBisindo, scoreAsl) }
        val together2 = AnimatorSet().apply { playTogether(title, image) }

        AnimatorSet().apply {
            playSequentially(welcome, desc, card, together2, together1)
            startDelay = 300
        }.start()
    }

    private fun Newsrv() {
        showLoadingLinear(true)
        lifecycleScope.launch {
            ApiClient.apiNewsService.ASLNews("ASL", "en", ApiClient.API_KEY)
                .enqueue(object : Callback<NewsResponse> {
                    override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                        if (response.isSuccessful) {
                            val article = response.body()?.articles ?: emptyList()
                            val newsList = article.mapNotNull { articles ->
                                if (!articles.title.isNullOrEmpty() && !articles.urlToImage.isNullOrEmpty()) {
                                    ItemNews(articles.title, articles.urlToImage, articles.url)
                                } else {
                                    null
                                }
                            }
                            newsAdapter.submitList(newsList)
                            showLoadingLinear(false)
                        } else {
                            showLoadingLinear(false)
                            showToast("Error Loading News")
                        }
                    }

                    override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                        showLoadingLinear(false)
                    }
                })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) { // Check if binding is not null
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showLoadingLinear(isLoading: Boolean) {
        if (_binding != null) { // Check if binding is not null
            binding.linearProgressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

