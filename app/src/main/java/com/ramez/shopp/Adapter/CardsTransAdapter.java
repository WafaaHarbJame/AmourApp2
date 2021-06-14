package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.Models.TransactionModel;
import com.ramez.shopp.databinding.RowCardTransItemBinding;
import com.ramez.shopp.databinding.RowCouponsItemBinding;

import java.util.List;

public class CardsTransAdapter extends RecyclerView.Adapter<CardsTransAdapter.Holder> {

    private Context context;
    private List<TransactionModel> objectModelList;
    private OnItemClick onItemClick;
    private int limit = 6;
    private boolean isHoriz;


    public CardsTransAdapter(Context context, List<TransactionModel> list, int limit, OnItemClick onItemClick) {
        this.context = context;
        this.objectModelList = list;
        this.onItemClick = onItemClick;
        this.limit = limit;

        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowCardTransItemBinding itemView = RowCardTransItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        TransactionModel couponsModel = objectModelList.get(position);

      //  holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,couponsModel));

    }



    @Override
    public int getItemCount() {

        if (limit != 0) return Math.min(objectModelList.size(), limit);
        else return objectModelList.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,CategoryModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowCardTransItemBinding binding;

        Holder(RowCardTransItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }


}
