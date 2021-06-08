package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.databinding.RowCategoryBinding;
import com.ramez.shopp.databinding.RowCouponsItemBinding;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.Holder> {

    private Context context;
    private List<CouponsModel> couponsModelList;
    private OnItemClick onItemClick;
    private int limit = 6;
    private boolean isHoriz;


    public CouponsAdapter(Context context, List<CouponsModel> couponsModelList, int limit, OnItemClick onItemClick) {
        this.context = context;
        this.couponsModelList = couponsModelList;
        this.onItemClick = onItemClick;
        this.limit = limit;

        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowCouponsItemBinding itemView = RowCouponsItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        CouponsModel couponsModel = couponsModelList.get(position);

      //  holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,couponsModel));

    }



    @Override
    public int getItemCount() {

        if (limit != 0) return Math.min(couponsModelList.size(), limit);
        else return couponsModelList.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,CategoryModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowCouponsItemBinding binding;

        Holder(RowCouponsItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }


}
