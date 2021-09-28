package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.CityModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowItemBranchBinding;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {
    private Context context;
    private OnCityClick onCityClick;
    private ArrayList<CityModel> list;
    private int selectedPosition = 0;

    public BranchAdapter(Context context, ArrayList<CityModel> list, int selectedPosition, OnCityClick onClick) {
        this.context = context;
        this.onCityClick = onClick;
        this.list = list;
        this.selectedPosition = selectedPosition;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowItemBranchBinding itemView = RowItemBranchBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CityModel cityModel = list.get(position);

        holder.binding.branchTv.setText(cityModel.getCityName());


        if (selectedPosition == cityModel.getId()) {
            holder.binding.bgLy.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_light_red_selected));
        } else {
            holder.binding.bgLy.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_light_red));
        }

        holder.binding.rowLY.setOnClickListener(v -> {
            {
                onCityClick.onCityClicked(selectedPosition, cityModel);
                selectedPosition = cityModel.getId();
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RowItemBranchBinding binding;

        ViewHolder(RowItemBranchBinding view) {
            super(view.getRoot());
            binding = view;

            binding.rowLY.setOnClickListener(v -> {
                if(list!=null&&getBindingAdapterPosition()>=0){
                    CityModel selectedCityModel = list.get(getBindingAdapterPosition());
//                if (selectedPosition != getBindingAdapterPosition()) {
//                    selectedPosition = cityModel.getId();
//                    notifyDataSetChanged();
//                }
                    onCityClick.onCityClicked(selectedPosition, selectedCityModel);
                    notifyDataSetChanged();


                }

            });


        }


    }

    public interface OnCityClick {
        void onCityClicked(int position, CityModel cityModel);
    }


}
