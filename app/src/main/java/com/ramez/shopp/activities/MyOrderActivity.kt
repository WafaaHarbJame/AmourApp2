package com.ramez.shopp.activities


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityMyOrderBinding
import com.ramez.shopp.fragments.CurrentOrderFragment
import com.ramez.shopp.fragments.PastOrderFragment


class MyOrderActivity : ActivityBase() {
    lateinit var binding: ActivityMyOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.my_order)
        binding.viewPager.adapter = MyViewPagerAdapter(supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.setTabTextColors(
            ContextCompat.getColor(activity, R.color.black),
            ContextCompat.getColor(activity, R.color.white)
        )
    }

    internal inner class MyViewPagerAdapter(fm: FragmentManager?) :
            FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    CurrentOrderFragment()
                }
                1 -> {
                    PastOrderFragment()
                }
                else -> CurrentOrderFragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.current_orders) else if (position == 1) getString(R.string.complete_request) else ""
        }
    }

}