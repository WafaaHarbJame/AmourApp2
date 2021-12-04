package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityNoBalanceBinding


class NoBalanceActivity : ActivityBase() {
    var binding: ActivityNoBalanceBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoBalanceBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        binding!!.addBalanceBut.setOnClickListener { view1 -> }
        setTitle(R.string.add_balance)
    }

    private fun startNoBalanceActivity() {
        val intent = Intent(activity, NoBalanceActivity::class.java)
        startActivity(intent)
    }
}