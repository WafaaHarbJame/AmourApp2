package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.DeliveryTime;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowDeliveryTimesBinding;

import java.util.List;


public class DeliveryTimeAdapter extends RecyclerView.Adapter<DeliveryTimeAdapter.ViewHolder> {

    private static final String TAG = "DeliveryTimeAdapter";
    //    public int lastIndex = 0;
    public String currency = "BHD";
    Context context;
    DataCallback dataCallback;
    private List<DeliveryTime> deliveryTimesList;
    private Double deliveryFees;
    public int selectedId;

    private boolean isSelected = false;

    public DeliveryTimeAdapter(Context context, List<DeliveryTime> deliveryTimesList, Double deliveryFees, int timeId, DataCallback dataCallback) {
        this.deliveryTimesList = deliveryTimesList;
        this.context = context;
        this.dataCallback = dataCallback;
        this.deliveryFees = deliveryFees;
        this.selectedId = timeId;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        RowDeliveryTimesBinding itemView = RowDeliveryTimesBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new ViewHolder(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        DeliveryTime deliveryTimes = deliveryTimesList.get(position);
        viewHolder.binding.deliveryTime.setText(deliveryTimes.getTime());

        currency = UtilityApp.getLocalData().getCurrencyCode();

        if (deliveryFees == 0) {
            viewHolder.binding.deliveryPrice.setText(context.getString(R.string.free));

        } else {
            viewHolder.binding.deliveryPrice.setText(NumberHandler
                    .formatDouble(deliveryFees,
                            UtilityApp.getLocalData().getFractional()) + " " + currency);

        }


//        if (lastIndex == position) {
//            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
//            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//
//        } else {
//            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle_o));
//            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));
//
//        }


        if (deliveryTimes.getId() == selectedId) {
            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        } else {
            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle_o));
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));

        }


    }

    @Override
    public int getItemCount() {
        return deliveryTimesList.size();
    }

    public interface onTimeSelected {
        void onTimeSelected(DeliveryTime deliveryTime, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RowDeliveryTimesBinding binding;

        ViewHolder(RowDeliveryTimesBinding view) {
            super(view.getRoot());
            binding = view;
            itemView.setOnClickListener(view1 -> {
                DeliveryTime deliveryTime = deliveryTimesList.get(getBindingAdapterPosition());
//                lastIndex = getAdapterPosition();
                selectedId = deliveryTime.getId();
                notifyDataSetChanged();

                if (dataCallback != null) {
                    dataCallback.dataResult(deliveryTime, "result", true);
                }
            });


        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
