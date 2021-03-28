package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Models.ProductOptionModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowProductOptionBinding;

import java.util.List;

public class ProductOptionAdapter extends RecyclerView.Adapter<ProductOptionAdapter.MyHolder> {

    public List<ProductOptionModel> optionModels;
    public Context context;
    public LayoutInflater inflater;
    public int selectedIndex = -1;
    DataCallback dataCallback;
    private int lastIndex = 0;


    public ProductOptionAdapter(Context context, List<ProductOptionModel> paymentMethods, DataCallback dataCallback) {
        this.optionModels = paymentMethods;
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

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        ProductOptionModel productOptionModel = optionModels.get(position);
        holder.binding.btnCategory.setText(productOptionModel.getName());


        if (lastIndex == position) {

            holder.binding.btnCategory.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_green));
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.green3));


        } else {
            holder.binding.btnCategory.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner_grey));
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.black));


        }


//        holder.binding.cardView.setOnClickListener(v -> {
//            selectedIndex = position;
//            lastIndex = position;
//            Log.i("tag", "Log 1" + paymentMethod.getId());
//            notifyDataSetChanged();
//            if (dataCallback != null) {
//                dataCallback.dataResult(paymentMethod, "success", true);
//            }
//        });


    }


    @Override
    public int getItemCount() {
        return optionModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RowProductOptionBinding binding;

        public MyHolder(RowProductOptionBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;


        }


    }


}



