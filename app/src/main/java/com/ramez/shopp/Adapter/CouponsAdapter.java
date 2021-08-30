package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.databinding.RowCategoryBinding;
import com.ramez.shopp.databinding.RowCouponsItemBinding;

import java.util.List;
import java.util.Locale;

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
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);

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
