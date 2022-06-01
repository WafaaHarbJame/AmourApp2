package com.amour.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Models.CouponsModel;
import com.amour.shop.Models.LocalModel;
import com.amour.shop.databinding.RowCouponsItemBinding;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.Holder> {

    private Context context;
    private List<CouponsModel> couponsModelList;
    private OnItemClick onItemClick;
    private int limit = 6;
    private boolean isHoriz;
    private LocalModel localModel;
    String currency="BHD";


    public CouponsAdapter(Context context, List<CouponsModel> couponsModelList, int limit, OnItemClick onItemClick) {
        this.context = context;
        this.couponsModelList = couponsModelList;
        this.onItemClick = onItemClick;
        this.limit = limit;
        UtilityApp.INSTANCE.getLocalData();
        localModel = UtilityApp.INSTANCE.getLocalData();

        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowCouponsItemBinding itemView = RowCouponsItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        CouponsModel couponsModel = couponsModelList.get(position);
         currency=localModel.getCurrencyCode();

        holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,couponsModel));
        holder.binding.barcodeView.setBarcodeText(couponsModel.coupon_code);
        holder.binding.textCouponCode.setText(couponsModel.coupon_code);
        holder.binding.textCouponPrice.setText(couponsModel.value+" "+currency);

    }



    @Override
    public int getItemCount() {

        if (limit != 0) return Math.min(couponsModelList.size(), limit);
        else return couponsModelList.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,CouponsModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowCouponsItemBinding binding;

        Holder(RowCouponsItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }


}
