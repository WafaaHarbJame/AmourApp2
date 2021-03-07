package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowKitchenGridBinding;
import com.ramez.shopp.databinding.RowKitchenLinearBinding;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DinnerModel> list;
    private OnKitchenClick onKitchenClick;
    private boolean isGrid;

    public KitchenAdapter(Context context, List<DinnerModel> list, OnKitchenClick onKitchenClick, boolean isGrid) {
        this.context = context;
        this.list = list;
        this.onKitchenClick = onKitchenClick;
        this.isGrid = isGrid;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (isGrid) {
            RowKitchenGridBinding itemView = RowKitchenGridBinding.inflate(LayoutInflater.from(context), parent, false);
            return new HolderGrid(itemView);
        } else {
            RowKitchenLinearBinding itemView = RowKitchenLinearBinding.inflate(LayoutInflater.from(context), parent, false);
            return new HolderLinear(itemView);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        DinnerModel dinnerModel = list.get(position);
        String imageUrl = dinnerModel.getImage();

        if (holder instanceof HolderLinear) {
            HolderLinear holderLinear = (HolderLinear) holder;
            GlobalData.PicassoImg(imageUrl, R.drawable.holder_image, holderLinear.binding.kitchenImg);
            holderLinear.binding.titleTV.setText(dinnerModel.getDescription());

            holderLinear.binding.container.setOnClickListener(v -> {
                onKitchenClick.onKitchenClicked(position, dinnerModel);
            });

        } else if (holder instanceof HolderGrid) {
            HolderGrid holderLinear = (HolderGrid) holder;
            GlobalData.PicassoImg(imageUrl, R.drawable.holder_image, holderLinear.binding.kitchenImg);
            holderLinear.binding.titleTV.setText(dinnerModel.getDescription());

            holderLinear.binding.container.setOnClickListener(v -> {
                onKitchenClick.onKitchenClicked(position, dinnerModel);
            });

        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnKitchenClick {
        void onKitchenClicked(int position, DinnerModel dinnerModel);
    }

    static class HolderLinear extends RecyclerView.ViewHolder {

        RowKitchenLinearBinding binding;

        HolderLinear(RowKitchenLinearBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }

    static class HolderGrid extends RecyclerView.ViewHolder {

        RowKitchenGridBinding binding;

        HolderGrid(RowKitchenGridBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
