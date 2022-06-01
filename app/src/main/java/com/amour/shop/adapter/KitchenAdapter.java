package com.amour.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amour.shop.classes.GlobalData;
import com.amour.shop.Models.DinnerModel;
import com.amour.shop.R;
import com.amour.shop.databinding.RowKitchenGridBinding;
import com.amour.shop.databinding.RowKitchenLinearBinding;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DinnerModel> list;
    private OnKitchenClick onKitchenClick;
    private boolean isGrid;
    private int limit;

    public KitchenAdapter(Context context, List<DinnerModel> list, OnKitchenClick onKitchenClick, boolean isGrid,int limit) {
        this.context = context;
        this.list = list;
        this.onKitchenClick = onKitchenClick;
        this.isGrid = isGrid;
        this.limit=limit;

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
            try {
                GlobalData.INSTANCE.GlideImg(context,imageUrl, R.drawable.holder_image, holderLinear.binding.kitchenImg);

            } catch (Exception e) {
                e.printStackTrace();
            }

            holderLinear.binding.titleTV.setText(dinnerModel.getDescription());

            holderLinear.binding.container.setOnClickListener(v -> {
                onKitchenClick.onKitchenClicked(position, dinnerModel);
            });

        } else if (holder instanceof HolderGrid) {
            HolderGrid holderLinear = (HolderGrid) holder;
            try {
                GlobalData.INSTANCE.GlideImg(context,imageUrl, R.drawable.holder_image, holderLinear.binding.kitchenImg);

            } catch (Exception e) {
                e.printStackTrace();
            }

            holderLinear.binding.titleTV.setText(dinnerModel.getDescription());

            holderLinear.binding.container.setOnClickListener(v -> {
                onKitchenClick.onKitchenClicked(position, dinnerModel);
            });

        }

    }


    @Override
    public int getItemCount() {
        if (limit != 0) return Math.min(list.size(), limit);
        else return list.size();

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
