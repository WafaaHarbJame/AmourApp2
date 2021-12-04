package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.Models.CardModel
import com.ramez.shopp.adapter.CardTypesAdapter
import com.ramez.shopp.adapter.CardTypesAdapter.OnCardTypeClick
import com.ramez.shopp.databinding.ActivityAddCardBinding
import java.util.ArrayList


class AddCardActivity : ActivityBase(), OnCardTypeClick {
    lateinit var binding: ActivityAddCardBinding
    var list: ArrayList<CardModel>? = null
    private var cardTypesAdapter: CardTypesAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCardBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        list = ArrayList()
        list?.add(CardModel(1, "فيزا", "visa"))
        list?.add(CardModel(1, "ماستر", "master"))

        // binding.toolBar.mainTitleTxt.setText(R.string.add_card);
        binding.toolBar.backBtn.setOnClickListener { view1 -> onBackPressed() }
        linearLayoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = linearLayoutManager
        binding.chooseCardTv.setOnClickListener {
            binding.cardContainer.visibility = View.VISIBLE
            binding.chooseCardTv.visibility = View.GONE
        }
        binding.saveBut.setOnClickListener { startAddCardActivity() }
        initAdapter()
    }

    override fun OnCardTypeClicked(position: Int, cardModel: CardModel) {}
    fun initAdapter() {
        cardTypesAdapter = CardTypesAdapter(activity, list, this)
        binding.recycler.adapter = cardTypesAdapter
    }

    private fun startAddCardActivity() {
        val intent = Intent(activity, AddBalanceActivity::class.java)
        startActivity(intent)
    }
}