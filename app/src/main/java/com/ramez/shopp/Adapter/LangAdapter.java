package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.LanguageModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowLangItemBinding;

import java.util.ArrayList;

public class LangAdapter extends RecyclerView.Adapter<LangAdapter.CountryViewHolder> {
    private Context context;
    private OnLangClick onLangClick;
    private ArrayList<LanguageModel> langList;
    private int selectedPosition = 0;

    public LangAdapter(Context context, ArrayList<LanguageModel> list, OnLangClick onLangClick,int selectedPosition) {
        this.context = context;
        this.onLangClick = onLangClick;
        this.langList = list;
        this.selectedPosition=selectedPosition;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowLangItemBinding itemView = RowLangItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        LanguageModel languageModel = langList.get(position);

        holder.binding.nameTxt.setText(languageModel.getName().trim());

        if (languageModel.getId() == selectedPosition) {
            holder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        }

        else {
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
        return langList != null ? langList.size() : 0;
    }


    public class CountryViewHolder extends RecyclerView.ViewHolder {

        RowLangItemBinding binding;

        CountryViewHolder(RowLangItemBinding view) {
            super(view.getRoot());
            binding = view;


            itemView.setOnClickListener(v -> {
                onLangClick.onLangClicked(getBindingAdapterPosition(), langList.get(getBindingAdapterPosition()));
                selectedPosition = langList.get(getBindingAdapterPosition()).getId();
                notifyDataSetChanged();
            });

        }



    }

    public interface OnLangClick {
        void onLangClicked(int position,LanguageModel languageModel);
    }
}
