package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowKitchenBinding;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.Holder> {

    private Context context;
    private List<DinnerModel> list;
    private OnKitchenClick onKitchenClick;

    public KitchenAdapter(Context context, List<DinnerModel> list, OnKitchenClick onKitchenClick) {
        this.context = context;
        this.list = list;
        this.onKitchenClick = onKitchenClick;
        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowKitchenBinding itemView = RowKitchenBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {


        DinnerModel dinnerModel = list.get(position);
        String imageUrl=dinnerModel.getImage();


        GlobalData.PicassoImg(imageUrl, R.drawable.holder_image, holder.binding.kitchenImg);
        holder.binding.titleTV.setText(dinnerModel.getDescription());

        holder.binding.container.setOnClickListener(v -> {
            onKitchenClick.onKitchenClicked(position, dinnerModel);
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnKitchenClick {
        void onKitchenClicked(int position, DinnerModel dinnerModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowKitchenBinding binding;

        Holder(RowKitchenBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
