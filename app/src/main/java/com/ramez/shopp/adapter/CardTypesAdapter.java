package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.CardModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowLangItemBinding;

import java.util.ArrayList;

public class CardTypesAdapter extends RecyclerView.Adapter<CardTypesAdapter.CountryViewHolder> {
    private Context context;
    private OnCardTypeClick onCardTypeClick;
    private ArrayList<CardModel> list;
    private int lastIndex = 0;

    public CardTypesAdapter(Context context, ArrayList<CardModel> list, OnCardTypeClick onCardTypeClick) {
        this.context = context;
        this.onCardTypeClick = onCardTypeClick;
        this.list = list;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowLangItemBinding itemView = RowLangItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        CardModel cardModel = list.get(position);

        holder.binding.nameTxt.setText(cardModel.getName().trim());


        if (lastIndex == position) {
            holder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        } else {
            holder.binding.selectTxt.setText(context.getString(R.string.fa_circle_o));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));
        }



        if (position == getItemCount() - 1) {
            holder.binding.divider.setVisibility(View.GONE);
        } else {
            holder.binding.divider.setVisibility(View.VISIBLE);
        }





    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public class CountryViewHolder extends RecyclerView.ViewHolder {

        RowLangItemBinding binding;

        CountryViewHolder(RowLangItemBinding view) {
            super(view.getRoot());
            binding = view;


            itemView.setOnClickListener(v -> {
                onCardTypeClick.OnCardTypeClicked(getBindingAdapterPosition(), list.get(getBindingAdapterPosition()));
                lastIndex = getBindingAdapterPosition();
                notifyDataSetChanged();
            });

        }



    }

    public interface OnCardTypeClick {
        void OnCardTypeClicked(int position,CardModel cardModel);
    }

}
