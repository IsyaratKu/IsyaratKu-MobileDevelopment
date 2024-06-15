package com.isyaratku.app.ui.main.leaderboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.isyaratku.app.ui.main.leaderboard.asl.AslFragment
import com.isyaratku.app.ui.main.leaderboard.bisindo.BisindoFragment

class SectionsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2 // Replace with the actual number of fragments you want to display
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = AslFragment()
            1 -> fragment = BisindoFragment()
            // Add more cases for additional fragments
        }
        return fragment as Fragment // Ensure fragment is not null
    }
}