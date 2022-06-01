package com.amour.shop.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.amour.shop.fragments.CardFragment
import com.amour.shop.fragments.CouponsFragment
import com.amour.shop.R
import com.amour.shop.databinding.ActivityRewardsBinding


class RewardsActivity : ActivityBase() {
    var binding: ActivityRewardsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        title = ""
        binding!!.viewPager.adapter = MyViewPagerAdapter(supportFragmentManager)
        binding!!.tabs.setupWithViewPager(binding!!.viewPager)
        binding!!.tabs.setTabTextColors(
            ContextCompat.getColor(activity, R.color.black),
            ContextCompat.getColor(activity, R.color.black)
        )
    }

    internal inner class MyViewPagerAdapter(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CardFragment()
                1 -> CouponsFragment()
                else -> CardFragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) getString(R.string.Card) else if (position == 1) getString(R.string.Coupons) else ""
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: MessageEvent) {
//        if (event.type == MessageEvent.TYPE_main) {
//            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
//            val intent = Intent(activiy, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }
}