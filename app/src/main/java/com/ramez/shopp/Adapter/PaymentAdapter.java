package com.ramez.shopp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Models.PaymentModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowPaymentTypeBinding;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyHolder> {

    public List<PaymentModel> paymentMethods;
    public Context context;
    public LayoutInflater inflater;
    public int selectedIndex;
    DataCallback dataCallback;


    public PaymentAdapter(Context context, List<PaymentModel> paymentMethods, DataCallback dataCallback) {
        this.paymentMethods = paymentMethods;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dataCallback = dataCallback;
//        this.selectedIndex=selectedIndex;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowPaymentTypeBinding view = RowPaymentTypeBinding.inflate(LayoutInflater.from(context), parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        PaymentModel paymentMethod = paymentMethods.get(position);
        holder.binding.paymentTv.setText(paymentMethod.getName());
        holder.binding.paymentIv.setImageResource(paymentMethod.getImage());


        if (selectedIndex == paymentMethod.getId()) {

            holder.binding.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_medium_corner_red));
            holder.binding.paymentTv.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.paymentIv.setColorFilter(ContextCompat.getColor(context, R.color.white));


        } else {
            holder.binding.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_medium_corner_unselected));
            holder.binding.paymentTv.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.binding.paymentIv.setColorFilter(ContextCompat.getColor(context, R.color.black));


        }


        holder.binding.cardView.setOnClickListener(v -> {
            selectedIndex = paymentMethod.getId();
            Log.i("tag", "Log 1" + paymentMethod.getId());
            notifyDataSetChanged();
            if (dataCallback != null) {
                dataCallback.dataResult(paymentMethod, "success", true);
            }
        });


    }


    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RowPaymentTypeBinding binding;

        public MyHolder(RowPaymentTypeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;


        }


    }


}



