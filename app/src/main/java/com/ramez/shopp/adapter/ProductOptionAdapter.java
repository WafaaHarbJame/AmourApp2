package com.ramez.shopp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Models.ProductBarcode;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowProductOptionBinding;

import java.util.List;

public class ProductOptionAdapter extends RecyclerView.Adapter<ProductOptionAdapter.MyHolder> {

    public List<ProductBarcode> productBarcodes;
    public Context context;
    public LayoutInflater inflater;
    public int selectedIndex = -1;
    DataCallback dataCallback;
    private int lastIndex = 0;


    public ProductOptionAdapter(Context context, List<ProductBarcode> productBarcodes, DataCallback dataCallback) {
        this.productBarcodes = productBarcodes;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dataCallback = dataCallback;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowProductOptionBinding view = RowProductOptionBinding.inflate(LayoutInflater.from(context), parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        ProductBarcode productOptionModel = productBarcodes.get(position);
        String wightName = productOptionModel.getProductUnits().getUnitName();
        //NumberHandler.formatDouble(productOptionModel.getWeight(), 0)
        //                + " " +
        holder.binding.btnCategory.setText(wightName);


        if (lastIndex == position) {

            holder.binding.btnCategory.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_green));
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.green3));


        } else {
            holder.binding.btnCategory.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_grey));
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.black));


        }


    }


    @Override
    public int getItemCount() {
        return productBarcodes.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RowProductOptionBinding binding;

        public MyHolder(RowProductOptionBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.btnCategory.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                selectedIndex = position;
                lastIndex = position;
                ProductBarcode optionModel = productBarcodes.get(position);
                Log.i("tag", "Log 1" + optionModel.getId());
                notifyDataSetChanged();
                if (dataCallback != null) {
                    dataCallback.dataResult(optionModel, "success", true);
                }
            });


        }


    }


}



