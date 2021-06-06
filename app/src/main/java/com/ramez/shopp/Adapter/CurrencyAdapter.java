package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.CurrencyModel;
import com.ramez.shopp.Models.LanguageModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowLangItemBinding;

import java.util.ArrayList;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private Context context;
    private OnCurrencyClick onLangClick;
    private ArrayList<CurrencyModel> list;
    private int selectedCurrency = 0;

    public CurrencyAdapter(Context context, ArrayList<CurrencyModel> list, OnCurrencyClick onCurrencyClick,int selectedCurrency) {
        this.context = context;
        this.onLangClick = onCurrencyClick;
        this.list = list;
        this.selectedCurrency=selectedCurrency;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowLangItemBinding itemView = RowLangItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CurrencyModel currencyModel = list.get(position);
        holder.binding.nameTxt.setText(currencyModel.getName().trim());


        if (currencyModel.getId() == selectedCurrency) {
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        RowLangItemBinding binding;

        ViewHolder(RowLangItemBinding view) {
            super(view.getRoot());
            binding = view;



            itemView.setOnClickListener(v -> {
                onLangClick.onCurrencyClicked(getBindingAdapterPosition(), list.get(getBindingAdapterPosition()));
                selectedCurrency = list.get(getBindingAdapterPosition()).getId();
                notifyDataSetChanged();
            });

        }



    }

    public interface OnCurrencyClick {
        void onCurrencyClicked(int position, CurrencyModel currencyModel);
    }
}
