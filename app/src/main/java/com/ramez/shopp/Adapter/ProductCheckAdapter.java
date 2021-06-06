package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.ProductChecker;
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




//        if (lastIndex == position) {
//            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
//            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//
//        } else {
//            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle_o));
//            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));
//
//        }


        if (productChecker.getId() == selectedPosition) {
            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle));
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        } else {
            viewHolder.binding.selectTxt.setText(context.getString(R.string.fa_circle_o));
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3));

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface onTimeSelected {
        void onTimeSelected(DeliveryTime deliveryTime, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RowProductCheckBinding binding;

        ViewHolder(RowProductCheckBinding view) {
            super(view.getRoot());
            binding = view;
            itemView.setOnClickListener(view1 -> {
                ProductChecker deliveryTime = list.get(getBindingAdapterPosition());
//                lastIndex = getBindingAdapterPosition();
                notifyDataSetChanged();
                selectedPosition = deliveryTime.getId();


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
