package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.ramez.shopp.Adapter.CardTypesAdapter;
import com.ramez.shopp.Models.CardModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityAddCardBinding;

import java.util.ArrayList;

public class AddCardActivity extends ActivityBase  implements  CardTypesAdapter.OnCardTypeClick {
    ActivityAddCardBinding binding;

    ArrayList<CardModel> list;
    private CardTypesAdapter cardTypesAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        list=new ArrayList<>();
        list.add(new CardModel(1,"فيزا","visa"));
        list.add(new CardModel(1,"ماستر","master"));

       // binding.toolBar.mainTitleTxt.setText(R.string.add_card);
        binding.toolBar.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });

        linearLayoutManager=new LinearLayoutManager(getActiviy());
        binding.recycler.setLayoutManager(linearLayoutManager);

        binding.chooseCardTv.setOnClickListener(view1 -> {
            binding.cardContainer.setVisibility(View.VISIBLE);
            binding.chooseCardTv.setVisibility(View.GONE);

        });

        binding.saveBut.setOnClickListener(view1 -> {
            startAddCardActivity();

        });

        initAdapter();


    }

    @Override
    public void OnCardTypeClicked(int position, CardModel cardModel) {

    }
    public void initAdapter () {

        cardTypesAdapter = new CardTypesAdapter(getActiviy(), list, this);
        binding.recycler.setAdapter(cardTypesAdapter);

    }
    private void startAddCardActivity(){
        Intent intent=new Intent(getActiviy(), AddBalanceActivity.class);
        startActivity(intent);
    }
}