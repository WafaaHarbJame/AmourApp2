package com.ramez.shopp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Models.ProductChecker;
import com.ramez.shopp.Models.DeliveryTime;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowProductCheckBinding;

import java.util.List;


public class ProductCheckAdapter extends RecyclerView.Adapter<ProductCheckAdapter.ViewHolder> {

    private static final String TAG = "DeliveryTimeAdapter";
//    public int lastIndex = 0;
    Context context;
    DataCallback dataCallback;
    private List<ProductChecker> list;
    public int selectedPosition;
    private boolean isSelected = false;
    private RadioButton lastCheckedRB = null;

    public ProductCheckAdapter(Context context, List<ProductChecker> checkerList, int selectedPosition, DataCallback dataCallback) {
        this.list = checkerList;
        this.context = context;
        this.dataCallback = dataCallback;
        this.selectedPosition=selectedPosition;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

        RowProductCheckBinding itemView = RowProductCheckBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new ViewHolder(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        ProductChecker productChecker = list.get(position);
        viewHolder.binding.productCheckTxt.setText(productChecker.getName());
        viewHolder.binding.selectTxt.setOnCheckedChangeListener(null);

        if (selectedPosition==productChecker.getId() ) {
            viewHolder.binding.selectTxt.setChecked(true);
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        } else {
            viewHolder.binding.selectTxt.setChecked(false);
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));

        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        RowProductCheckBinding binding;

        @SuppressLint("NotifyDataSetChanged")
        ViewHolder(RowProductCheckBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(view1 -> {
                ProductChecker deliveryTime = list.get(getBindingAdapterPosition());
                selectedPosition = deliveryTime.getId();
                notifyDataSetChanged();

                if (dataCallback != null) {
                    dataCallback.dataResult(deliveryTime, "result", true);
                }
            });

            binding.selectTxt.setOnClickListener(v -> {
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB =   binding.selectTxt;
                ProductChecker deliveryTime = list.get(getBindingAdapterPosition());
                selectedPosition = deliveryTime.getId();
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
