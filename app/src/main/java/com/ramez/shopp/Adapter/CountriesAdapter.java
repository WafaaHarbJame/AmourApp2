package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowCountryCodeBinding;
import com.ramez.shopp.databinding.RowItemCitiesBinding;
import com.ramez.shopp.databinding.RowItemCityBinding;

import java.util.ArrayList;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountryViewHolder> {
    private Context context;
    private OnCountryClick onItemClick;
    private ArrayList<CountryModel> countryModels;
    private int selectedPosition = 0;

    public CountriesAdapter(Context context, OnCountryClick onItemClick, ArrayList<CountryModel> countryModels, int selectedPosition) {
        this.context = context;
        this.onItemClick = onItemClick;
        this.countryModels = countryModels;
        this.selectedPosition=selectedPosition;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowItemCityBinding itemView = RowItemCityBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        CountryModel countryModel = countryModels.get(position);
            holder.binding.textCountry.setText(countryModel.getCountryName());
            holder.binding.imgFlag.setImageResource(countryModel.getFlag());

        if (selectedPosition==countryModel.getId()) {
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
        return countryModels != null ? countryModels.size() : 0;
    }


    public class CountryViewHolder extends RecyclerView.ViewHolder {

        RowItemCityBinding binding;

        CountryViewHolder(RowItemCityBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(v -> {
                onItemClick.onCountryClicked(getAdapterPosition(), countryModels.get(getAdapterPosition()));
                selectedPosition = countryModels.get(getAdapterPosition()).getId();
                notifyDataSetChanged();
            });

        }


    }

    public interface OnCountryClick {
        void onCountryClicked(int position, CountryModel countryModel);
    }
}
