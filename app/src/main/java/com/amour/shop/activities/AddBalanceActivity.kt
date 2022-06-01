package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amour.shop.R
import com.amour.shop.databinding.ActivityAddBalanceBinding


class AddBalanceActivity : ActivityBase() {
    lateinit var binding: ActivityAddBalanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBalanceBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.toolBar.backBtn.setOnClickListener { onBackPressed() }
        binding.followBut.setOnClickListener { startNoBalanceActivity() }
        setTitle(R.string.add_balance)
    }

    private fun startNoBalanceActivity() {
        val intent = Intent(activity, NoBalanceActivity::class.java)
        startActivity(intent)
    }
}