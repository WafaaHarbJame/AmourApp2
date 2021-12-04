package com.ramez.shopp.activities


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.R
import com.ramez.shopp.adapter.RegisterLoginAdapter
import com.ramez.shopp.databinding.ActivityRegisteLoginBinding


class RegisterLoginActivity : ActivityBase() {
    private lateinit var binding: ActivityRegisteLoginBinding
    private var adapter: RegisterLoginAdapter? = null
    private var login = false
    private var register = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisteLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.toolBar.backBtn.visibility = View.GONE
        intentExtra
        adapter = RegisterLoginAdapter(activity, supportFragmentManager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.viewPager.adapter = adapter
        binding.tabs.setSelectedTabIndicatorColor(Color.WHITE)
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(activity, R.color.colorAccent))
        if (login) {
            binding.viewPager.currentItem = 1
        } else {
            binding.viewPager.currentItem = 0
        }
    }

    private val intentExtra: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                login = intent.getBooleanExtra(Constants.LOGIN, false)
                register = intent.getBooleanExtra(Constants.REGISTER, false)
            }
        }
}