package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowKitchenBinding;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.Holder> {

    private Context context;
    private List<Slider> list;
    private OnKitchenClick onKitchenClick;

    public KitchenAdapter(Context context, List<Slider> list, OnKitchenClick onKitchenClick) {
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


        Slider slider = list.get(position);
        String imageUrl;

        if (UtilityApp.getLanguage().equals(Constants.English)) {
            imageUrl = slider.getImage();

        } else {
            imageUrl = slider.getImage2();

        }


        GlobalData.PicassoImg(imageUrl, R.drawable.holder_image, holder.binding.kitchenImg);

        holder.binding.container.setOnClickListener(v -> {
            onKitchenClick.onKitchenClicked(position, slider);
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnKitchenClick {
        void onKitchenClicked(int position, Slider slider);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowKitchenBinding binding;

        Holder(RowKitchenBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
