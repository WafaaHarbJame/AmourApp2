package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowBannersItemBinding;

import java.util.List;

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.Holder> {

    private Context context;
    private List<Slider> list;
    private OnBannersClick onBannersClick;

    public BannersAdapter(Context context, List<Slider> list, OnBannersClick onBannersClick) {
        this.context = context;
        this.list = list;
        this.onBannersClick = onBannersClick;
        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowBannersItemBinding itemView = RowBannersItemBinding.inflate(LayoutInflater.from(context), parent, false);

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

        try {
            GlobalData.INSTANCE.GlideImgGif(context,imageUrl,R.drawable.holder_image,holder.binding.ivCatImage);

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.binding.container.setOnClickListener(v -> {
            onBannersClick.onBannersClicked(position, slider);
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnBannersClick {
        void onBannersClicked(int position, Slider slider);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowBannersItemBinding binding;

        Holder(RowBannersItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
