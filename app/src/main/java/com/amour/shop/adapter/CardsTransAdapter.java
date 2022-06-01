package com.amour.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amour.shop.classes.Constants;
import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Models.LocalModel;
import com.amour.shop.Models.TransactionModel;
import com.amour.shop.Utils.DateHandler;
import com.amour.shop.databinding.RowCardTransItemBinding;

import java.util.List;

public class CardsTransAdapter extends RecyclerView.Adapter<CardsTransAdapter.Holder> {

    private Context context;
    private List<TransactionModel> objectModelList;
    private OnItemClick onItemClick;
    private int limit = 6;
    private boolean isHoriz;
    String lang;
    private LocalModel localModel;
    String currency="BHD";


    public CardsTransAdapter(Context context, List<TransactionModel> list, int limit, OnItemClick onItemClick) {
        this.context = context;
        this.objectModelList = list;
        this.onItemClick = onItemClick;
        this.limit = limit;
        UtilityApp.INSTANCE.getLocalData();
        localModel = UtilityApp.INSTANCE.getLocalData();



    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowCardTransItemBinding itemView = RowCardTransItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        TransactionModel transactionModel = objectModelList.get(position);
        if (UtilityApp.INSTANCE.getLanguage() == null) {
            lang = Constants.English;
        } else {
            lang = UtilityApp.INSTANCE.getLanguage();

        }

        String billDate = (DateHandler.INSTANCE.FormatDate4(transactionModel.billDate,
                "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", lang));
        currency=localModel.getCurrencyCode();

        holder.binding.dateTv.setText(billDate);
        holder.binding.billAmtTv.setText(String.valueOf(transactionModel.billAmt));
        holder.binding.currentPointsTv.setText(String.valueOf(transactionModel.currentPoints));
        holder.binding.currencyTv.setText(currency);

        holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,transactionModel));

    }



    @Override
    public int getItemCount() {

        if (limit != 0) return Math.min(objectModelList.size(), limit);
        else return objectModelList.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,TransactionModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowCardTransItemBinding binding;

        Holder(RowCardTransItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }


}
