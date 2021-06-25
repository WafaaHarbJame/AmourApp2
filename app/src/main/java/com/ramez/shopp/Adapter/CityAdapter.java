package com.ramez.shopp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CityModel;
import com.ramez.shopp.Models.LanguageModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowItemCitiesBinding;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private Context context;
    private OnCityClick onCityClick;
    private ArrayList<CityModel> list;
    private int selectedPosition = 0;

    public CityAdapter(Context context, ArrayList<CityModel> list, int selectedPosition, OnCityClick onCityClick) {
        this.context = context;
        this.onCityClick = onCityClick;
        this.list = list;
        this.selectedPosition = selectedPosition;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowItemCitiesBinding itemView = RowItemCitiesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CityModel languageModel = list.get(position);

        holder.binding.nameTxt.setText(languageModel.getCityName());

//        String lang = UtilityApp.getLanguage();
//        if (lang.equals(Constants.Arabic)) {
//            if (languageModel.getNameAr() == null || languageModel.getNameAr().isEmpty()) {
//
//            } else {
//                holder.binding.nameTxt.setText(languageModel.getNameAr());
//
//            }
//        } else {
//            if (languageModel.getName() == null || languageModel.getName().isEmpty()) {
//                holder.binding.nameTxt.setText(languageModel.getNameAr());
//
//            } else {
//                holder.binding.nameTxt.setText(languageModel.getName());
//
//            }
//        }
//

        if (selectedPosition == languageModel.getId()) {
            holder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
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

        RowItemCitiesBinding binding;

        ViewHolder(RowItemCitiesBinding view) {
            super(view.getRoot());
            binding = view;


            itemView.setOnClickListener(v -> {
                onCityClick.onCityClicked(getBindingAdapterPosition(), list.get(getBindingAdapterPosition()));
                selectedPosition = list.get(getBindingAdapterPosition()).getId();
                notifyDataSetChanged();
            });

        }


    }

    public interface OnCityClick {
        void onCityClicked(int position, CityModel cityModel);
    }


}
