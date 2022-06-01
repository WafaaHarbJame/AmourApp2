package com.amour.shop.activities


import android.os.Bundle
import android.view.View
import com.amour.shop.Models.WelcomeModel
import com.amour.shop.R
import com.amour.shop.adapter.WelcomeSliderAdapter
import com.amour.shop.databinding.ActivityWelcomeBinding
import java.util.ArrayList


class WelcomeActivity : ActivityBase() {
    private lateinit var binding: ActivityWelcomeBinding
    var welcomeSliderModels: ArrayList<WelcomeModel>? = null
    private var welcomeSliderAdapter: WelcomeSliderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        welcomeSliderModels = ArrayList()
        welcomeSliderModels?.add(
            WelcomeModel(
                getString(R.string.dummy2),
                R.drawable.screen1,
                getString(R.string.string_menu_home)
            )
        )
        welcomeSliderModels?.add(
            WelcomeModel(
                getString(R.string.dummy2),
                R.drawable.screen4,
                getString(R.string.categories)
            )
        )
        welcomeSliderModels?.add(
            WelcomeModel(
                getString(R.string.dummy2),
                R.drawable.screen3,
                getString(R.string.offer_text)
            )
        )
        welcomeSliderAdapter = WelcomeSliderAdapter(this, welcomeSliderModels)
        binding.viewPager.adapter = welcomeSliderAdapter
        binding.nextBut.setOnClickListener {
            binding.viewPager.setCurrentItem(getItem(+1), true)
            if (binding.viewPager.adapter?.count == binding.viewPager.currentItem + 1) {
                navigateChooseCityActivity()
            }
        }
        binding.skipBtn.setOnClickListener { navigateChooseCityActivity() }
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    fun navigateChooseCityActivity() {
//        startActivity(new Intent(WelcomeActivity.this, ChooseCityActivity.class));
//        finish();
    }
}