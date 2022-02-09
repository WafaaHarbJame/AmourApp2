package com.ramez.shopp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.Models.AreasModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowCityBinding;

import java.util.List;

public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.ViewHolder> {
    private Activity activity;
    private List<AreasModel> list;
    private int selectedCity;
    private DataFetcherCallBack dataFetcherCallBack;

    public StatesAdapter(Activity activity, List<AreasModel> list, int  selectedCity, DataFetcherCallBack dataFetcherCallBack) {
        this.activity = activity;
        this.list = list;
        this.selectedCity = selectedCity;
        this.dataFetcherCallBack = dataFetcherCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCityBinding itemView = RowCityBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AreasModel cityModel = list.get(position);
        holder.binding.nameTxt.setText(cityModel.getStateName());


        if (selectedCity ==cityModel.getId()) {
            holder.binding.selectTxt.setText(activity.getString(R.string.fa_check));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity,R.color.green));
        } else {
            holder.binding.selectTxt.setText("");
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowCityBinding binding;

        public ViewHolder(RowCityBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(v -> {

                AreasModel cityModel = list.get(getBindingAdapterPosition());
                selectedCity =cityModel.getId();
                notifyDataSetChanged();
                if (dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(cityModel, Constants.success, true);
                }


            });

        }


    }

}