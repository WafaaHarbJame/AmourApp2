package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ramez.shopp.Classes.MessageEvent
import com.ramez.shopp.fragments.CardFragment
import com.ramez.shopp.fragments.CouponsFragment
import com.ramez.shopp.MainActivity
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityRewardsBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
            ContextCompat.getColor(activiy, R.color.black),
            ContextCompat.getColor(activiy, R.color.black)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.type == MessageEvent.TYPE_main) {
            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
            val intent = Intent(activiy, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}